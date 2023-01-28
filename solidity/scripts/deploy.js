const hre = require("hardhat");

async function main() {
  const [deployer] = await hre.ethers.getSigners();
  console.log("Deploying contract with account: ", deployer.address);

  const contractFactory = await hre.ethers.getContractFactory("Hello");
  const contract = await contractFactory.deploy();
  await contract.deployed();

  console.log(`Contract address: ${contract.address}`);
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
