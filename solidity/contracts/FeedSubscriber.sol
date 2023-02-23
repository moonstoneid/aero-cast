// SPDX-License-Identifier: UNLICENSED

pragma solidity ^0.8.12;

import "hardhat/console.sol";

import "./Ownable.sol";

contract FeedSubscriber is Ownable {

    event CreateSubscription(address indexed pubAddress);
    event RemoveSubscription(address indexed pubAddress);

    event CreateReaction(address indexed pubAddress, uint _pubItemNum);
    event RemoveReaction(address indexed pubAddress, uint _pubItemNum);

    struct Subscription {
        address pubAddress;
        uint timestamp;
    }

    struct Reaction {
        address pubAddress;
        uint pubItemNum;
        Rating rating;
        uint timestamp;
    }
    
    enum Rating {
        Liked,   // 0
        Disliked // 1
    }

    // Array to store subscriptions
    Subscription[] private _subs;
    // Mapping to find subscriptions faster
    // Key: uint('pub addr')
    mapping (uint160 => uint) private _subRefs;

    // Array to store reactions
    Reaction[] private _reacts;
    // Mapping to find reactions faster
    // Key: Bitwise OR of uint('pub addr') and uint('pub item num')
    mapping (uint160 => uint) private _reactRefs;

    constructor() {
        
    }

    function subscribe(address pubAddr) public onlyOwner {
        Subscription memory sub = Subscription(pubAddr, block.timestamp);

        // Calculate key
        uint160 subKey = calcSubscriptionKey(pubAddr);
        // Get reference
        uint subRef = _subRefs[subKey];

        // Does item already exists?
        if (subRef > 0) {
            // Update item
            _subs[subRef-1] = sub;
        } else {
            // Store item
            _subs.push(sub);
            // Store item reference
            subRef = _subs.length;
            _subRefs[subKey] = subRef;
        }

        console.log("Account %s created subscription on publisher %s.", msg.sender, pubAddr);

        emit CreateSubscription(pubAddr);
    }

    function unsubscribe(address pubAddr) public onlyOwner {
        // Calculate key
        uint160 subKey = calcSubscriptionKey(pubAddr);
        // Get reference
        uint subRef = _subRefs[subKey];

        // If item was not found: Abort
        if (subRef == 0) {
            return;
        }

        // Reset item reference
        _subRefs[subKey] = 0;

        // If items exists: Reorganize items
        if (_subs.length > 1) {
            Subscription memory movSub = _subs[_subs.length-1];
            uint160 movSubKey = calcSubscriptionKey(movSub.pubAddress);
            _subs[subRef-1] = movSub;
            _subRefs[movSubKey] = subRef;
        }

        // Remove last item
        _subs.pop();

        console.log("Account %s removed subscription on publisher %s.", msg.sender, pubAddr);

        emit RemoveSubscription(pubAddr);
    }

    function react(address pubAddr, uint pubItemNum, Rating rating) public onlyOwner {
        Reaction memory rea = Reaction(pubAddr, pubItemNum, rating, block.timestamp);

        // Calculate key
        uint160 reaKey = calcReactionKey(pubAddr, pubItemNum);
        // Get reference
        uint reaRef = _reactRefs[reaKey];

        // Does item already exists?
        if (reaRef > 0) {
            // Update item
            _reacts[reaRef-1] = rea;
        } else {
            // Store item
            _reacts.push(rea);
            // Store item reference
            reaRef = _reacts.length;
            _reactRefs[reaKey] = reaRef;
        }

        console.log("Account %s created reaction on pub item %s/%d", msg.sender, pubAddr, pubItemNum);

        emit CreateReaction(pubAddr, pubItemNum);
    }

    function unreact(address pubAddr, uint pubItemNum) public onlyOwner {
        // Calculate key
        uint160 reaKey = calcReactionKey(pubAddr, pubItemNum);
        // Get reference
        uint reaRef = _reactRefs[reaKey];

        // If item was not found: Abort
        if (reaRef == 0) {
            return;
        }

        // Reset item reference
        _reactRefs[reaKey] = 0;

        // If items exists: Reorganize items
        if (_reacts.length > 1) {
            Reaction memory movRea = _reacts[_reacts.length-1];
            uint160 movReaKey = calcReactionKey(movRea.pubAddress, movRea.pubItemNum);
            _reacts[reaRef-1] = movRea;
            _reactRefs[movReaKey] = reaRef;
        }

        // Remove last item
        _reacts.pop();

        console.log("Account %s removed reaction on pub item %s/%d", msg.sender, pubAddr, pubItemNum);

        emit RemoveReaction(pubAddr, pubItemNum);
    }

    function getSubscriptions() public view returns (Subscription[] memory) {
        return _subs;
    }

    function getReactions() public view returns (Reaction[] memory) {
        return _reacts;
    }

    function calcSubscriptionKey(address pubAddr) internal pure returns (uint160) {
        return createAddrKey(pubAddr);
    }

    function calcReactionKey(address pubAddr, uint pubItemNum) internal pure returns (uint160) {
        return createAddrKey(pubAddr) << 16 | createNumKey(pubItemNum);
    }

    function createAddrKey(address addr) internal pure returns (uint160) {
        return uint160(addr);
    }

    function createNumKey(uint num) internal pure returns (uint160) {
        return uint160(num);
    }

}
