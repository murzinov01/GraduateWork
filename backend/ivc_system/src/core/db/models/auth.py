from sqlalchemy import Column, ForeignKey, Integer, String
from sqlalchemy.orm import relationship

from ..database import Base


class UserToken(Base):
    __tablename__ = "tokens"

    id = Column(Integer, primary_key=True, index=True)
    refresh_token = Column(String, default="")
    user_id = Column(Integer, ForeignKey("users.id"), unique=True)

    user = relationship("User", back_populates="token")
