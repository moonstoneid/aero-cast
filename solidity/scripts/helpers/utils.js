const fs = require('fs');

function getAddresses() {
  const json = fs.readFileSync('./addresses.json', 'utf8');
  const addrs = JSON.parse(json);
  return addrs;
}

async function getAccounts(web3) {
  return await web3.eth.getAccounts();
}

function getErrorMessage(error) {
  if (error instanceof Error) return error.message
  return String(error)
}

module.exports = {getAddresses, getAccounts, getErrorMessage};