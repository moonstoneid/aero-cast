import { createApp } from "/js/vue.js";

const appTmpl = `
<h1>Demo Aggregator</h1>

<p v-if="error != null">{{ error }}</p>

<div v-if="account === null">
  <p>Connect your wallet.</p>
  <button v-on:click="$event => handleConnectWallet()">Connect</button>
</div>

<div v-if="account !== null && !hasRegistered">
  <p>You are currently not registered.</p>
  <button v-on:click="$event => handleRegister()">Register</button>
</div>

<div v-if="account !== null && hasRegistered">
  <p>Here is your latest feed.</p>
  <table border="1">
    <tr>
      <th>Pub Date</th>
      <th>Title</th>
      <th>Description</th>
      <th>Link</th>
    </tr>
    <tr v-for="entry in entries">
      <td>{{ entry.date }}"</td>
      <td>{{ entry.title }}</td>
      <td>{{ entry.description }}</td>
      <td><a v-bind:href="entry.url" />{{ entry.url }}</td>
    </tr>
  </table>
</div>

<div v-if="account !== null && hasRegistered">
  <button v-on:click="$event => handleUnregister()">Unregister</button>
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
}

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
}

const unregister = async function(account) {
    const response = await fetch("/subscriber/" + account, {
        method: "DELETE"
    });
    if (response.ok || response.status === 404) {
        return;
    }
    throw new Error("A unknown error occurred!");
}

const fetchEntries = async function(account) {
    const response = await fetch("/subscriber/" + account + "/entries");
    if (response.ok) {
        return response.json();
    }
    if (response.status === 404) {
        throw new Error("The subscriber account was not found!");
    } else {
        throw new Error("A unknown error occurred!");
    }
}

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
        }
    }
}).mount("#app");