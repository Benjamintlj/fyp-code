import time
from enum import Enum

from botocore.exceptions import BotoCoreError, ClientError
from fastapi import HTTPException, Request, Depends
from pydantic import BaseModel
from starlette import status

from lib.globals import (
    stats_table,
    day1,
    day2,
)
from lib.user_verification import verify_username

BRONZE = 'BRONZE'
SILVER = 'SILVER'
GOLD = 'GOLD'


class Rank(Enum):
    BRONZE = 1
    SILVER = 2
    GOLD = 3


STREAK_BOUNDARIES = {BRONZE: 3, SILVER: 10, GOLD: 30}
COMPLETE_LESSONS_BOUNDARIES = {BRONZE: 3, SILVER: 10, GOLD: 30}
FLAWLESS_BOUNDARIES = {BRONZE: 5, SILVER: 20, GOLD: 40}
SPEED_RUN_BOUNDARIES = {BRONZE: 5, SILVER: 20, GOLD: 40}
REVISED_LESSON_BOUNDARIES = {BRONZE: 5, SILVER: 20, GOLD: 40}
TOTAL_GEMS_BOUNDARIES = {BRONZE: 100, SILVER: 250, GOLD: 1000}
FIRST_PLACE_BOUNDARIES = {BRONZE: 1, SILVER: 3, GOLD: 10}


class Content(BaseModel):
    username: str


class Gems(BaseModel):
    username: str
    gems: int


def stats(app):
    @app.put('/stats/reset_rank_changed', status_code=status.HTTP_200_OK)
    def reset_rank_changed(request: Request, content: Content):
        verify_username(request, content.username)

        try:
            dynamo_response = stats_table.get_item(Key={'username': content.username})
            statistics = dynamo_response.get('Item')

            if not statistics:
                return

            stat_type = [
                'streak_stats',
                'lessons_completed',
                'flawless',
                'speed_run',
                'revised_lessons',
                'total_gems'
            ]

            for stat in stat_type:
                if stat in statistics:
                    stats_table.update_item(
                        Key={'username': content.username},
                        UpdateExpression='SET ' + stat + '.rank_changed = :new_rank_changed',
                        ExpressionAttributeValues={
                            ':new_rank_changed': False
                        }
                    )

        except ClientError as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")
        except Exception as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")

        return {}

    @app.put('/stats/streak', status_code=status.HTTP_200_OK)
    def updateStreak(request: Request, content: Content):
        verify_username(request, content.username)

        current_time = int(time.time() * 1000)

        try:
            dynamo_response = stats_table.get_item(Key={'username': content.username})
            statistics = dynamo_response.get('Item')

            if statistics and 'streak_stats' in statistics:
                streak_stats = statistics['streak_stats']
                rank_changed = streak_stats['rank']

                if current_time > (streak_stats['last_online'] + day2):
                    stats_table.update_item(
                        Key={'username': content.username},
                        UpdateExpression='SET streak_stats.streak = :streak, '
                                         'streak_stats.last_online = :last_online, '
                                         'streak_stats.rank = :rank, '
                                         'streak_stats.rank_changed = :rank_changed',
                        ExpressionAttributeValues={
                            ':new_streak_stats': {
                                'streak': 0,
                                'last_online': current_time,
                                'rank': streak_stats.get('rank', ''),
                                'rank_changed': rank_changed
                            }
                        }
                    )

                elif current_time > streak_stats['last_online'] + day1:
                    new_streak = streak_stats['username'] + 1
                    current_rank = Rank[streak_stats['rank'].upper()] if 'rank' in streak_stats and \
                                                                                streak_stats['rank'] else None

                    new_rank = current_rank

                    if new_streak == STREAK_BOUNDARIES[BRONZE] and current_rank is None:
                        new_rank = Rank.BRONZE
                        rank_changed = True
                    elif new_streak == STREAK_BOUNDARIES[SILVER] and (current_rank is None or current_rank == Rank.BRONZE):
                        new_rank = Rank.SILVER
                        rank_changed = True
                    elif new_streak == STREAK_BOUNDARIES[GOLD] and (current_rank is None or current_rank in [Rank.BRONZE, Rank.SILVER]):
                        new_rank = Rank.GOLD
                        rank_changed = True

                    if rank_changed:
                        rank = new_rank.name.lower()
                    else:
                        rank = current_rank.name.lower() if current_rank else ''

                    stats_table.update_item(
                        Key={'username': content.username},
                        UpdateExpression='SET streak_stats.streak = :streak, '
                                         'streak_stats.last_online = :last_online, '
                                         'streak_stats.rank = :rank, '
                                         'streak_stats.rank_changed = :rank_changed',
                        ExpressionAttributeValues={
                            ':new_streak_stats': {
                                'streak': new_streak,
                                'last_online': current_time,
                                'rank': rank,
                                'rank_changed': rank_changed
                            }
                        }
                    )
            else:
                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET streak_stats = :streak_stats',
                    ExpressionAttributeValues={
                        ':streak_stats': {
                            'streak': 1,
                            'last_online': current_time,
                            'rank': '',
                            'rank_changed': False
                        }
                    },
                    ConditionExpression='attribute_not_exists(streak_stats)'
                )

        except ClientError as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")
        except Exception as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")

        return {}

    @app.put('/stats/lessons_completed', status_code=status.HTTP_200_OK)
    def update_lessons_completed(request: Request, content: Content):
        verify_username(request, content.username)

        try:
            dynamo_response = stats_table.get_item(Key={'username': content.username})
            statistics = dynamo_response.get('Item')

            if statistics and 'lessons_completed' in statistics:
                lessons_stats = statistics['lessons_completed']
                lessons_completed = lessons_stats.get('num_of_lessons_completed', 0) + 1
                rank_changed = lessons_stats.get('rank_changed', False)
                current_rank = Rank[lessons_stats['rank'].upper()] if 'rank' in lessons_stats and lessons_stats['rank'] else None

                if lessons_completed == COMPLETE_LESSONS_BOUNDARIES[BRONZE]:
                    new_rank = Rank.BRONZE
                    rank_changed = True
                elif lessons_completed == COMPLETE_LESSONS_BOUNDARIES[SILVER]:
                    new_rank = Rank.SILVER
                    rank_changed = True
                elif lessons_completed == COMPLETE_LESSONS_BOUNDARIES[GOLD]:
                    new_rank = Rank.GOLD
                    rank_changed = True
                else:
                    new_rank = current_rank

                if rank_changed:
                    rank = new_rank.name.lower()
                else:
                    rank = current_rank.name.lower() if current_rank else ''

                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET lessons_completed.num_of_lessons_completed = :num_of_lessons_completed, '
                                     'lessons_completed.rank = :rank, '
                                     'lessons_completed.rank_changed = :rank_changed',
                    ExpressionAttributeValues={
                        ':num_of_lessons_completed': lessons_completed,
                        ':rank': rank,
                        ':rank_changed': rank_changed
                    }
                )
            else:
                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET lessons_completed = :new_lessons_stats',
                    ExpressionAttributeValues={
                        ':new_lessons_stats': {
                            'num_of_lessons_completed': 1,
                            'rank': '',
                            'rank_changed': False
                        }
                    },
                    ConditionExpression='attribute_not_exists(lessons_completed)'
                )

        except ClientError as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")
        except Exception as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")

        return {}

    @app.put('/stats/flawless', status_code=status.HTTP_200_OK)
    def update_flawless(request: Request, content: Content):
        verify_username(request, content.username)

        try:
            dynamo_response = stats_table.get_item(Key={'username': content.username})
            statistics = dynamo_response.get('Item')

            if statistics and 'flawless' in statistics:
                flawless_stats = statistics['flawless']
                flawless_completed = flawless_stats.get('num_of_flawless', 0) + 1
                rank_changed = flawless_stats.get('rank_changed', False)
                current_rank = Rank[flawless_stats['rank'].upper()] if 'rank' in flawless_stats and flawless_stats['rank'] else None

                if flawless_completed == FLAWLESS_BOUNDARIES[BRONZE]:
                    new_rank = Rank.BRONZE
                    rank_changed = True
                elif flawless_completed == FLAWLESS_BOUNDARIES[SILVER]:
                    new_rank = Rank.SILVER
                    rank_changed = True
                elif flawless_completed == FLAWLESS_BOUNDARIES[GOLD]:
                    new_rank = Rank.GOLD
                    rank_changed = True
                else:
                    new_rank = current_rank

                if rank_changed:
                    rank = new_rank.name.lower()
                else:
                    rank = current_rank.name.lower() if current_rank else ''

                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET flawless.num_of_flawless = :num_of_flawless, '
                                     'flawless.rank = :rank, '
                                     'flawless.rank_changed = :rank_changed',
                    ExpressionAttributeValues={
                        ':num_of_flawless': flawless_completed,
                        ':rank': rank,
                        ':rank_changed': rank_changed
                    }
                )
            else:
                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET flawless = :new_flawless_stats',
                    ExpressionAttributeValues={
                        ':new_flawless_stats': {
                            'num_of_flawless': 1,
                            'rank': '',
                            'rank_changed': False
                        }
                    },
                    ConditionExpression='attribute_not_exists(flawless)'
                )

        except ClientError as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")
        except Exception as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")

        return {}

    @app.put('/stats/speed_run', status_code=status.HTTP_200_OK)
    def update_speed_run(request: Request, content: Content):
        verify_username(request, content.username)

        try:
            dynamo_response = stats_table.get_item(Key={'username': content.username})
            statistics = dynamo_response.get('Item')

            if statistics and 'speed_run' in statistics:
                speed_run_stats = statistics['speed_run']
                speed_runs_completed = speed_run_stats.get('num_of_speed_runs', 0) + 1
                rank_changed = speed_run_stats.get('rank_changed', False)
                current_rank = Rank[
                    speed_run_stats['rank'].upper()] if 'rank' in speed_run_stats and \
                                                                  speed_run_stats['rank'] else None

                if speed_runs_completed == SPEED_RUN_BOUNDARIES[BRONZE]:
                    new_rank = Rank.BRONZE
                    rank_changed = True
                elif speed_runs_completed == SPEED_RUN_BOUNDARIES[SILVER]:
                    new_rank = Rank.SILVER
                    rank_changed = True
                elif speed_runs_completed == SPEED_RUN_BOUNDARIES[GOLD]:
                    new_rank = Rank.GOLD
                    rank_changed = True
                else:
                    new_rank = current_rank

                if rank_changed:
                    rank = new_rank.name.lower()
                else:
                    rank = current_rank.name.lower() if current_rank else ''

                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET speed_run.num_of_speed_runs = :num_of_speed_runs, '
                                     'speed_run.rank = :rank, '
                                     'speed_run.rank_changed = :rank_changed',
                    ExpressionAttributeValues={
                        ':num_of_speed_runs': speed_runs_completed,
                        ':rank': rank,
                        ':rank_changed': rank_changed
                    }
                )
            else:
                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET speed_run = :new_speed_run_stats',
                    ExpressionAttributeValues={
                        ':new_speed_run_stats': {
                            'num_of_speed_runs': 1,
                            'rank': '',
                            'rank_changed': False
                        }
                    },
                    ConditionExpression='attribute_not_exists(speed_run)'
                )

        except ClientError as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")
        except Exception as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")

        return {}

    @app.put('/stats/revised_lessons', status_code=status.HTTP_200_OK)
    def update_revised_lessons(request: Request, content: Content):
        verify_username(request, content.username)

        try:
            dynamo_response = stats_table.get_item(Key={'username': content.username})
            statistics = dynamo_response.get('Item')

            if statistics and 'revised_lessons' in statistics:
                revised_stats = statistics['revised_lessons']
                revised_completed = revised_stats.get('num_of_revised', 0) + 1
                rank_changed = revised_stats.get('rank_changed', False)
                current_rank = Rank[revised_stats['rank'].upper()] if 'rank' in revised_stats and \
                                                                              revised_stats['rank'] else None

                if revised_completed == REVISED_LESSON_BOUNDARIES[BRONZE]:
                    new_rank = Rank.BRONZE
                    rank_changed = True
                elif revised_completed == REVISED_LESSON_BOUNDARIES[SILVER]:
                    new_rank = Rank.SILVER
                    rank_changed = True
                elif revised_completed == REVISED_LESSON_BOUNDARIES[GOLD]:
                    new_rank = Rank.GOLD
                    rank_changed = True
                else:
                    new_rank = current_rank

                if rank_changed:
                    rank = new_rank.name.lower()
                else:
                    rank = current_rank.name.lower() if current_rank else ''

                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET revised_lessons.num_of_revised = :num_of_revised, '
                                     'revised_lessons.rank = :rank, '
                                     'revised_lessons.rank_changed = :rank_changed',
                    ExpressionAttributeValues={
                        ':num_of_revised': revised_completed,
                        ':rank': rank,
                        ':rank_changed': rank_changed
                    }
                )
            else:
                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET revised_lessons = :new_revised_stats',
                    ExpressionAttributeValues={
                        ':new_revised_stats': {
                            'num_of_revised': 1,
                            'rank': '',
                            'rank_changed': False
                        }
                    },
                    ConditionExpression='attribute_not_exists(revised_lessons)'
                )

        except ClientError as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")
        except Exception as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")

        return {}

    @app.put('/stats/total_gems', status_code=status.HTTP_200_OK)
    def update_total_gems(request: Request, content: Gems):
        verify_username(request, content.username)

        try:
            dynamo_response = stats_table.get_item(Key={'username': content.username})
            statistics = dynamo_response.get('Item')

            if statistics and 'total_gems' in statistics:
                gems_stats = statistics['total_gems']
                total_gems = gems_stats.get('num_of_gems', 0) + content.gems
                rank_changed = gems_stats.get('rank_changed', False)
                current_rank = Rank[gems_stats['rank'].upper()] if 'rank' in gems_stats and gems_stats['rank'] else None

                if total_gems >= TOTAL_GEMS_BOUNDARIES[GOLD]:
                    new_rank = Rank.GOLD
                    rank_changed = True
                elif total_gems >= TOTAL_GEMS_BOUNDARIES[SILVER]:
                    new_rank = Rank.SILVER
                    rank_changed = True
                elif total_gems >= TOTAL_GEMS_BOUNDARIES[BRONZE]:
                    new_rank = Rank.BRONZE
                    rank_changed = True
                else:
                    new_rank = current_rank

                if rank_changed:
                    rank = new_rank.name.lower()
                else:
                    rank = current_rank.name.lower() if current_rank else ''

                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET total_gems.num_of_gems = :num_of_gems, '
                                     'total_gems.rank = :rank, '
                                     'total_gems.rank_changed = :rank_changed',
                    ExpressionAttributeValues={
                        ':num_of_gems': total_gems,
                        ':rank': rank,
                        ':rank_changed': rank_changed
                    }
                )
            else:
                stats_table.update_item(
                    Key={'username': content.username},
                    UpdateExpression='SET total_gems = :new_gems_stats',
                    ExpressionAttributeValues={
                        ':new_gems_stats': {
                            'num_of_gems': content.gems,
                            'rank': '',
                            'rank_changed': False
                        }
                    },
                    ConditionExpression='attribute_not_exists(total_gems)'
                )

        except ClientError as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")
        except Exception as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")

        return {}

    @app.get('/stats', status_code=status.HTTP_200_OK)
    def get_stats(username: str,
                  verified: bool = Depends(verify_username)):
        try:
            response = stats_table.get_item(
                Key={
                    'username': username
                }
            )
        except ClientError as ignore:
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail='Something went wrong')
        except Exception as ignore:
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail='Something went wrong')

        if response.get('Item'):
            return {
                'stats': response.get('Item'),
                'boundaries': {
                    'streak_stats': STREAK_BOUNDARIES,
                    'lessons_completed': COMPLETE_LESSONS_BOUNDARIES,
                    'flawless': FLAWLESS_BOUNDARIES,
                    'speed_run': SPEED_RUN_BOUNDARIES,
                    'revised_lessons': REVISED_LESSON_BOUNDARIES,
                    'total_gems': TOTAL_GEMS_BOUNDARIES,
                    'first_place': FIRST_PLACE_BOUNDARIES
                }
            }
        else:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail='Item not found')
