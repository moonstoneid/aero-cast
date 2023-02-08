// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.0;

import "hardhat/console.sol";

import "./Ownable.sol";

contract FeedSubscriber is Ownable {

    event NewSubscription(address indexed pubAddress);

    struct Subscription {
        uint timestamp;
        address pubAddress;
    }

    Subscription[] private _subscriptions;
    
    constructor() {
        
    }

    function subscribe(address _pubAddress) public onlyOwner {
        _subscriptions.push(Subscription(block.timestamp, _pubAddress));
        console.log("Account %s subscribed to publisher %s.", msg.sender, _pubAddress);
        emit NewSubscription(_pubAddress);
    }

    function getSubscriptions() public view returns (Subscription[] memory) {
        return _subscriptions;
    }

}
