import re

from pydantic import BaseModel, EmailStr, Field, validator


class UserBase(BaseModel):
    username: str = Field(min_length=3)


class UserProfile(UserBase):
    email: EmailStr


class UserLogin(UserBase):
    password: str

    @validator("password")
    def check_password(cls, password):
        reg = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!#%*?&]{6,20}$"
        pattern = re.compile(reg)
        if re.search(pattern, password):
            return password
        else:
            raise ValueError("Password does not meet the strength criteria")


class UserCreate(UserProfile, UserLogin):
    pass


class UserDb(UserProfile):
    hashed_password: str

    class Config:
        orm_mode = True
