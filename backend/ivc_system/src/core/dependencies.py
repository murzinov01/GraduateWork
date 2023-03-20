from .db.dals.users_dal import UserDAL, UserTokenDal
from .db.database import SessionLocal


def get_users_dal():
    session = SessionLocal()
    try:
        yield UserDAL(session)
    finally:
        session.close()


def get_user_token_dal():
    session = SessionLocal()
    try:
        yield UserTokenDal(session)
    finally:
        session.close()
