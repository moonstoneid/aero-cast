// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.12;

import "@ganache/console.log/console.sol";

import "./Ownable.sol";

contract FeedPublisher is Ownable {

    event NewPubItem(uint indexed num);

    struct PubItem {
        uint num;
        uint timestamp;
        string data;
    }

    string private _feedUrl;
    PubItem[] private _pubItems;
    
    constructor() {
        console.log("Publisher contract has been constructed!");
    }

    function setFeedUrl(string memory feedUrl) public onlyOwner {
        _feedUrl = feedUrl;
        console.log("Account %s set feed URL: '%s'", msg.sender, _feedUrl);
    }

    function getFeedUrl() public view returns (string memory) {
        return _feedUrl;
    }

    function publish(string memory data) public onlyOwner {
        uint size = _pubItems.length;
        _pubItems.push(PubItem(size, block.timestamp, data));
        console.log("Account %s published item %d: '%s'", msg.sender, size, data);
        emit NewPubItem(size);
    }

    function getTotalPubItemCount() public view returns (uint) {
        return _pubItems.length;
    }

    function getPubItem(uint num) public view returns (PubItem memory) {
        require(
            num < _pubItems.length,
            "Pub item does not exist!"
        );
        return _pubItems[num];
    }

}
