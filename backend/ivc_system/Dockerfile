FROM python:3.10-slim as compile-image

RUN apt-get update
RUN apt-get -y update
RUN apt-get install -y libpq-dev python3-dev libkrb5-dev libc-dev-bin postgresql-client

COPY requirements.txt ./

RUN pip install --upgrade pip
RUN pip install -r requirements.txt

FROM scratch as runtime-image
ARG APP_HOST=0.0.0.0
ARG APP_PORT=8000

ENV APP_HOST=${APP_HOST} \
    APP_PORT=${APP_PORT}

WORKDIR /srv/www/
EXPOSE ${APP_PORT}

COPY --from=compile-image / /

COPY . .

ENTRYPOINT ["./entrypoint.sh"]