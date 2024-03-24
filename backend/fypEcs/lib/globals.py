import os
import boto3

region = 'eu-west-1'

leaderboard_table_name = os.environ.get('LEADER_BOARD_TABLE')
spaced_repetition_table_name = os.environ.get('SPACED_REPETITION_TABLE')
stats_table_name = os.environ.get('USER_STATS_TABLE')
user_pool_id = os.environ.get('USER_POOL_ID')

dynamodb_resource = boto3.resource('dynamodb', region_name=region)
leaderboard_table = dynamodb_resource.Table(leaderboard_table_name)
spaced_repetition_table = dynamodb_resource.Table(spaced_repetition_table_name)
stats_table = dynamodb_resource.Table(stats_table_name)

cognito_client = boto3.client('cognito-idp', region_name=region)


day1 = 82800000  # 23-hours due to aws time
day2 = 165600000
day4 = 345600000
week1 = 604800000
week2 = 1209600000
month = 2592000000
