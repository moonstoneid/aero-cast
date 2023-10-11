import { createApp } from "/js/vue.js";

const appTmpl = `
<div class="mb-5">
  <div class="mb-3">
    <h1>Feed Aggregator</h1>
  </div>

  <p v-if="error != null">{{ error }}</p>
  
  <div v-if="account === null" class="mb-4">
    <p>Connect your wallet.</p>
    <button class="btn btn-primary" v-on:click="$event => handleConnectWallet()">Connect</button>
  </div>
  
  <div v-if="account !== null && !hasRegistered" class="mb-4">
    <p>You are currently not registered.</p>
    <button class="btn btn-primary" v-on:click="$event => handleRegister()">Register</button>
  </div>
  
  <div v-if="account !== null && hasRegistered" class="mb-4">
    <button class="btn btn-light" v-on:click="$event => handleUnregister()">Unregister</button>
  </div>
</div>

<div v-if="account !== null && hasRegistered" class="mb-4">
  <div v-if="!entries.length">
    <p>There is currently nothing for you ...</p>
  </div>
  <div v-for="entry in entries" class="mb-4">
    <h5><a v-on:click.prevent="$event => toggleEntryVisibility(entry)" href="#" target="_blank">{{ entry.title }}</a></h5>
    <p class="small">{{ entry.date }}</p>
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
        throw new Error("Make sure you have MetaMask!");
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
        console.log(error);
        throw new Error("Could not retrieve Ethereum account!");
    }
};

const requestAccount = async function() {
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
        console.log(error)
        throw new Error("Could not connect Ethereum account!");
    }
};

const fetchHasRegistered = async function(account) {
    const response = await fetch("/subscriber/" + account);
    if (response.ok) {
        return true;
    }
    if (response.status === 404) {
        return false;
    }
    throw new Error("A unknown error occurred!");
};

const register = async function(account) {
    const response = await fetch("/subscriber/" + account, {
        method: "POST"
    });
    if (response.ok) {
        return;
    }
    if (response.status === 404) {
        throw new Error("The subscriber account was not found!");
    } else {
        throw new Error("A unknown error occurred!");
    }
};

const unregister = async function(account) {
    const response = await fetch("/subscriber/" + account, {
        method: "DELETE"
    });
    if (response.ok || response.status === 404) {
        return;
    }
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
        throw new Error("The subscriber account was not found!");
    } else {
        throw new Error("A unknown error occurred!");
    }
};

const mapEntries = function(entries) {
    const es = entries.map(e => (
        {
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
    const df = new Intl.DateTimeFormat('en-US', {
        weekday: 'short',
        day: 'numeric',
        month: 'short',
        year: 'numeric',
    });
    return df.format(date);
};

createApp({
    template: appTmpl,
    data() {
        return {
            error: null,
            account: null,
            hasRegistered: false,
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
            this.hasRegistered = await fetchHasRegistered(this.account);
            if (!this.hasRegistered) {
                return;
            }
            this.entries = await fetchEntries(this.account);
        } catch (e) {
            this.error = e;
        }
    },
    methods: {
        async handleConnectWallet() {
            this.error = null;
            try {
                this.account = await requestAccount();
            } catch (e) {
                this.error = e;
            }
        },
        async handleRegister() {
            this.error = null;
            try {
                await register(this.account);
                this.hasRegistered = true;
                this.entries = await fetchEntries(this.account);
            } catch (e) {
                this.error = e;
            }
        },
        async handleUnregister() {
            this.error = null;
            try {
                await unregister(this.account);
                this.hasRegistered = false;
                this.entries = [];
            } catch (e) {
                this.error = e;
            }
        },
        toggleEntryVisibility(entry) {
            const visible = entry.visible;
            this.entries.forEach(e => {e.visible = false;});
            entry.visible = !visible;
        }
    }
}).mount("#app");