const { getAddresses, getAccounts, getErrorMessage } = require('./helpers/utils');

const FeedRegistry = artifacts.require("./FeedRegistry.sol");

const FEED_URL = "http://localhost:8082/rss";

const main = async (callback) => {
  try {
    const addresses = getAddresses();
    const accounts = await getAccounts(web3);

    // Get publisher account
    const pub = accounts[1];
    
    // Create publisher contract
    const regContr = await FeedRegistry.at(addresses['registry']);
    await regContr.setupPublisher(FEED_URL, {from: pub});

    // Get publisher contract address
    const contrAddr = await regContr.getPublisherContract({from: pub});
    console.log(`Pub contract address: ${contrAddr}`);
  } catch(err) {
    console.log(`Error: ${getErrorMessage(err)}`);
  }
  callback();
}

module.exports = main;