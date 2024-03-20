import os
import boto3

leaderboard_table_name = os.environ.get('LEADER_BOARD_TABLE')
spaced_repetition_table = os.environ.get('SPACED_REPETITION_TABLE')
user_pool_id = os.environ.get('USER_POOL_ID')

dynamodb_resource = boto3.resource('dynamodb')
leaderboard_table = dynamodb_resource.Table(leaderboard_table_name)
spaced_repetition_table = dynamodb_resource.Table(spaced_repetition_table)

cognito_client = boto3.client('cognito-idp')
