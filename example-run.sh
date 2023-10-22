#!/bin/sh

docker build \
  --file apps/feed-aggregator.Dockerfile \
  --tag "web3-feed-aggregator" \
  apps

docker build \
  --file apps/feed-publisher.Dockerfile \
  --tag "web3-feed-publisher" \
  apps

docker build \
  --file example/Dockerfile \
  --tag "web3-feed-example" \
  example

docker run \
  --name "web3-feed-example" \
  --publish 8545:8545 \
  --publish 8081:8081 \
  --publish 9091:9091 \
  --publish 8082:8082 \
  --publish 9092:9092 \
  --publish 8083:8083 \
  --publish 9093:9093 \
  --rm \
  web3-feed-example