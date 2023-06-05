import os


# DATABASE
# SQLALCHEMY_DATABASE_URL = os.getenv("SQLALCHEMY_DATABASE_URL", "postgresql://postgres:postgrespw@localhost:5432")
SQLALCHEMY_DATABASE_URL = os.getenv("SQLALCHEMY_DATABASE_URL", "").replace("postgres", "postgresql")
print(f"{SQLALCHEMY_DATABASE_URL=}")

# JWT Generation
SECRET_KEY = "01d25e094faa6ca3556c818166b7a9563b93f7099f6f0f4caa6cf63b88e8d3e7"
ALGORITHM = "HS256"
ACCESS_TOKEN_EXPIRE_MINUTES = 1  # 10 minutes
REFRESH_TOKEN_EXPIRE_MINUTES = 60 * 24 * 30  # 30 days
