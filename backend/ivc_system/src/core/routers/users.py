from fastapi import APIRouter, Body, Depends, status
from fastapi.exceptions import HTTPException

from ..db.dals.users_dal import UserDAL
from ..dependencies import get_users_dal
from ..enums import Tags
from ..schemas.users import UserCreate, UserDb, UserProfile
from .auth import get_current_user

router = APIRouter(prefix="/users", tags=[Tags.users])


@router.post(
    "/create",
    description="Create (Register) user in the system.",
    status_code=status.HTTP_201_CREATED,
    response_model=UserProfile,
)
async def create_user(user: UserCreate = Body(), user_dal: UserDAL = Depends(get_users_dal)):
    if user_dal.get_user_by_email(user.email):
        raise HTTPException(status_code=status.HTTP_409_CONFLICT, detail=f"User with email={user.email} already exists")
    else:
        db_user = user_dal.create_user(user)
        return UserProfile(username=db_user.username, email=db_user.email)


@router.get(
    "/profile",
    description="Get user profile (main info)",
    response_model=UserProfile,
)
async def read_users_profile(db_user: UserDb = Depends(get_current_user)):
    return UserProfile(username=db_user.username, email=db_user.email)

