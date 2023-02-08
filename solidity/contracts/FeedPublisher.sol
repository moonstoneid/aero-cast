// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.0;

import "hardhat/console.sol";

import "./Ownable.sol";

contract FeedPublisher is Ownable {
    
    constructor() {
        
    }

    function publish() public view onlyOwner {
        console.log("'publish' called by address %s.", msg.sender);
    }

}
