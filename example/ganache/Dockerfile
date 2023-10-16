FROM node:18
RUN npm install -g ganache@7.8.0

ENTRYPOINT ganache --host=0.0.0.0 --port=8545 --chain.chainId=31337 --wallet.mnemonic='test test test test test test test test test test test junk' --database.dbPath=ganache-data