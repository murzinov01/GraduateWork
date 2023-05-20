from sqlalchemy.orm import Session

from ...schemas.users import UserCreate
from ...utils.password import get_password_hash
from ..models.auth import UserToken
from ..models.users import User


class UserDAL:
    def __init__(self, db_session: Session):
        self.db_session = db_session

    def create_user(self, user: UserCreate) -> User:
        hashed_password = get_password_hash(user.password)
        db_user = User(username=user.username, email=user.email, hashed_password=hashed_password)
        self.db_session.add(db_user)
        self.db_session.commit()
        self.db_session.refresh(db_user)

        db_token = UserToken(user_id=db_user.id)
        self.db_session.add(db_token)
        self.db_session.commit()

        return db_user

    def get_user_by_id(self, user_id: int) -> User:
        return self.db_session.query(User).filter(User.id == user_id).first()

    def get_user_by_username(self, username: str) -> User:
        return self.db_session.query(User).filter(User.username == username).first()

    def get_user_by_email(self, email: str) -> User:
        
        
        
        
        
        
        
        
        return self.db_session.query(User).filter(User.email == email).first()


class UserTokenDal:
    def __init__(self, db_session: Session):
        self.db_session = db_session

    def is_token_exists(self, user_id: int, refresh_token: str) -> UserToken:
        return (
            self.db_session.query(UserToken)
            .filter(UserToken.user_id == user_id, UserToken.refresh_token == refresh_token)
            .first()
        )

    def update_refresh_token(self, user_id: int, refresh_token: str):
        self.db_session.query(UserToken).filter(UserToken.user_id == user_id).update({UserToken.refresh_token: refresh_token})
        # db_token = UserToken(user_id=user_id, refresh_token=refresh_token)
        # self.db_session.add(db_token)
        self.db_session.commit()
        # self.db_session.refresh(db_token)
        # return db_token
