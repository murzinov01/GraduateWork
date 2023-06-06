#!/usr/bin/env bash

alembic upgrade head && uvicorn src.core.app:app --reload --host "${APP_HOST}" --port "${APP_PORT}"
