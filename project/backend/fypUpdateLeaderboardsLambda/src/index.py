import boto3
import os
import json
from .utils import (
    get_positions,
    reset_current_leaderboard,
    set_new_rank
)
from .stats_handler import (
    update_first_place_stats
)


def main(event, context):
    dynamodb = boto3.client('dynamodb')
    cognito_client = boto3.client('cognito-idp')
    user_pool_id = os.environ['USER_POOL_ID']
    leaderboard_table_name = os.environ['LEADER_BOARD_TABLE']

    dynamodb_resource = boto3.resource('dynamodb')
    stats_table_name = os.environ.get('USER_STATS_TABLE')
    stats_table = dynamodb_resource.Table(stats_table_name)

    try:
        for record in event['Records']:
            body = json.loads(record['body'])
            pk = body['id']

            response = dynamodb.get_item(
                TableName=leaderboard_table_name,
                Key={'id': {'S': pk}}
            )

            if 'Item' in response:
                league_rank, ordered_positions = get_positions(response)

                if ordered_positions[0]:
                    update_first_place_stats(stats_table, ordered_positions[0]['username'])

                # top n users progress to next rank
                num_of_users_to_progress = 3
                for position in ordered_positions[:num_of_users_to_progress]:
                    try:
                        set_new_rank(cognito_client, league_rank, position, user_pool_id, True)
                    except Exception as ignore:
                        pass

                num_of_users_to_relegate = 2
                if len(ordered_positions) > (num_of_users_to_progress + num_of_users_to_relegate):
                    for position in ordered_positions[-num_of_users_to_relegate:]:
                        try:
                            set_new_rank(cognito_client, league_rank, position, user_pool_id, False)
                        except Exception as ignore:
                            pass

                reset_current_leaderboard(cognito_client, ordered_positions, user_pool_id)

            dynamodb.delete_item(
                TableName=leaderboard_table_name,
                Key={
                    'id': {'S': pk}
                }
            )

    except Exception as e:
        print(e)
