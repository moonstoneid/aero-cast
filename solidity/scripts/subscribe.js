const hre = require("hardhat");

const pubContractMeta = require('../artifacts/contracts/FeedPublisher.sol/FeedPublisher.json');
const subContractMeta = require('../artifacts/contracts/FeedSubscriber.sol/FeedSubscriber.json');

async function main() {
  // Get signers
  const [main, pub, sub] = await hre.ethers.getSigners();
  console.log(`Main address: ${main.address}`);
  console.log(`Pub address: ${pub.address}`);
  console.log(`Sub address: ${sub.address}`);


  // Create publisher
  const pubContr = await getPublisher("0xa16E02E87b7454126E5E10d957A927A7F5B5d2be");
  // Create subscriber
  const subContr = await getSubscriber("0xB7A5bd0345EF1Cc5E66bf61BdeC17D2461fBd968");

  // Subscribe
  await subContr.connect(sub).subscribe(pubContr.address);

  // Get subscriptions
  getSubscriptions(subContr);

}


async function getPublisher(pubAddress) {
  const MyContract = await ethers.getContractFactory("FeedPublisher");
  const contract = await MyContract.attach(pubAddress);
  return contract;
}

async function getSubscriber(subAddress) {
  const MyContract = await ethers.getContractFactory("FeedSubscriber");
  const contract = await MyContract.attach(subAddress);
  return contract;
}

async function getSubscriptions(subContr) {
  const subscriptions = await subContr.getSubscriptions();
  console.log(Date.now() + " Subscriptions:");
  for (let s of subscriptions) {
    console.log(`- pubAddress: ${s.pubAddress}, timestamp: ${s.timestamp}`);
  }
}


main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
