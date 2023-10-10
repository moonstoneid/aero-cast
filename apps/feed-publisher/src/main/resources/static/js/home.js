import regContractMeta from "/abi/FeedRegistry.json" assert {type: "json"};
import subContractMeta from "/abi/FeedSubscriber.json" assert {type: "json"};

const subscribeButtonHandler = async function () {
    if (await connectWallet()) {
        await subscribe(PUB_CONTRACT_ADDRESS);
    }
}

const unsubscribeButtonHandler = async function () {
    if (await connectWallet()) {
        await unsubscribe(PUB_CONTRACT_ADDRESS);
    }
}

const subscribeButton = document.getElementById('subscribe-button');
const unsubscribeButton = document.getElementById('unsubscribe-button');
subscribeButton && subscribeButton.addEventListener('click', subscribeButtonHandler, false);
unsubscribeButton && unsubscribeButton.addEventListener('click', unsubscribeButtonHandler, false);

document.addEventListener("DOMContentLoaded", async function () {
    if (!await connectWallet()) {
        // Show connect button
        return;
    }

    if (await isSubscribed(PUB_CONTRACT_ADDRESS)) {
        setIsSubscribed();
    } else {
        setIsUnSubscribed();
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
    try {
        const regContract = getRegContract();
        subContractAddr = await regContract.getSubscriberContract();
    } catch (error) {
        console.log("Could not get subscriber contract address: ", error);
        return false;
    }
    if (!subContractAddr) {
        console.log("No subscriber contract exists!");
        return false;
    }

    let subscriptions;
    try {
        const subContract = getSubContract(subContractAddr);
        subscriptions = await subContract.getSubscriptions();
    } catch (error) {
        console.log("Could not get subs: ", error);
        return false;
    }

    let isSubscribed = false;
    for (let s of subscriptions) {
        if (s.pubAddress.toLowerCase() === pubContractAddr.toLowerCase()) {
            isSubscribed = true;
            break;
        }
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
}

function setIsUnSubscribed() {
    subscribeButton.style.display = null;
    unsubscribeButton.style.display = 'none';
}