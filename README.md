# Local Ganache blockchain

## Start blockchain

Fresh blockchain
```
npm run ganache 
```

Persistent blockchain

```
npm run ganache-persistent 
```

## Accounts Wallet

Mnemonic: test test test test test test test test test test test junk

## Accounts

- Publisher: (1)
  - Account: 0x70997970C51812dc3A010C7d01b50e0d17dc79C8
  - PK: 0x59c6995e998f97a5a0044966f0945389dc9e86dae88c7a8412f4603b6b78690d
- Subscriber (2)
  - Account: 0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC
  - PK: 0x5de4111afa1a4b94908f83103eb1f1706367c2e68ca870fc3fb9a804cdab365a

# Contracts

## Deploy registry contract

The registry contract is created via Truffle migrations.

```
truffle migrate
```

## Create publisher contract

The publisher contract is created via a Truffle script.

```
truffle exec scripts/create_publisher.js
```

## Create subscriber contract

The subscriber contract is created via a Truffle script.

```
truffle exec scripts/create_subscriber.js
```

## Created contract addresses

- Registry: 0x5FbDB2315678afecb367f032d93F642f64180aa3
- Publisher: 0xa16E02E87b7454126E5E10d957A927A7F5B5d2be
- Subscriber: 0xB7A5bd0345EF1Cc5E66bf61BdeC17D2461fBd968