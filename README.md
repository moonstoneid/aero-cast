# Web3 Feed

Web3 Feed is a demo of a _decentralized_ 🌐 and _interactive_ 👍 web feed.

Personal feeds are an integral part of the web. However, centralized social media platforms define
how the user's feed is curated and threaten their users' privacy. At the same time, decentralized
systems like RSS/Atom are not curated and don't have a feedback channel.

Put more generally, the fabric of the web is decentralized protocols but its applications are often
centralized.

Web3 Feed uses the Ethereum blockchain to build a decentralized feed on the application level, where
users can **subscribe to websites** and choose their feed aggregator freely.

![Demo](./demo-light.webp#gh-light-mode-only "Demo")
![Demo](./demo-dark.webp#gh-dark-mode-only "Demo")

## Overview

![Overview](overview-light.png#gh-light-mode-only "Overview")
![Overview](overview-dark.png#gh-dark-mode-only "Overview")

Web 3 feed consists of the following components:

- **Publishers** create content. This could be any website such as a blog or news site.
- **Aggregators** are services that collect and curate published content on behalf of a subscriber.
- **Subscribers** are users who subscribe to publishers. They consume the content using an
  aggregator of their choice.
- **Ethereum Blockchain** provides a decentralized database to store subscriptions.

Of course, there can be many publishers, subscribers and aggregators.

At first, the publisher and subscriber must each deploy a smart contract
on the blockchain (omitted from the figure for the sake of simplicity). When a user subscribes to a
publisher, they add the publisher's address to the list of subscriptions stored in their contract.
Additionally, the subscriber allows an aggregator of their choice to read the list of
subscriptions from their contract.

When a publisher creates new content, an Ethereum event is emitted. Once the aggregator receives the
event, he fetches the content from the publisher and provides it to the user as part of a curated
feed.

### Detailed workflow

The figure below illustrates the whole workflow on a technical level.

![Workflow](workflow-light.png#gh-light-mode-only "Workflow")
![Workflow](workflow-dark.png#gh-dark-mode-only "Workflow")

1. A publisher creates a new instance of the Publisher contract on the blockchain. The contract
   contains the URL of the publisher's RSS feed. It is also used to emit an event when new content is
   created.
2. The new Publisher contract is registered with a Registry contract, which serves as a lookup table
   for all contracts.
3. The publisher stores the address of his contract on his publishing website (e.g. his blog).
4. A subscriber creates a new instance of the Subscriber contract. The contract is later used to
   manage subscriptions.
5. The new Subscriber contract is registered with the Registry Contract so that its address can be
   looked up by others.
6. The subscriber registers with an aggregator of his choice. An aggregator is a web service, that
   collects and curates published content on behalf of the subscriber.
7. During the registration, the aggregator fetches the subscriber's contract from the registry.
8. The subscriber subscribes to the publisher's website.
9. Upon subscribing, client-side Javascript requests the address of the subscriber's contract from
   the Registry contract.
10. The publisher's contract address is added to the list of subscriptions of the subscriber's
    contract using a client-side Javascript request. The publisher is now subscribed.
11. Adding to the list of subscriptions emits an Ethereum event. This enables aggregators to become
    aware of changes to subscriptions.
12. The publisher creates new content on his website.
13. This adds a reference to the new content to the publisher's contract.
14. A new Ethereum event is emitted to notify listeners of the new content.
15. When the aggregator's listener captures the event, he fetches the new content from the
    publisher's RSS feed.
16. Next, the aggregator provides the content to the user as part of a curated feed.

## Quickstart

The easiest way to set up Web3 Feed is via Docker.

The following command spins up a preconfigured local Ganache blockchain, one aggregator and two
publishers with sample data:

Linux / MacOS:
```bash
./example-run.sh
```

Windows (Powershell):
```bash
.\example-run.cmd
```

After startup, the following services are available:

|                  | URL(s)                                                 | Description                                                 | 
|:-----------------|:-------------------------------------------------------|:------------------------------------------------------------|
| **Aggregator**   | http://127.0.0.1:8081                                  | Generates a feed of subscribed                              |
| **Publisher 1**  | http://127.0.0.1:8082<br />http://127.0.0.1:8082/admin | Displays all published articles<br />Create a new article   |
| **Publisher 2**  | http://127.0.0.1:8083<br />http://127.0.0.1:8083/admin | Displays all published articles<br />Create a new article   |

## Usage

To interact with the local Ganache blockchain and its contracts, you need to set up Metamask
accordingly.

### Add new network to Metamask

Add a new network to Metamask using the following data:

| Type            | Value                 |
|:----------------|:----------------------|
| Network name    | Ganache localhost     |
| RPC URL         | http://127.0.0.1:8545 |
| Chain-ID        | 31337                 |
| Currency Symbol | ETH                   |

### Add accounts to Metamask

One of the advantages of a local blockchain is that accounts don't change. This example has several
accounts preconfigured. This means you can import these accounts to your Metamask. To interact as a
subscriber with publishers and aggregators, use the following private key to import the account to
your Metamask:

| Type            | Value                                                              |
|:----------------|:-------------------------------------------------------------------|
| Account Address | 0x90F79bf6EB2c4f870365E785982E1f101E93b906                         |
| Private Key     | 0x7c852118294e51e653712a81e05800f419141751be58f605c371e15141b007a6 |

<details>
<summary>Overview of all accounts and contracts (click to expand).</summary>

#### Accounts

- Publisher 1: (1)
  - Account: 0x70997970C51812dc3A010C7d01b50e0d17dc79C8
  - PK: 0x59c6995e998f97a5a0044966f0945389dc9e86dae88c7a8412f4603b6b78690d
- Publisher 2: (2)
  - Account: 0x3C44CdDdB6a900fa2b585dd299e03d12FA4293BC
  - PK: 0x5de4111afa1a4b94908f83103eb1f1706367c2e68ca870fc3fb9a804cdab365a
- Subscriber 1: (3)
  - Account: 0x90F79bf6EB2c4f870365E785982E1f101E93b906
  - PK: 0x7c852118294e51e653712a81e05800f419141751be58f605c371e15141b007a6
- Subscriber 2: (4)
  - Account: 0x15d34AAf54267DB7D7c367839AAf71A00a2C6A65
  - PK: 0x47e179ec197488593b187f80a00eb0da91f1b9d0b13f8733639f19c30a34926a

#### Contracts

- Registry: 0x5FbDB2315678afecb367f032d93F642f64180aa3
- Publisher 1: 0xa16E02E87b7454126E5E10d957A927A7F5B5d2be
- Publisher 2: 0xB7A5bd0345EF1Cc5E66bf61BdeC17D2461fBd968
- Subscriber 1: 0xeEBe00Ac0756308ac4AaBfD76c05c4F3088B8883
- Subscriber 2: 0x10C6E9530F1C1AF873a391030a1D9E8ed0630D26
</details>

### Register at Aggregator

First, we want the feed aggregator to fetch the content to which the subscriber has subscribed.
Go to http://127.0.0.1:8081 and use the Metamask subscriber account to connect your wallet and enlist.

### Subscribe to Publisher

Now we want to subscribe to the two publishers. Open http://127.0.0.1:8082 and use the Metamask
subscriber account to subscribe. If everything works, the aggregator will now receive an Ethereum
event every time the publisher creates a new article. Do the same with the second publisher at
http://127.0.0.1:8083.

Now the aggregator should already show you sample content from both publishers in your personal feed.

### Publish content

Let's have some fun and publish some content own content. Open the first publisher's admin interface
at http://127.0.0.1:8082/admin and create a new article. This emits an event that the Aggregator
picks up after a few seconds.

Open the aggregator at http://127.0.0.1:8082 and you should see your new article.

That's it 👏!

## Manual Setup

If you don't want to use the Docker-based quickstart, you can set up the project yourself. Note that
the project requires recent versions of Java, Maven, Node, and NPM.

### Set up local blockchain

This project uses Ganache (a local Ethereum blockchain). Follow the steps below to set up Ganache.

#### Installation

Run the following commands within the `contracts` folder to install all modules and Ganache:

```bash
npm install
npm install --global ganache
```

#### Start blockchain

If the installation was successful, execute the following command within the `contracts` folder to
start Ganache:

```bash
npm run ganache 
```

If you want the blockchain to be persistent, use the following command:

```bash
npm run ganache-persistent 
```
Now Ganache should be up and running!

### Deploy contracts

Now we can deploy the various contracts on the Ganache blockchain.

#### Registry contract

The registry contract serves as a lookup table for all other contracts. Deploy the registry contract
via Truffle migrations.

```bash
truffle migrate
```

#### Publisher contract

Next, deploy the publisher via a Truffle script.

```bash
truffle exec scripts/create_publisher.js
```

#### Subscriber contract

Finally, deploy the subscriber contract.

```bash
truffle exec scripts/create_subscriber.js
```

<details>
<summary>Click to show contract address details.</summary>

- Registry: 0x5FbDB2315678afecb367f032d93F642f64180aa3
- Publisher: 0xa16E02E87b7454126E5E10d957A927A7F5B5d2be
- Subscriber: 0xB7A5bd0345EF1Cc5E66bf61BdeC17D2461fBd968
</details>

### Set up apps

Now that the contracts are deployed, let's set up the Publisher App and Aggregator App. These are
Java-based applications that provide a web-based frontend to interact with. On the backend, they
interact with the deployed contracts.

#### Build

To build the Apps, run the following command within the `apps` folder:

```bash
mvn clean package
```

#### Start Publisher app

To start, run the following command within the `apps` folder:

```bash
java -jar feed-publisher\target\publisher.jar \
  --server.port=8082 \
  --h2.server.port=9092 \
  --spring.datasource.url="jdbc:h2:file:./pubdb;DATABASE_TO_LOWER=TRUE" \
  --spring.datasource.username="sa" \
  --spring.datasource.password="password" \
  --eth.api.url="http://127.0.0.1:8345" \
  --eth.registry.contractaddress="0x5FbDB2315678afecb367f032d93F642f64180aa3" \
  --eth.publisher.privateKey="0x59c6995e998f97a5a0044966f0945389dc9e86dae88c7a8412f4603b6b78690d" \
  --eth.publisher.contractAddress="0xa16E02E87b7454126E5E10d957A927A7F5B5d2be" \
  --app.baseURL="http://127.0.0.1:8082" \
  --app.title="Publisher 1" \
  --app.subTitle="A demo publisher" \
  --app.iconPath="pub.ico"
```

If everything works, you should be able to access the web interfaces:

|                 | Public                         | Admin                       |
|-----------------|--------------------------------|-----------------------------|
| **URL**         | http://127.0.0.1:8082          | http://127.0.0.1:8082/admin |
| **Description** | Overview of published articles | Publish a new article       |

#### Start Aggregator app

To start, run the following command within the `apps` folder:

```bash
java -jar feed-aggregator\target\aggregator.jar \
  --server.port=8081 \
  --h2.server.port=9091 \
  --spring.datasource.url="jdbc:h2:file:./aggdb;DATABASE_TO_LOWER=TRUE" \
  --spring.datasource.username="sa" \
  --spring.datasource.password="password" \
  --eth.api.url="http://127.0.0.1:8545" \
  --eth.registry.contractaddress="0x5FbDB2315678afecb367f032d93F642f64180aa3"
```

If everything works, you should be able to access the web interface using this URL:
http://127.0.0.1:8081

That's it. You have successfully set up the project. 👏

## Related work

[ActivityPub](https://en.wikipedia.org/wiki/ActivityPub) pursues similar goals using a different
technical approach.

## Contributing

Please use the GitHub issue tracker to report any bugs.

If you would like to contribute code, fork the repository and send a pull request. When submitting code, please make
every effort to follow existing conventions and style in order to keep the code as readable as possible.

## License

This project is distributed under the Apache License, Version 2.0 (see LICENSE file).

By submitting a pull request to this project, you agree to license your contribution under the Apache
License, Version 2.0.