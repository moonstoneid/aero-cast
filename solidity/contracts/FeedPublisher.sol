// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.0;

import "hardhat/console.sol";

import "./Ownable.sol";

contract FeedPublisher is Ownable {

    event NewPubItem(uint indexed num);

    struct PubItem {
        uint timestamp;
        string message;
    }

    PubItem[] private _pubItems;
    
    constructor() {
        
    }

    function publish(string memory _message) public onlyOwner {
        uint size = _pubItems.length;
        _pubItems.push(PubItem(block.timestamp, _message));
        console.log("Account %s published item %d: %s.", msg.sender, size, _message);
        emit NewPubItem(size);
    }

    function getTotalPubItemCount() public view returns (uint) {
        return _pubItems.length;
    }

    function getPubItem(uint _num) public view returns (PubItem memory) {
        require(
            _num < _pubItems.length,
            "Pub item does not exist!"
        );
        return _pubItems[_num];
    }

}
