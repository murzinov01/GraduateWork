from datetime import datetime, timedelta

from jose import jwt

from ...config import (ACCESS_TOKEN_EXPIRE_MINUTES, ALGORITHM,
                       REFRESH_TOKEN_EXPIRE_MINUTES, SECRET_KEY)


def create_access_token(data: dict, expires_delta: timedelta | None = None):
    to_encode = data.copy()
    if expires_delta:
        expire = datetime.utcnow() + expires_delta
    else:
        expire = datetime.utcnow() + timedelta(minutes=15)
    to_encode.update({"exp": expire})
    encoded_jwt = jwt.encode(to_encode, SECRET_KEY, algorithm=ALGORITHM)
    return encoded_jwt


def generate_tokens(data: str) -> tuple[str, str]:
    access_token = create_access_token(
        data={"sub": data},
        expires_delta=timedelta(minutes=ACCESS_TOKEN_EXPIRE_MINUTES),
    )
    refresh_token = create_access_token(
        data={"sub": data},
        expires_delta=timedelta(minutes=REFRESH_TOKEN_EXPIRE_MINUTES),
    )
    return access_token, refresh_token
