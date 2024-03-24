from fastapi import Request, HTTPException, Depends, Query
from typing import Optional
import boto3
from jose import jwt
from jose.exceptions import JWTError
from lib.globals import (
    region,
    user_pool_id,
    cognito_client
)


def get_auth_token(request: Request) -> Optional[str]:
    authorization: str = request.headers.get('Authorization')
    if not authorization:
        raise HTTPException(status_code=401, detail="Requires a auth token to use backend services.")
    try:
        scheme, token = authorization.split()
        if scheme.lower() != "bearer":
            raise HTTPException(status_code=401, detail="Authorization scheme must be bearer.")
        return token
    except ValueError:
        raise HTTPException(status_code=401, detail="Invalid authorization format.")


def get_username_and_validate_auth_token(token: str) -> str:
    json_web_ket_set = cognito_client.get_paginator('list_user_pool_clients').paginate(UserPoolId=user_pool_id)
    keys = json_web_ket_set['Keys']

    try:
        claims = jwt.decode(token, keys, algorithms=['RS256'])
        return claims['username']
    except JWTError as e:
        raise HTTPException(status_code=401, detail="Invalid auth token.")


def verify_username(request: Request, username: str):
    token = get_auth_token(request)
    token_username = get_username_and_validate_auth_token(token)
    if token_username != username:
        raise HTTPException(status_code=403, detail="Username does not match token.")
    return True
