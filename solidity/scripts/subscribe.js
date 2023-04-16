const { getAddresses, getAccounts, getErrorMessage } = require('./helpers/utils');

const FeedPublisher = artifacts.require("./FeedPublisher.sol");
const FeedSubscriber = artifacts.require("./FeedSubscriber.sol");

const main = async (callback) => {
  try {
    const addresses = getAddresses();
    const accounts = await getAccounts(web3);

    // Get subscriber account
    const sub = accounts[2];
    
    // Get publisher contract
    const pubContr = await FeedPublisher.at(addresses['publisher']);

    // Get subscriber contract
    const subContr = await FeedSubscriber.at(addresses['subscriber']);

    // Subscribe
    await subContr.subscribe(pubContr.address, {from: sub});

    // Get subscriptions
    const subscriptions = await subContr.getSubscriptions({from: sub});
    console.log("Subscriptions:");
    for (let s of subscriptions) {
      console.log(`- address: ${s.pubAddress}, timestamp: ${s.timestamp}`);
    }
  } catch(err) {
    console.log(`Error: ${getErrorMessage(err)}`);
  }
  callback();
}

module.exports = main;