// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.12;

import "hardhat/console.sol";

import "./FeedPublisher.sol";
import "./FeedSubscriber.sol";

contract FeedRegistry {
    
    // Mapping of publisher contracts
    mapping(address => address) public publisherContracts;
    // Mapping of subscriber contracts
    mapping(address => address) public subscriberContracts;

    constructor() {
        //console.log("Registry contract has been constructed!");
    }

    /**
    * @notice Creates a new publisher contract
    **/
    function setupPublisher() public {
        FeedPublisher c = new FeedPublisher();
        c.setOwner(msg.sender);
        address ca = address(c);
        //console.log("Account %s created publisher contract %s.", msg.sender, ca);
        publisherContracts[msg.sender] = ca;
    }

    /**
    * @notice Returns the sender's publisher contract
    **/
    function getPublisherContract() public view returns (address) {
        address ca = publisherContracts[msg.sender];
        //console.log("Account %s has publisher contract %s.", msg.sender, ca);
        return ca;
    }

    /**
    * @notice Creates a new subscriber contract
    **/
    function setupSubscriber() public {
        FeedSubscriber c = new FeedSubscriber();
        c.setOwner(msg.sender);
        address ca = address(c);
        //console.log("Account %s created subscriber contract %s.", msg.sender, ca);
        subscriberContracts[msg.sender] = ca;
    }

    /**
    * @notice Returns the sender's subscriber contract
    **/
    function getSubscriberContract() public view returns (address) {
        address ca = subscriberContracts[msg.sender];
        //console.log("Account %s has subscriber contract %s.", msg.sender, ca);
        return ca;
    }

}
