import regContractMeta from "/abi/FeedRegistry.json" assert {type: "json"};
import subContractMeta from "/abi/FeedSubscriber.json" assert {type: "json"};

const subscribeButtonHandler = function () {
    const connected = connectWallet();

    if (connected) {
        subscribe(PUB_CONTRACT_ADDRESS);
    }
}

const unsubscribeButtonHandler = function () {
    const connected = connectWallet();

    if (connected) {
        unsubscribe(PUB_CONTRACT_ADDRESS);
    }
}

const subscribeButton = document.getElementById('subscribe-button');
const unsubscribeButton = document.getElementById('unsubscribe-button');
const isSubscribedParagraph = document.getElementById('issubscribed-paragraph');
subscribeButton && subscribeButton.addEventListener('click', subscribeButtonHandler, false);
unsubscribeButton && unsubscribeButton.addEventListener('click', unsubscribeButtonHandler, false);

document.addEventListener("DOMContentLoaded", function () {
    const connected = connectWallet();
    if (connected) {
        if (isSubscribed(PUB_CONTRACT_ADDRESS)) {
            setIsSubscribed();
        } else {
            setIsUnSubscribed();
        }
    } else {
        // Show connect button
    }
});

async function connectWallet() {
    const {ethereum} = window;

    if (!ethereum) {
        console.log("Get MetaMask!");
        return false;
    }

    try {
        const accounts = await ethereum.request({method: "eth_requestAccounts"});
        const account = accounts[0];
        console.log("Connected account: ", account);
        return true;
    } catch (error) {
        console.log("Could not connect account: ", error);
        return false;
    }
}

async function subscribe(pubContractAddr) {
    let subContractAddr;
    try {
        const regContract = getRegContract();
        subContractAddr = await regContract.getSubscriberContract();
    } catch (error) {
        console.log("Could not get subscriber contract address: ", error);
        return;
    }
    if (!subContractAddr) {
        console.log("No subscriber contract exists!");
        return;
    }

    try {
        const subContract = getSubContract(subContractAddr);
        await subContract.subscribe(pubContractAddr);
        setIsSubscribed();
    } catch (error) {
        console.log("Could not create subscription: ", error);
    }
}

async function isSubscribed(pubContractAddr) {
    let subContractAddr;
    let isSubscribed = false;
    try {
        const regContract = getRegContract();
        subContractAddr = await regContract.getSubscriberContract();
    } catch (error) {
        console.log("Could not get subscriber contract address: ", error);
        return isSubscribed;
    }
    if (!subContractAddr) {
        console.log("No subscriber contract exists!");
        return isSubscribed;
    }

    try {
        const subContract = getSubContract(subContractAddr);
        const subscriptions = await subContract.getSubscriptions();


        for (let s of subscriptions) {
            if (s.pubAddress.toLowerCase() === pubContractAddr.toLowerCase()) {
                isSubscribed = true;
                break;
            }
        }
    } catch (error) {
        console.log("Could not get subs: ", error);
    }
    return isSubscribed;
}


async function unsubscribe(pubContractAddr) {
    let subContractAddr;
    try {
        const regContract = getRegContract();
        subContractAddr = await regContract.getSubscriberContract();
    } catch (error) {
        console.log("Could not get subscriber contract address: ", error);
        return;
    }
    if (!subContractAddr) {
        console.log("No subscriber contract exists!");
        return;
    }

    try {
        const subContract = getSubContract(subContractAddr);
        await subContract.unsubscribe(pubContractAddr);
        setIsUnSubscribed();
    } catch (error) {
        console.log("Could not create subscription: ", error);
    }
}

function getRegContract() {
    return new ethers.Contract(REG_CONTRACT_ADDRESS, regContractMeta.abi, getSigner());
}

function getSubContract(subContractAddr) {
    return new ethers.Contract(subContractAddr, subContractMeta.abi, getSigner());
}

function getSigner() {
    const {ethereum} = window;
    const provider = new ethers.providers.Web3Provider(ethereum);
    return provider.getSigner();
}

function setIsSubscribed() {
    subscribeButton.style.display = 'none';
    unsubscribeButton.style.display = null;
    isSubscribedParagraph.innerText = '\u2705 You are subscribed to this publisher!';
}

function setIsUnSubscribed() {
    subscribeButton.style.display = null;
    unsubscribeButton.style.display = 'none';
    isSubscribedParagraph.innerText = '\u274C You are not subscribed to this publisher!';
}