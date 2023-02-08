const hre = require("hardhat");

async function main() {
  const [deployer] = await hre.ethers.getSigners();
  console.log(`Deployer address: ${deployer.address}`);

  const contractFactory = await hre.ethers.getContractFactory("FeedRegistry");
  const contract = await contractFactory.deploy();
  await contract.deployed();

  console.log(`Contract address: ${contract.address}`);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
