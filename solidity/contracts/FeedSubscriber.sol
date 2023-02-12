// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.0;

import "hardhat/console.sol";

import "./Ownable.sol";

contract FeedSubscriber is Ownable {

    event NewSubscription(address indexed pubAddress);

    enum Reaction {
        Liked,
        Disliked,
        Neutral
    }

    struct PubItemReaction {
        string hash;
        Reaction reaction;
    }

    struct Subscription {
        uint timestamp;
        address pubAddress;
    }

    Subscription[] private _subscriptions;
    mapping(string => Reaction) private _pubItemToReaction;
    mapping(address => PubItemReaction[]) private _pubAddressToPubItemReaction;

    constructor() {
        
    }

    function subscribe(address _pubAddress) public onlyOwner {
        _subscriptions.push(Subscription(block.timestamp, _pubAddress));
        console.log("Account %s subscribed to publisher %s.", msg.sender, _pubAddress);
        emit NewSubscription(_pubAddress);
    }

    function reactToPubItem(address _pubAddress, string calldata hash, Reaction reaction) public onlyOwner {
        _pubItemToReaction[hash] = reaction;
        PubItemReaction[] storage pubItemReactions = _pubAddressToPubItemReaction[_pubAddress];
        pubItemReactions.push(PubItemReaction(hash, reaction));
        console.log("Account %s reacted to PubItem with hash %s.: %s", msg.sender, hash, uint(reaction));
    }

    function getSubscriptions() public view returns (Subscription[] memory) {
        return _subscriptions;
    }

    function getReactions(address _pubAddress) public view returns (PubItemReaction[] memory) {
        return _pubAddressToPubItemReaction[_pubAddress];
    }

}
