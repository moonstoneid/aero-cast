#!/bin/bash
tar -xvf data.tar.gz
ganache \
--host=0.0.0.0 \
--port=8545 \
--chain.chainId=31337 \
--wallet.mnemonic='test test test test test test test test test test test junk' \
--database.dbPath=data