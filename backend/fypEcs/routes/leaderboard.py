from typing import Optional

from pydantic import BaseModel
from lib.globals import (leaderboard_table)
from fastapi import FastAPI, HTTPException, status, Query, Request, Depends
from lib.leaderboard_helpers import (
    get_user_league_rank,
    create_new_leaderboard,
    add_user_to_leaderboard,
    set_user_leaderboard_id,
    get_user_leaderboard,
    increase_user_score,
    update_user_attribute
)
from lib.user_verification import verify_username


class CurrentUser(BaseModel):
    username: str


class Score(BaseModel):
    username: str
    score: int


def leaderboard(app):
    @app.patch('/leaderboard/join', status_code=status.HTTP_200_OK)
    def join_leaderboard(request: Request, current_user: CurrentUser):
        verify_username(request, current_user.username)

        league_rank, current_leaderboard_id = get_user_league_rank(current_user.username)

        update_user_attribute(current_user.username, 'custom:seenWelcome', 'true')

        if not league_rank:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND, detail='League rank not found.')

        if current_leaderboard_id != 'none':
            raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail='User already in a leaderboard.')

        response = leaderboard_table.query(
            IndexName='rank-index',
            KeyConditionExpression='league_rank = :r',
            FilterExpression='num_of_positions < :max',
            ExpressionAttributeValues={
                ':r': league_rank,
                ':max': 30
            }
        )

        items = response.get('Items', [])
        if not items:
            leaderboard_id = create_new_leaderboard(league_rank, current_user.username)
        else:
            leaderboard_id = items[0]['id']
            add_user_to_leaderboard(leaderboard_id, current_user.username)

        successfully_added_leaderboard = set_user_leaderboard_id(current_user.username, leaderboard_id)
        if not successfully_added_leaderboard:
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
                                detail='Failed to add user.')

        return {'leaderboard_id': leaderboard_id}

    @app.get('/leaderboard/rankings', status_code=status.HTTP_200_OK)
    def get_user_rankings(username: Optional[str] = Query(None),
                          verified: bool = Depends(verify_username)):
        if username:
            current_user = CurrentUser(username=username)
            leaderboard_id = get_user_leaderboard(current_user.username)
            if leaderboard_id:
                response = leaderboard_table.get_item(Key={'id': leaderboard_id})
                item = response.get('Item', {})
                positions = item.get('positions', [])
                ranked_positions = sorted(positions, key=lambda x: x['score'], reverse=True)
                return ranked_positions
        else:
            raise HTTPException(status_code=status.HTTP_404_NOT_FOUND,
                                detail='Username is required.')

    @app.patch('/leaderboard/score', status_code=status.HTTP_200_OK)
    def new_score(request: Request, score: Score):
        verify_username(request, score.username)

        leaderboard_id = get_user_leaderboard(score.username)
        increase_user_score(leaderboard_id, score.username, score.score)
