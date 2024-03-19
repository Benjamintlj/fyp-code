from fastapi import HTTPException
from starlette import status
from uuid import uuid4


from lib.globals import (cognito_client, user_pool_id, leaderboard_table)


def get_user_league_rank(username: str) -> str:
    try:
        user_info = cognito_client.admin_get_user(
            UserPoolId=user_pool_id,
            Username=username
        )

        for attr in user_info['UserAttributes']:
            if attr['Name'] == 'custom:leagueRank':
                return attr['Value']

    except cognito_client.exceptions.ClientError as e:
        print(e)
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail='No user found.')

    return ''


def get_user_leaderboard(username: str) -> str:
    try:
        user_info = cognito_client.admin_get_user(
            UserPoolId=user_pool_id,
            Username=username
        )

        for attr in user_info['UserAttributes']:
            if attr['Name'] == 'custom:currentLeaderboardId':
                return attr['Value']

    except cognito_client.exceptions.ClientError as e:
        print(e)
        raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail='No user found.')

    return ''


def create_new_leaderboard(league_rank, username):
    new_id = str(uuid4())
    new_entry = {
        'id': new_id,
        'league_rank': league_rank,
        'num_of_positions': 1,
        'positions': [
            {
                'username': username,
                'score': 0
            }
        ]
    }
    leaderboard_table.put_item(Item=new_entry)
    return new_id


def add_user_to_leaderboard(leaderboard_id, username):
    response = leaderboard_table.update_item(
        Key={'id': leaderboard_id},
        UpdateExpression='SET num_of_positions = num_of_positions + :inc, positions = list_append(positions, :new_user)',
        ExpressionAttributeValues={
            ':inc': 1,
            ':new_user': [{'username': username, 'score': 0}],
        },
        ReturnValues='UPDATED_NEW'
    )
    return response


def set_user_leaderboard_id(username, leaderboard_id):
    try:
        response = cognito_client.admin_update_user_attributes(
            UserPoolId=user_pool_id,
            Username=username,
            UserAttributes=[
                {
                    'Name': 'custom:currentLeaderboardId',
                    'Value': leaderboard_id
                },
            ]
        )
        return True

    except Exception as e:
        return False


def increase_user_score(leaderboard_id, username, score):
    response = leaderboard_table.get_item(
        Key={'id': leaderboard_id}
    )

    if 'Item' not in response:
        return None

    item = response['Item']
    positions = item.get('positions', [])
    user_index = next((i for i, position in enumerate(positions) if position['username'] == username), None)

    if user_index is None:
        return None

    update_expression = f"SET positions[{user_index}].score = positions[{user_index}].score + :increment"
    leaderboard_table.update_item(
        Key={'id': leaderboard_id},
        UpdateExpression=update_expression,
        ExpressionAttributeValues={
            ':increment': score
        }
    )

    return leaderboard_id
