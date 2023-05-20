from fastapi import APIRouter, Depends, status
from ..enums import Tags

router = APIRouter(tags=[Tags.system])


@router.get("/health", status_code=status.HTTP_200_OK)
async def health():
    return {"status": "successful"}
