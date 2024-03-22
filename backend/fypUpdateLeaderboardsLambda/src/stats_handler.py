from enum import Enum


class Rank(Enum):
    BRONZE = 1
    SILVER = 2
    GOLD = 3


def update_first_place_stats(stats_table, username):
    try:
        dynamo_response = stats_table.get_item(Key={'username': username})
        statistics = dynamo_response.get('Item')

        if statistics and 'first_place' in statistics:
            first_place_stats = statistics['first_place']
            first_place_completed = first_place_stats.get('num_of_first_place', 0) + 1
            rank_changed = first_place_stats.get('rank_changed', False)
            current_rank = Rank[
                first_place_stats['first_place_rank'].upper()] if 'first_place_rank' in first_place_stats and \
                                                                  first_place_stats['first_place_rank'] else None
            if first_place_completed == 1:
                new_rank = Rank.BRONZE
                rank_changed = True
            elif first_place_completed == 3:
                new_rank = Rank.SILVER
                rank_changed = True
            elif first_place_completed == 10:
                new_rank = Rank.GOLD
                rank_changed = True
            else:
                new_rank = current_rank

            if rank_changed:
                rank = new_rank.name.lower()
            else:
                rank = current_rank.name.lower() if current_rank else ''

            stats_table.update_item(
                Key={'username': username},
                UpdateExpression='SET first_place.num_of_first_place = :num_of_first_place, '
                                 'first_place.first_place_rank = :first_place_rank, '
                                 'first_place.rank_changed = :rank_changed',
                ExpressionAttributeValues={
                    ':num_of_first_place': first_place_completed,
                    ':first_place_rank': rank,
                    ':rank_changed': rank_changed
                }
            )
        else:
            stats_table.update_item(
                Key={'username': username},
                UpdateExpression='SET first_place = :new_first_place_stats',
                ExpressionAttributeValues={
                    ':new_first_place_stats': {
                        'num_of_first_place': 1,
                        'first_place_rank': '',
                        'rank_changed': False
                    }
                },
                ConditionExpression='attribute_not_exists(first_place)'
            )

    except Exception as ignore:
        # nothing can be done
        pass
