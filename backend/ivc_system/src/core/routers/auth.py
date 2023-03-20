from fastapi import APIRouter, Depends, status
from fastapi.exceptions import HTTPException
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from jose import JWTError, jwt

from ...config import ALGORITHM, SECRET_KEY
from ..db.dals.users_dal import UserDAL, UserTokenDal
from ..dependencies import get_user_token_dal, get_users_dal
from ..enums import Tags
from ..exceptions import CredentialException
from ..schemas.auth import Token
from ..schemas.users import UserDb
from ..utils.jwt_token import generate_tokens
from ..utils.password import verify_password

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="auth/login")
router = APIRouter(prefix="/auth", tags=[Tags.auth])


def authenticate_user(user_dal: UserDAL, username: str, password: str):
    user = user_dal.get_user_by_username(username)
    if not user:
        return False
    if not verify_password(password, user.hashed_password):
        return False
    return user


async def get_current_user(token: str = Depends(oauth2_scheme), user_dal: UserDAL = Depends(get_users_dal)) -> UserDb:
    try:
        payload = jwt.decode(token, SECRET_KEY, algorithms=[ALGORITHM])
        user_id: int = int(payload.get("sub"))
        if user_id is None:
            raise CredentialException
    except JWTError:
        raise CredentialException
    if not (db_user := user_dal.get_user_by_id(user_id)):
        raise CredentialException
    else:
        return db_user


@router.post(
    "/login",
    description="Authenticate user in the system",
    response_model=Token,
    status_code=status.HTTP_200_OK,
)
async def login(
    form_data: OAuth2PasswordRequestForm = Depends(),
    user_dal: UserDAL = Depends(get_users_dal),
    user_token_dal: UserTokenDal = Depends(get_user_token_dal),
):
    user = authenticate_user(user_dal, form_data.username, form_data.password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect username or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token, refresh_token = generate_tokens(data=str(user.id))
    user_token_dal.update_refresh_token(user.id, refresh_token)
    return Token(access_token=access_token, refresh_token=refresh_token, token_type="bearer")


@router.post(
    "/refresh",
    description="Refresh access token by refresh token",
    response_model=Token,
    status_code=status.HTTP_200_OK,
)
async def update_access_and_refresh_tokens(
    refresh_token: str, user_token_dal: UserTokenDal = Depends(get_user_token_dal)
):
    try:
        payload = jwt.decode(refresh_token, SECRET_KEY, algorithms=[ALGORITHM])
        user_id: str = payload.get("sub")
        if user_id is None:
            raise CredentialException
    except JWTError:
        raise CredentialException
    if not user_token_dal.is_token_exists(int(user_id), refresh_token):
        raise CredentialException
    access_token, refresh_token = generate_tokens(data=user_id)
    user_token_dal.update_refresh_token(int(user_id), refresh_token)
    return Token(access_token=access_token, refresh_token=refresh_token, token_type="bearer")
