from boto3.dynamodb.conditions import Key
from botocore.exceptions import ClientError
from fastapi import HTTPException, status

from lib.globals import (
    spaced_repetition_table
)


def get_item(s3_url: str, username: str):
    try:
        response = spaced_repetition_table.get_item(
            Key={
                's3_url': s3_url,
                'username': username
            }
        )
    except ClientError as ignore:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail='Something went wrong')
    except Exception as ignore:
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail='Something went wrong')

    if response.get('Item'):
        return {
            'last_completed': response.get('Item').get('last_completed'),
            'wait': response.get('Item').get('wait')
        }
    else:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail='Item not found')


def get_items_for_user(username: str):
    try:
        response = spaced_repetition_table.query(
            IndexName='username-index',
            KeyConditionExpression=Key('username').eq(username)
        )
    except ClientError as e:
        print(e)
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail='Something went wrong')
    except Exception as e:
        print(e)
        raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail='Something went wrong')

    items = response.get('Items', [])
    if not items:
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail='No items found for the user')

    lessons = []
    for item in items:
        lesson = {
            's3_url': item.get('s3_url'),
            'last_completed': item.get('last_completed'),
            'wait': item.get('wait')
        }
        lessons.append(lesson)

    return lessons

