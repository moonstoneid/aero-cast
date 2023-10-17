const { ethereum } = window;

let regContractMeta;
let subContractMeta;

const connectWalletButtonHandler = async function () {
    try {
        hideError();
        const isWalletConnected = await connectWallet();
        if (!isWalletConnected) {
            return;
        }
        await checkIsSubscribed();
    } catch (error) {
        showError(error);
    }
}

const subscribeButtonHandler = async function () {
    try {
        hideError();
        await subscribe();
    } catch (error) {
        showError(error);
    }
}

const unsubscribeButtonHandler = async function () {
    try {
        hideError();
        await unsubscribe();
    } catch (error) {
        showError(error);
    }
}

const errorContainer = document.getElementById('error-container');
const errorMessageText = errorContainer.children[0];
const errorCloseButton = errorContainer.children[1];
const connectWalletButton = document.getElementById('connect-wallet-button');
const subscribeButton = document.getElementById('subscribe-button');
const unsubscribeButton = document.getElementById('unsubscribe-button');
errorCloseButton && errorCloseButton.addEventListener('click', (e) => hideError(), false);
connectWalletButton && connectWalletButton.addEventListener('click', connectWalletButtonHandler, false);
subscribeButton && subscribeButton.addEventListener('click', subscribeButtonHandler, false);
unsubscribeButton && unsubscribeButton.addEventListener('click', unsubscribeButtonHandler, false);

document.addEventListener("DOMContentLoaded", async function () {
    try {
        await fetchContractAbis();
        const isWalletConnected = await hasConnectedWallet();
        if (!isWalletConnected) {
            return;
        }
        await checkIsSubscribed();
    } catch (error) {
        showError(error);
    }
});

async function fetchContractAbis() {
    regContractMeta = await fetchContractAbi("/abi/FeedRegistry.json");
    subContractMeta = await fetchContractAbi("/abi/FeedSubscriber.json");
}

async function fetchContractAbi(path) {
    const response = await fetch(path);
    if (response.ok) {
        return response.json();
    }
    console.log("Could not fetch contract ABI: ", response);
    throw new Error("Could not retrieve contract ABI!");
}

async function hasConnectedWallet() {
    const account = await getAccount();
    const isWalletConnected = account !== null;
    setWalletConnected(isWalletConnected);
    return isWalletConnected;
}

async function getAccount() {
    if (!ethereum) {
        console.log("No browser wallet has been found!");
        return null;
    }

    try {
        const accounts = await ethereum.request({ method: "eth_accounts" });
        if (accounts.length === 0) {
            console.log("No account is connected.");
            return null;
        }

        const account = accounts[0];
        console.log("Connected account: ", account);
        return account;
    } catch (error) {
        if (error.code === 4001) {
            console.log("No account is connected.");
            return null;
        }
        console.log("Could not connect account: ", error);
        throw new Error("Could not retrieve Ethereum account!");
    }
}

async function connectWallet() {
    try {
        const account = await requestAccount();
        const isWalletConnected = account !== null;
        setWalletConnected(isWalletConnected);
        return isWalletConnected;
    } catch (error) {
        setWalletConnected(false);
        throw error;
    }
}

async function requestAccount() {
    if (!ethereum) {
        console.log("No browser wallet has been found!");
        throw new Error("No browser wallet has been found. Make sure you have installed a browser " +
            "wallet like MetaMask!");
    }

    try {
        const accounts = await ethereum.request({method: "eth_requestAccounts"});
        if (accounts.length === 0) {
            console.log("No account was connected!");
            return null;
        }

        const account = accounts[0];
        console.log("Connected account: ", account);
        return account;
    } catch (error) {
        if (error.code === 4001) {
            console.log("No account was connected!");
            return null;
        }
        console.log("Could not connect account: ", error);
        throw new Error("Could not retrieve Ethereum account!");
    }
}

async function getSubContractAddress() {
    let subContractAddr;
    try {
        const regContract = getRegContract();
        subContractAddr = await regContract.getSubscriberContract();
    } catch (error) {
        console.log("Could not get subscriber contract address: ", error);
        throw new Error("Could not retrieve subscriber information. Make sure you are connected " +
            "to the correct network!");
    }

    if (isEmptyAddress(subContractAddr)) {
        console.log("No subscriber contract exists!");
        throw new Error("Your account is not registered as a subscriber. Please create a subscriber " +
            "account first!");
    }

    return subContractAddr;
}

async function checkIsSubscribed() {
    setSubscribed(false);

    const subContractAddr = await getSubContractAddress();
    const subContract = getSubContract(subContractAddr);

    let subscriptions;
    try {
        subscriptions = await subContract.getSubscriptions();
    } catch (error) {
        console.error("Could not retrieve subscriptions!", error);
        throw new Error("Could not retrieve subscriptions!");
    }

    let isSubscribed = false;
    for (let s of subscriptions) {
        if (areAddressesEqual(s.pubAddress, PUB_CONTRACT_ADDRESS)) {
            isSubscribed = true;
            break;
        }
    }

    setSubscribed(isSubscribed);
}

async function subscribe() {
    const subContractAddr = await getSubContractAddress();
    const subContract = getSubContract(subContractAddr);

    try {
        await subContract.subscribe(PUB_CONTRACT_ADDRESS);
        setSubscribed(true);
    } catch (error) {
        console.log("Could not create subscription: ", error);
        throw new Error("Could not create subscription!");
    }
}

async function unsubscribe() {
    const subContractAddr = await getSubContractAddress();
    const subContract = getSubContract(subContractAddr);

    try {
        await subContract.unsubscribe(PUB_CONTRACT_ADDRESS);
        setSubscribed(false);
    } catch (error) {
        console.log("Could not remove subscription: ", error);
        throw new Error("Could not remove subscription!");
    }
}

function getRegContract() {
    return new ethers.Contract(REG_CONTRACT_ADDRESS, regContractMeta.abi, getSigner());
}

function getSubContract(subContractAddr) {
    return new ethers.Contract(subContractAddr, subContractMeta.abi, getSigner());
}

function getSigner() {
    if (!ethereum) {
        setWalletConnected(false);
        throw new Error("No browser wallet has been found. Make sure you have installed a browser " +
            "wallet like MetaMask!");
    }

    const provider = new ethers.providers.Web3Provider(ethereum);
    if (provider === null) {

    }

    return provider.getSigner();
}

function showError(error) {
    errorMessageText.textContent = error.message;
    errorContainer.style.display = null;
}

function hideError() {
    errorMessageText.textContent = '';
    errorContainer.style.display = 'none';
}

function setWalletConnected(connected) {
    if (connected) {
        connectWalletButton.style.display = 'none';
    } else {
        connectWalletButton.style.display = null;
        subscribeButton.style.display = 'none';
        unsubscribeButton.style.display = 'none';
    }
}

function setSubscribed(subscribed) {
    if (subscribed) {
        unsubscribeButton.style.display = null;
        subscribeButton.style.display = 'none';
    } else {
        subscribeButton.style.display = null;
        unsubscribeButton.style.display = 'none';
    }
}

function isEmptyAddress(address) {
    return address === null || address === "0x0000000000000000000000000000000000000000";
}

function areAddressesEqual(address1, address2) {
    if (address1 === null || address2 === null) {
        return false;
    }
    return address1.toLowerCase() === address2.toLowerCase();
}