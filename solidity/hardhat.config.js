require("dotenv").config();
require("@nomiclabs/hardhat-ethers");
require("@nomicfoundation/hardhat-chai-matchers");
require("@nomicfoundation/hardhat-toolbox");

const ALCHEMY_URL = process.env.ALCHEMY_URL;
const SEPOLIA_PRIVATE_KEY_MAIN = process.env.SEPOLIA_PRIVATE_KEY_MAIN;
const SEPOLIA_PRIVATE_KEY_PUB = process.env.SEPOLIA_PRIVATE_KEY_PUB;
const SEPOLIA_PRIVATE_KEY_SUB = process.env.SEPOLIA_PRIVATE_KEY_SUB;

module.exports = {
  solidity: "0.8.12",
  networks: {
    sepolia: {
      url: ALCHEMY_URL,
      accounts: [SEPOLIA_PRIVATE_KEY_MAIN, SEPOLIA_PRIVATE_KEY_PUB, SEPOLIA_PRIVATE_KEY_SUB]
    }
  }
};
