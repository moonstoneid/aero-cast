const hre = require("hardhat");

async function main() {
  const [deployer, pub, sub] = await hre.ethers.getSigners();
  console.log(`Deployer address: ${deployer.address}`);
  console.log(`Pub address: ${pub.address}`);
  console.log(`Sub address: ${sub.address}`);

  const regContrFact = await hre.ethers.getContractFactory("FeedRegistry");
  const regContr = await regContrFact.deploy();
  await regContr.deployed();

  console.log(`Registry contract address: ${regContr.address}`);

  const setupPubTxn = await regContr.connect(pub).setupPublisher();
  await setupPubTxn.wait();

  const setupSubTxn = await regContr.connect(sub).setupSubscriber();
  await setupSubTxn.wait();
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
