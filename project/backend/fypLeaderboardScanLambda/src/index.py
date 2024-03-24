import boto3
import os
import json


def main(event, context):
    dynamodb = boto3.client('dynamodb')
    sqs = boto3.client('sqs')
    table_name = os.environ['LEADER_BOARD_TABLE']
    queue_url = os.environ['QUEUE_URL']

    response = dynamodb.scan(TableName=table_name)

    if 'Items' in response:
        for item in response['Items']:
            pk = item['id']['S']

            message = {
                'id': pk
            }

            sqs.send_message(
                QueueUrl=queue_url,
                MessageBody=json.dumps(message)
            )
