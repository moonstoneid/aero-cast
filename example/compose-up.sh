DOCKER_BUILDKIT=1 docker compose \
  --project-name web3-feed \
  --file docker-compose.yml \
  up --build --detach