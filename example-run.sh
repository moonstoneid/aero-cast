#!/bin/sh

docker build \
  --file apps/feed-aggregator.Dockerfile \
  --tag "aero-cast-aggregator" \
  apps

docker build \
  --file apps/feed-publisher.Dockerfile \
  --tag "aero-cast-publisher" \
  apps

docker build \
  --file example/Dockerfile \
  --tag "aero-cast-example" \
  example

docker run \
  --name "aero-cast-example" \
  --publish 8545:8545 \
  --publish 8081:8081 \
  --publish 9091:9091 \
  --publish 8082:8082 \
  --publish 9092:9092 \
  --publish 8083:8083 \
  --publish 9093:9093 \
  --rm \
  aero-cast-example