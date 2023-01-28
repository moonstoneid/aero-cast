// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.0;

import "hardhat/console.sol";

contract Hello {

    uint private helloCnt;

    constructor() {
        console.log("Contract has been constructed!");
    }

    function hello() public {
        console.log("Hello from %s!", msg.sender);
        helloCnt++;
    }

    function getHelloCount() public view returns (uint) {
        console.log("We have %d total hellos!", helloCnt);
        return helloCnt;
    }

}
