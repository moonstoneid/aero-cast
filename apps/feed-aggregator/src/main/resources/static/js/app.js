import { createApp } from "/js/vue.js";

const appTmpl = `
<div class="mb-5">
  <div class="mb-3">
    <h1>Feed Aggregator</h1>
  </div>
  
  <div v-if="error !== null" class="alert alert-warning alert-dismissible">
    <span>{{ error }}</span>
    <button class="btn-close" aria-label="Close" v-on:click="$event => error = null"></button>
  </div>
  
  <div v-if="account === null" class="mb-4">
    <p>Connect your wallet.</p>
    <button class="btn btn-primary" v-on:click="$event => handleConnectWallet()">Connect</button>
  </div>
  
  <div v-if="account !== null && !isEnlisted" class="mb-4">
    <p>You are currently not enlisted.</p>
    <button class="btn btn-primary" v-on:click="$event => handleEnlist()">Enlist</button>
  </div>
  
  <div v-if="account !== null && isEnlisted" class="mb-4">
    <button class="btn btn-light" v-on:click="$event => handleDelist()">Delist</button>
  </div>
</div>

<div v-if="account !== null && isEnlisted" class="mb-4">
  <div v-if="!entries.length">
    <p>There is currently nothing for you ...</p>
  </div>
  <div v-for="entry in entries" class="mb-4">
    <h5><a v-on:click.prevent="$event => toggleEntryVisibility(entry)" href="#" target="_blank">
        {{ entry.title }}</a></h5>
    <div class="d-flex flex-row align-items-center mb-2">
      <img class="publisher-icon me-2" v-bind:src="entry.pubFavicon">
      <p class="small m-0">{{ entry.pubName }} - {{ entry.date }}</p>
    </div>
    <div v-if="entry.visible">
      <p>{{ entry.description }}</p>
      <p><a v-bind:href="entry.url" target="_blank">Read more ...</a></p>
    </div>
  </div>
</div>
`;

const { ethereum } = window;

const getAccount = async function() {
    if (!ethereum) {
        throw new Error("No browser wallet has been found. Make sure you have installed a browser " +
            "wallet like MetaMask!");
    }

    try {
        const accounts = await ethereum.request({ method: "eth_accounts" });
        if (accounts.length === 0) {
            console.log("No account is connected.");
            return null;
        }

        const account = accounts[0];
        console.log("Connected account: ", account);
        return account
    } catch (error) {
        if (error.code === 4001) {
            console.log("No account is connected.");
            return null;
        }
        console.log("Could not get Ethereum account!\n", error)
        throw new Error("Could not retrieve Ethereum account!");
    }
};

const requestAccount = async function() {
    if (!ethereum) {
        throw new Error("No browser wallet has been found. Make sure you have installed a browser " +
            "wallet like MetaMask!");
    }

    try {
        const accounts = await ethereum.request({ method: "eth_requestAccounts" });
        if (accounts.length === 0) {
            console.log("No account was connected!");
            return null;
        }

        const account = accounts[0];
        console.log("Connected account: ", account);
        return account
    } catch (error) {
        if (error.code === 4001) {
            console.log("No account was connected!");
            return null;
        }
        console.log("Could not request Ethereum account!\n", error)
        throw new Error("Could not connect Ethereum account!");
    }
};

const fetchIsEnlisted = async function(account) {
    const response = await fetch("/subscriber/" + account);
    if (response.ok) {
        return true;
    }
    if (response.status === 404) {
        return false;
    }
    console.log("Could not fetch subscriber status!\n", response);
    throw new Error("A unknown error occurred!");
};

const enlist = async function(account) {
    const response = await fetch("/subscriber/" + account, {
        method: "POST"
    });
    if (response.ok) {
        return;
    }
    if (response.status === 404) {
        throw new Error("Your account is not registered as a subscriber. Please create a subscriber " +
            "account first!");
    }
    console.log("Could not enlist subscriber!\n", response);
    throw new Error("A unknown error occurred!");
};

const delist = async function(account) {
    const response = await fetch("/subscriber/" + account, {
        method: "DELETE"
    });
    if (response.ok || response.status === 404) {
        return;
    }
    console.log("Could not delist subscriber!\n", response);
    throw new Error("A unknown error occurred!");
};

const fetchEntries = async function(account) {
    const response = await fetch("/subscriber/" + account + "/entries");
    if (response.ok) {
        return response
            .json()
            .then(entries => mapEntries(entries));
    }
    if (response.status === 404) {
        throw new Error("Your account is not enlisted. Please enlist first.");
    }
    console.log("Could not fetch subscriber entries!\n", response);
    throw new Error("A unknown error occurred!");
};

const mapEntries = function(entries) {
    const es = entries.map(e => (
        {
            pubFavicon: "/publisher/" + e.pubContractAddress + "/favicon.ico",
            pubName: e.pubName,
            title: e.title,
            date: formatDate(Date.parse(e.date)),
            description: e.description,
            url: e.url,
            visible: false,
        }
    ));
    if (es.length > 0) {
        es[0].visible = true;
    }
    return es;
};

const formatDate = function(date) {
    const df = new Intl.DateTimeFormat("en-US", {
        weekday: "short",
        day: "numeric",
        month: "short",
        year: "numeric",
    });
    return df.format(date);
};

createApp({
    template: appTmpl,
    data() {
        return {
            error: null,
            account: null,
            isEnlisted: false,
            entries: []
        };
    },
    async created() {
        this.error = null;
        try {
            this.account = await getAccount();
            if (this.account === null) {
                return;
            }
            this.isEnlisted = await fetchIsEnlisted(this.account);
            if (!this.isEnlisted) {
                return;
            }
            this.entries = await fetchEntries(this.account);
        } catch (e) {
            this.error = e.message;
        }
    },
    methods: {
        async handleConnectWallet() {
            this.error = null;
            try {
                this.account = await requestAccount();
            } catch (e) {
                this.error = e.message;
            }
        },
        async handleEnlist() {
            this.error = null;
            try {
                await enlist(this.account);
                this.isEnlisted = true;
                this.entries = await fetchEntries(this.account);
            } catch (e) {
                this.error = e.message;
            }
        },
        async handleDelist() {
            this.error = null;
            try {
                await delist(this.account);
                this.isEnlisted = false;
                this.entries = [];
            } catch (e) {
                this.error = e.message;
            }
        },
        toggleEntryVisibility(entry) {
            const visible = entry.visible;
            this.entries.forEach(e => {e.visible = false;});
            entry.visible = !visible;
        }
    }
}).mount("#app");