const hre = require("hardhat");

const pubContractMeta = require('../artifacts/contracts/FeedPublisher.sol/FeedPublisher.json');
const subContractMeta = require('../artifacts/contracts/FeedSubscriber.sol/FeedSubscriber.json');

async function main() {
  // Get signers
  const [main, pub, sub] = await hre.ethers.getSigners();
  console.log(`Main address: ${main.address}`);
  console.log(`Pub address: ${pub.address}`);
  console.log(`Sub address: ${sub.address}`);

  // Create registry
  const regContr = await createRegistry(main);

  // Create publisher
  const pubContr = await createPublisher(regContr, pub);
  // Create subscriber
  const subContr = await createSubscriber(regContr, sub);

  // Subscribe
  await subContr.connect(sub).subscribe(pubContr.address);

  // Listen for publish events
  pubContr.on("NewPubItem", (num) => {
    console.log(`New item ${num} has been published.`);
  });

  // Publish
  await pubContr.connect(pub).publish("Hello!");
  await pubContr.connect(pub).publish("Servus!");
}

async function createRegistry(main) {
  const contrFact = await hre.ethers.getContractFactory("FeedRegistry");
  const contr = await contrFact.connect(main).deploy();
  console.log(`Registry contract address: ${contr.address}`);
  return contr;
}

async function createPublisher(regContr, pub) {
  const setupTxn = await regContr.connect(pub).setupPublisher();
  await setupTxn.wait();

  const contrAddr = await regContr.connect(pub).getPublisherContract();
  console.log(`Pub contract address: ${contrAddr}`);

  return new hre.ethers.Contract(contrAddr, pubContractMeta.abi, pub);
}

async function createSubscriber(regContr, sub) {
  const setupTxn = await regContr.connect(sub).setupSubscriber();
  await setupTxn.wait();

  const contrAddr = await regContr.connect(sub).getSubscriberContract();
  console.log(`Sub contract address: ${contrAddr}`);

  return new hre.ethers.Contract(contrAddr, subContractMeta.abi, sub);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
