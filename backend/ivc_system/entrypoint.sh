#!/usr/bin/env bash

echo "HOST ${APP_HOST}"
echo "PORT ${APP_PORT}"
alembic upgrade head && uvicorn src.core.app:app --reload --host "${APP_HOST}" --port "${APP_PORT}"
