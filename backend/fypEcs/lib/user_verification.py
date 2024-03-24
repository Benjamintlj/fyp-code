from botocore.exceptions import BotoCoreError, ClientError
from fastapi import Request, HTTPException
from typing import Optional
from lib.globals import (
    cognito_client
)


def validate_token_with_cognito(access_token: str):
    try:
        response = cognito_client.get_user(AccessToken=access_token)
        return response
    except (BotoCoreError, ClientError) as ignore:
        raise HTTPException(status_code=401, detail="Invalid token.")


def get_token_from_header(request: Request) -> Optional[str]:
    authorization: str = request.headers.get('Authorization')
    if not authorization:
        raise HTTPException(status_code=401, detail="Authorization header is missing.")
    try:
        scheme, token = authorization.split()
        if scheme.lower() != "bearer":
            raise HTTPException(status_code=401, detail="Authorization scheme not bearer.")
        return token
    except ValueError:
        raise HTTPException(status_code=401, detail="Invalid authorization header format.")


def verify_username(request: Request, username: str):
    token = get_token_from_header(request)
    user_info = validate_token_with_cognito(token)
    token_username = user_info['Username']
    if token_username != username:
        raise HTTPException(status_code=403, detail="Username does not match token.")
    return True
