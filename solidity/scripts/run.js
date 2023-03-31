const hre = require("hardhat");

const pubContractMeta = require('../artifacts/contracts/FeedPublisher.sol/FeedPublisher.json');
const subContractMeta = require('../artifacts/contracts/FeedSubscriber.sol/FeedSubscriber.json');

const RATING_LIKED = 0;
const RATING_DISLIKED = 1;

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

  // Listen for publisher events
  pubContr.on("NewPubItem", (itemNum) => {
    console.log(`New item ${itemNum} has been published.`);
  });
  // Listen for subscriberer events
  subContr.on("CreateSubscription", (pubAddr) => {
    console.log(`Subscription on ${pubAddr} has been created.`);
  });
  subContr.on("CreateReaction", (pubAddr, pubItemNum) => {
    console.log(`Reaction on ${pubAddr}/${pubItemNum} has been created.`);
  });

  // Subscribe
  await subContr.connect(sub).subscribe(pubContr.address);

  // Get subscriptions
  getSubscriptions(subContr);

  // Publish
  await pubContr.connect(pub).publish("Hello!");
  await pubContr.connect(pub).publish("Servus!");

  // React to item 0 and 1
  // (Enums are treated as uint)
  await subContr.connect(sub).react(pubContr.address, 0, RATING_DISLIKED); 
  await subContr.connect(sub).react(pubContr.address, 1, RATING_LIKED);

  // Get reactions
  getReactions(subContr);
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

async function getSubscriptions(subContr) {
  const subscriptions = await subContr.getSubscriptions();
  console.log(Date.now() + " Subscriptions:");
  for (let s of subscriptions) {
    console.log(`- pubAddress: ${s.pubAddress}, timestamp: ${s.timestamp}`);
  }
}

async function getReactions(subContr) {
  const reactions = await subContr.getReactions();
  console.log("Reactions:");
  for (let r of reactions) {
    console.log(`- pubAddress: ${r.pubAddress}, pubItemNum: ${r.pubItemNum}, timestamp: ${r.timestamp}`);
  }
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
