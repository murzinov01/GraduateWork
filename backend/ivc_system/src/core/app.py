import uvicorn
from fastapi import FastAPI

from .routers import auth, users, system

app = FastAPI(
    title="IvcCore",
    version="0.0.1",
    contact={
        "name": "Murzinov Michail",
        "email": "murzinov01@bk.ru",
    },
)


app.include_router(users.router)
app.include_router(auth.router)
app.include_router(system.router)


if __name__ == "__main__":
    uvicorn.run("app:app", port=8000, host="127.0.0.1")
