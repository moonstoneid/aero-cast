require("dotenv").config();
require("@nomiclabs/hardhat-ethers");
require("@nomicfoundation/hardhat-chai-matchers");
require("@nomicfoundation/hardhat-toolbox");

const ALCHEMY_URL = process.env.ALCHEMY_URL;
const SEPOLIA_PRIVATE_KEY = process.env.SEPOLIA_PRIVATE_KEY;

module.exports = {
  solidity: "0.8.12",
  networks: {
    sepolia: {
      url: ALCHEMY_URL,
      accounts: [SEPOLIA_PRIVATE_KEY]
    }
  }
};
