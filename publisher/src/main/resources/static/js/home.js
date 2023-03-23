import regContractMeta from "/abi/FeedRegistry.json" assert { type: "json" };
import subContractMeta from "/abi/FeedSubscriber.json" assert { type: "json" };

const subscribeButtonHandler = function() {
    const connected = connectWallet();
    if (connected) {
        isSubscribed(PUB_CONTRACT_ADDRESS);
      //  subscribe(PUB_CONTRACT_ADDRESS);
    }
}

const subscribeButton = document.getElementById('subscribe-button');
subscribeButton && subscribeButton.addEventListener('click', subscribeButtonHandler, false);

async function connectWallet() {
    const { ethereum } = window;

    if (!ethereum) {
        console.log("Get MetaMask!");
        return false;
    }

    try {
        const accounts = await ethereum.request({ method: "eth_requestAccounts" });
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
        subscribeButton.textContent = '\u2705 Subscribed. Click to unsubscribe.';
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
        return;
    }
    if (!subContractAddr) {
        console.log("No subscriber contract exists!");
        return;
    }

    try {
        const subContract = getSubContract("0xB7A5bd0345EF1Cc5E66bf61BdeC17D2461fBd968");
        await subContract.getSubscriptions();
        console.log("test");
    } catch (error) {
        console.log("Could not get subs: ", error);
    }
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
        subscribeButton.textContent = '\u1F50C Unsubscribed. Click to subscribe.';
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
    const { ethereum } = window;
    const provider = new ethers.providers.Web3Provider(ethereum);
    return provider.getSigner();
}
