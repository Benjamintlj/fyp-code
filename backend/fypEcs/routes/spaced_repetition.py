import time

from botocore.exceptions import ClientError
from fastapi import status, Query, HTTPException, Depends, Request
from pydantic import BaseModel
from lib.globals import (
    spaced_repetition_table,
    day1,
    day4,
    week1,
    week2,
    month
)
from lib.spaced_repetition_helpers import (
    get_item,
    get_items_for_user
)
from lib.user_verification import verify_username


class UpdateSpacedRepetition(BaseModel):
    s3_url: str
    username: str
    percentage: int


def spaced_repetition(app):
    @app.get('/spaced-repetition', status_code=status.HTTP_200_OK)
    def get_spaced_repetition_data(s3_url: str = Query(None, description="Partition key, e.g., 'biology/1_1_cell_stuff'"),
                                   username: str = Query(..., description="Sort key"),
                                   verified: bool = Depends(verify_username)):

        if s3_url and username:
            return get_item(s3_url, username)
        elif username:
            return get_items_for_user(username)
        else:
            raise HTTPException(status_code=status.HTTP_400_BAD_REQUEST, detail='Bad request!')

    @app.put('/spaced-repetition', status_code=status.HTTP_200_OK)
    def update_spaced_repetition(request: Request, content: UpdateSpacedRepetition):
        verify_username(request, content.username)

        current_time_millis = int(time.time() * 1000)
        try:
            response = spaced_repetition_table.get_item(
                Key={
                    's3_url': content.s3_url,
                    'username': content.username
                }
            )
            lesson = response.get('Item')

            print(0)
            if lesson:
                time_to_wait = lesson.get('time_to_wait')
                last_completed = lesson.get('last_completed')

                if content.percentage > 70:
                    if (current_time_millis + day1) > (last_completed + time_to_wait):
                        if time_to_wait <= day4:
                            time_to_wait = week1
                        elif time_to_wait <= week1:
                            time_to_wait = week2
                        else:
                            time_to_wait = month
                elif content.percentage > 55:
                    time_to_wait = day4
                else:
                    time_to_wait = day1

                spaced_repetition_table.update_item(
                    Key={
                        's3_url': content.s3_url,
                        'username': content.username
                    },
                    UpdateExpression="set last_completed = :new_last_completed, time_to_wait = :new_wait",
                    ExpressionAttributeValues={
                        ':new_last_completed': current_time_millis,
                        ':new_wait': time_to_wait
                    },
                )
            else:
                spaced_repetition_table.put_item(
                    Item={
                        's3_url': content.s3_url,
                        'username': content.username,
                        'last_completed': current_time_millis,
                        'time_to_wait': day4
                    }
                )

        except ClientError as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")
        except Exception as e:
            print(e)
            raise HTTPException(status_code=status.HTTP_500_INTERNAL_SERVER_ERROR, detail="Something went wrong")

        return
