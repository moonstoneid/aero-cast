// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.0;

import "hardhat/console.sol";

contract FeedRegistry {
    
    // Mapping of publisher contracts
    mapping(address => address) public publisherContracts;
    // Mapping of subscriber contracts
    mapping(address => address) public subscriberContracts;

    constructor() {
        console.log("Registry contract has been constructed!");
    }

    /**
    * @notice Creates a new publisher contract
    **/
    function setupPublisher() public returns (address) {
        FeedPublisher c = new FeedPublisher();
        address ca = address(c);
        console.log("Account %s created publisher contract %s.", msg.sender, ca);
        publisherContracts[msg.sender] = ca;
        return ca;
    }

    /**
    * @notice Returns the sender's publisher contract
    **/
    function getPublisherContract() public view returns (address) {
        address ca = publisherContracts[msg.sender];
        console.log("Account %s has publisher contract %s.", msg.sender, ca);
        return ca;
    }

    /**
    * @notice Creates a new subscriber contract
    **/
    function setupSubscriber() public returns (address) {
        FeedSubscriber c = new FeedSubscriber();
        address ca = address(c);
        console.log("Account %s created subscriber contract %s.", msg.sender, ca);
        subscriberContracts[msg.sender] = ca;
        return ca;
    }

    /**
    * @notice Returns the sender's subscriber contract
    **/
    function getSubscriberContract() public view returns (address) {
        address ca = subscriberContracts[msg.sender];
        console.log("Account %s has subscriber contract %s.", msg.sender, ca);
        return ca;
    }

}

abstract contract Ownable {

    address private _owner;

    constructor() {
        _owner = msg.sender;
    }

    function owner() public view virtual returns (address) {
        return _owner;
    }

    modifier onlyOwner() {
        require(_owner == msg.sender, "Caller of the function is not the owner!");
        _;
    }

}

contract FeedPublisher is Ownable {
    
    constructor() {
        
    }

}

contract FeedSubscriber is Ownable {
    
    constructor() {
        
    }

}
