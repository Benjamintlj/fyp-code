import boto3
import os


def main(event, context):
    dynamodb = boto3.client('dynamodb')
    table_name = os.environ['USER_STATS_TABLE']

    username = event['userName']

    dynamodb.put_item(
        TableName=table_name,
        Item={
            'username': {
                'S': username,
            }
        }
    )

    return event
