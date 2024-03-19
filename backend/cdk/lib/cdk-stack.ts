import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as s3 from 'aws-cdk-lib/aws-s3';
import * as DynamoDB from "aws-cdk-lib/aws-dynamodb";
import * as iam from "aws-cdk-lib/aws-iam";
import * as ecr from "aws-cdk-lib/aws-ecr";
import * as events from 'aws-cdk-lib/aws-events';
import * as lambda from "aws-cdk-lib/aws-lambda";
import * as sqs from 'aws-cdk-lib/aws-sqs';
import * as lambdaEventSources from 'aws-cdk-lib/aws-lambda-event-sources';
import * as targets from 'aws-cdk-lib/aws-events-targets';


export class CdkStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const lessonBucket = new s3.Bucket(this, 'fypLessonBucket', {
      removalPolicy: cdk.RemovalPolicy.RETAIN,
    });

    const codeBucket = new s3.Bucket(this, 'fypCodeBucket', {
      removalPolicy: cdk.RemovalPolicy.RETAIN,
    });

    // User pool
    const userPool = new cdk.aws_cognito.UserPool(this, 'fypAccountsUserPool', {
      selfSignUpEnabled: true,
      signInAliases: {
        username: true,
        email: true,
      },
      autoVerify: {
        email: true,
      },
      standardAttributes: {
        email: {
          required: true,
          mutable: true,
        },
      },
      customAttributes: {
        leagueRank: new cdk.aws_cognito.StringAttribute({ mutable: true }),
        currentLeaderboardId: new cdk.aws_cognito.StringAttribute({ mutable: true }),
      },
      removalPolicy: cdk.RemovalPolicy.DESTROY,
    });

    const cognitoAccessStatement = new cdk.aws_iam.PolicyStatement({
      actions: ['cognito-idp:*'],
      resources: [userPool.userPoolArn],
      effect: iam.Effect.ALLOW
    });

    // DynamoDB
    const leaderboardsTable = new DynamoDB.Table(this, 'fypLeaderboardTable', {
      partitionKey: { name: 'id', type: DynamoDB.AttributeType.STRING },
      removalPolicy: cdk.RemovalPolicy.RETAIN,
    });

    leaderboardsTable.addGlobalSecondaryIndex({
      indexName: 'rank-index',
      partitionKey: { name: 'league_rank', type: DynamoDB.AttributeType.STRING },
    });

    const leaderboardPolicy = new cdk.aws_iam.PolicyStatement({
      actions: ['dynamodb:*'],
      resources: [leaderboardsTable.tableArn],
      effect: iam.Effect.ALLOW
    });

    // // ECS
    // const vpc = new cdk.aws_ec2.Vpc(this, 'fypLeaderVPC', {
    //       maxAzs: 2
    //   });
    //
    //   const cluster = new cdk.aws_ecs.Cluster(this, 'fypLeaderCluster', {
    //       vpc: vpc,
    //   });
    //
    //   const taskDefinition = new cdk.aws_ecs.FargateTaskDefinition(this, 'fypLeaderBoardTaskDefinition');
    //
    //   const taskDefinitionPolicy = new iam.Policy(this, 'fypLeaderTaskDefinitionPolicy', {
    //       statements: [
    //           leaderboardPolicy,
    //           cognitoAccessStatement,
    //       ]
    //   });
    //   taskDefinition.taskRole.attachInlinePolicy(taskDefinitionPolicy);
    //
    //   const repository = ecr.Repository.fromRepositoryName(
    //       this,
    //       'fyp',
    //       'fyp-repository'
    //   );
    //
    //   const container = taskDefinition.addContainer('fypContainer', {
    //       image: cdk.aws_ecs.ContainerImage.fromEcrRepository(repository, 'latest'),
    //       environment: {
    //           'LEADER_BOARD_TABLE': leaderboardsTable.tableName,
    //           'USER_POOL_ID': userPool.userPoolId,
    //       }
    //   });
    //
    //   container.addPortMappings({
    //       containerPort: 80
    //   });
    //
    //   const service = new cdk.aws_ecs.FargateService(this, 'fypService', {
    //       cluster: cluster,
    //       taskDefinition: taskDefinition,
    //   });

    // Event bridge
    const fypLeaderboardEventBridge = new events.Rule(this, 'fypLeaderboardEventBridge', {
      schedule: events.Schedule.rate(cdk.Duration.days(7)),
    });

    // SQS
    const fypUpdateLeaderboardsSQS = new sqs.Queue(this, 'fypUpdateLeaderboardsSQS');

    // Lambda to get all leaderboards
    const fypLeaderboardScan = new lambda.Function(this, 'fypLeaderboardScan', {
      runtime: lambda.Runtime.PYTHON_3_9,
      handler: 'src.index.main',
      code: lambda.Code.fromBucket(codeBucket, 'fypLeaderboardScan.zip'),
      timeout: cdk.Duration.seconds(30),
      architecture: lambda.Architecture.ARM_64,
      environment: {
        LEADER_BOARD_TABLE: leaderboardsTable.tableName,
        QUEUE_URL: fypUpdateLeaderboardsSQS.queueUrl
      }
    });
    leaderboardsTable.grantReadData(fypLeaderboardScan);
    fypUpdateLeaderboardsSQS.grantSendMessages(fypLeaderboardScan);
    fypLeaderboardEventBridge.addTarget(new targets.LambdaFunction(fypLeaderboardScan));

    // Lambda to update leaderboards and users
    const fypUpdateLeaderboards = new lambda.Function(this, 'fypUpdateLeaderboards', {
      runtime: lambda.Runtime.PYTHON_3_9,
      handler: 'src.index.main',
      code: lambda.Code.fromBucket(codeBucket, 'fypUpdateLeaderboards.zip'),
      timeout: cdk.Duration.seconds(30),
      architecture: lambda.Architecture.ARM_64,
      environment: {
        LEADER_BOARD_TABLE: leaderboardsTable.tableName,
        QUEUE_URL: fypUpdateLeaderboardsSQS.queueUrl,
        USER_POOL_ID: userPool.userPoolId
      },
    });
    fypUpdateLeaderboards.addToRolePolicy(cognitoAccessStatement);
    leaderboardsTable.grantReadWriteData(fypUpdateLeaderboards);
    fypUpdateLeaderboards.addEventSource(new lambdaEventSources.SqsEventSource(fypUpdateLeaderboardsSQS));
  }
}
