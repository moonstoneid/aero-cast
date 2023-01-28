const hre = require("hardhat");

async function main() {
  const contractFactory = await hre.ethers.getContractFactory("Hello");
  const contract = await contractFactory.deploy();
  await contract.deployed();

  const helloTxn = await contract.hello();
  await helloTxn.wait();
}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
