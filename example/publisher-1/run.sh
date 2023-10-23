#!/bin/bash
APP_PORT="8082" \
APP_BASE_URL="http://127.0.0.1:8082" \
APP_TITLE="Architonic Perspectives" \
APP_SUB_TITLE="Welcome to Architonic Perspectives, where architecture unveils its stories through the interplay of light and shadows, the green revolution reshaping cities, and the timeless elegance of design movements." \
APP_ICON_PATH="./pub.ico" \
H2_FILE="./pubdb" \
H2_USERNAME="sa" \
H2_PASSWORD="password" \
H2_SERVER_PORT="9092" \
ETH_API_URL="http://127.0.0.1:8545" \
ETH_REGISTRY_CONTRACT_ADDRESS="0x5FbDB2315678afecb367f032d93F642f64180aa3" \
ETH_PUBLISHER_PRIVATE_KEY="0x59c6995e998f97a5a0044966f0945389dc9e86dae88c7a8412f4603b6b78690d" \
ETH_PUBLISHER_CONTRACT_ADDRESS="0xa16E02E87b7454126E5E10d957A927A7F5B5d2be" \
java -jar app.jar