
def reset_current_leaderboard(cognito_client, ordered_positions, user_pool_id):
    for position in ordered_positions:
        cognito_client.admin_update_user_attributes(
            UserPoolId=user_pool_id,
            Username=position['username'],
            UserAttributes=[
                {
                    'Name': 'custom:currentLeaderboardId',
                    'Value': 'none'
                }
            ]
        )


def get_positions(response):
    item = response['Item']
    league_rank = item['league_rank']['S']
    positions = []
    for user in item['positions']['L']:
        positions.append({
            'score': int(user['M']['score']['N']),
            'username': user['M']['username']['S']
        })
    ordered_positions = sorted(positions, key=lambda x: x['score'], reverse=True)
    return league_rank, ordered_positions


def set_new_rank(cognito_client, league_rank, position, user_pool_id, is_increase):
    username = position['username']

    if is_increase:
        new_rank = increase_rank(league_rank)
    else:
        new_rank = decrease_rank(league_rank)

    if new_rank:
        cognito_client.admin_update_user_attributes(
            UserPoolId=user_pool_id,
            Username=username,
            UserAttributes=[{
                'Name': 'custom:leagueRank',
                'Value': new_rank
            }]
        )


def increase_rank(current_rank):
    rank_mapping = {
        'bronze': 'silver',
        'silver': 'gold'
    }
    return rank_mapping.get(current_rank)


def decrease_rank(current_rank):
    rank_mapping = {
        'silver': 'bronze',
        'gold': 'silver'
    }
    return rank_mapping.get(current_rank)
