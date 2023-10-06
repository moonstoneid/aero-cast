const { getAddresses, getAccounts, getErrorMessage } = require('./helpers/utils');

const FeedRegistry = artifacts.require("./FeedRegistry.sol");

const main = async (callback) => {
  try {
    const addresses = getAddresses();
    const accounts = await getAccounts(web3);

    // Get subscriber account
    const sub = accounts[2];
    
    // Create subscriber contract
    const regContr = await FeedRegistry.at(addresses['registry']);
    await regContr.setupSubscriber({from: sub});

    // Get subscriber contract address
    const contrAddr = await regContr.getSubscriberContract({from: sub});
    console.log(`Sub contract address: ${contrAddr}`);
  } catch(err) {
    console.log(`Error: ${getErrorMessage(err)}`);
  }
  callback();
}

module.exports = main;