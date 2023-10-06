const FeedRegistry = artifacts.require("./FeedRegistry.sol");

module.exports = async function (deployer, networks, accounts) {
  await deployer.deploy(FeedRegistry);
  const registry = await FeedRegistry.deployed();
  console.log(`Registry contract address: ${registry.address}`);
};