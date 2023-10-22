FROM ubuntu:22.04 AS base
RUN apt update && \
    apt install -y ca-certificates curl gnupg && \
    mkdir -p /etc/apt/keyrings && \
    curl -fsSL https://apt.corretto.aws/corretto.key | gpg --dearmor -o /etc/apt/keyrings/corretto.gpg && \
    echo "deb [signed-by=/etc/apt/keyrings/corretto.gpg] https://apt.corretto.aws stable main" | tee /etc/apt/sources.list.d/corretto.list && \
    curl -fsSL https://deb.nodesource.com/gpgkey/nodesource-repo.gpg.key | gpg --dearmor -o /etc/apt/keyrings/nodesource.gpg && \
    echo "deb [signed-by=/etc/apt/keyrings/nodesource.gpg] https://deb.nodesource.com/node_18.x nodistro main" | tee /etc/apt/sources.list.d/nodesource.list && \
    apt update && \
    apt install -y java-17-amazon-corretto-jdk && \
    apt install nodejs -y && \
    npm install -g truffle@5.11.5 && \
    npm install -g ganache@7.9.1 && \
    apt install -y supervisor && \
    mkdir -p /var/log/supervisor

FROM base
COPY ganache /ganache
COPY --from=web3-feed-aggregator /app/app.jar /apps/aggregator/app.jar
COPY aggregator /apps/aggregator
COPY --from=web3-feed-publisher /app/app.jar /apps/publisher-1/app.jar
COPY publisher-1 /apps/publisher-1
COPY --from=web3-feed-publisher /app/app.jar /apps/publisher-2/app.jar
COPY publisher-2 /apps/publisher-2
COPY supervisord.conf /etc/supervisor/conf.d/supervisord.conf
EXPOSE 8545
EXPOSE 8081
EXPOSE 9091
EXPOSE 8082
EXPOSE 9092
EXPOSE 8083
EXPOSE 9093
CMD ["supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
