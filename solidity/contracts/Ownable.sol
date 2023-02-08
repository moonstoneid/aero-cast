// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.0;

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

    function setOwner(address _newOwner) public onlyOwner {
        _owner = _newOwner;
    }

}
