// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.0;

import "hardhat/console.sol";

import "./Ownable.sol";

contract FeedSubscriber is Ownable {
    
    constructor() {
        
    }

    function subscribe() public view onlyOwner {
        console.log("'subscribe' called by address %s.", msg.sender);
    }

}