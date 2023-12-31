#!/bin/bash
APP_PORT="8083" \
APP_BASE_URL="http://127.0.0.1:8083" \
APP_TITLE="Wanderlust Chronicles" \
APP_SUB_TITLE="Join me in exploring diverse cultures, savoring culinary wonders, and immersing ourselves in the untouched beauty of nature." \
APP_ICON_PATH="./pub.ico" \
H2_FILE="./pubdb" \
H2_USERNAME="sa" \
H2_PASSWORD="password" \
H2_SERVER_PORT="9093" \
ETH_API_URL="http://127.0.0.1:8545" \
ETH_REGISTRY_CONTRACT_ADDRESS="0x5FbDB2315678afecb367f032d93F642f64180aa3" \
ETH_PUBLISHER_PRIVATE_KEY="0x5de4111afa1a4b94908f83103eb1f1706367c2e68ca870fc3fb9a804cdab365a" \
ETH_PUBLISHER_CONTRACT_ADDRESS="0xB7A5bd0345EF1Cc5E66bf61BdeC17D2461fBd968" \
java -jar app.jar