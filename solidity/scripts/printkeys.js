const hre = require("hardhat");

const pubContractMeta = require('../artifacts/contracts/FeedPublisher.sol/FeedPublisher.json');
const subContractMeta = require('../artifacts/contracts/FeedSubscriber.sol/FeedSubscriber.json');

const RATING_LIKED = 0;
const RATING_DISLIKED = 1;

async function main() {
  const accounts = config.networks.hardhat.accounts;

  let index = 0; // first wallet, increment for next wallets
  const mainWallet = ethers.Wallet.fromMnemonic(accounts.mnemonic, accounts.path + `/${index}`);
  console.log(`Main address: ${mainWallet.address}`);
  console.log(`Main address private key: ${mainWallet.privateKey}`);

  index = 1;
  const pubWallet = ethers.Wallet.fromMnemonic(accounts.mnemonic, accounts.path + `/${index}`);
  console.log(`Pub address: ${pubWallet.address}`);
  console.log(`Pub address private key: ${pubWallet.privateKey}`);

  index = 2;
  const subWallet = ethers.Wallet.fromMnemonic(accounts.mnemonic, accounts.path + `/${index}`);
  console.log(`Sub address: ${subWallet.address}`);
  console.log(`Sub address private key: ${subWallet.privateKey}`);

}

main().catch((error) => {
  console.error(error);
  process.exitCode = 1;
});
