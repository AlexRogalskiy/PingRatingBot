FROM gradle:7-jdk11 AS build
WORKDIR /app
COPY --chown=gradle:gradle . /app
RUN gradle build --no-daemon

FROM openjdk:11-slim
WORKDIR /app
COPY --from=build /app/build/distributions/pingpong.tar /app
RUN tar -xf ./pingpong.tar && rm ./pingpong.tar
ENTRYPOINT "/app/pingpong/bin/pingpong"
