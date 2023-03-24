import { createApp } from "/js/vue.js";

const appTmpl = `
<h1>Demo aggregator</h1>

<p v-if="error != null">{{ error }}</p>

<div v-if="account === null">
  <p>Connect your wallet.</p>
  <button v-on:click="$event => handleConnectWallet()">Connect</button>
</div>

<div v-if="account !== null && !hasUserEnrolled">
  <p>You are currently not enrolled.</p>
  <button v-on:click="$event => handleEnrollUser()">Enroll</button>
</div>

<div v-if="account !== null && hasUserEnrolled">
  <p>Here is your latest feed.</p>
  <table border="1">
    <tr>
      <th>Pub Date</th>
      <th>Title</th>
      <th>Description</th>
      <th>Link</th>
    </tr>
    <tr v-for="entry in entries">
      <td>{{ entry.title }}</td>
      <td>{{ entry.description }}</td>
      <td>{{ entry.date }}"</td>
      <td><a v-bind:href="entry.url" />{{ entry.url }}</td>
    </tr>
  </table>
</div>
`;

const { ethereum } = window;

const getAccount = async function() {
    if (!ethereum) {
        console.log("Make sure you have MetaMask!");
        // TODO: Throw exception
        return null;
    }

    try {
        const accounts = await ethereum.request({ method: "eth_accounts" });

        if (accounts.length === 0) {
            console.log("No authorized account found!");
            return null;
        }

        const account = accounts[0];
        console.log("Connected account: ", account);
        return account
    } catch (error) {
        console.log(error);
        // TODO: Throw exception
        return null;
    }
};

const requestAccount = async function() {
    try {
        const accounts = await ethereum.request({ method: "eth_requestAccounts" });

        if (accounts.length === 0) {
            console.log("No authorized account found!");
            return null;
        }

        const account = accounts[0];
        console.log("Connected account: ", account);
        return account
    } catch (error) {
        console.log(error)
        // TODO: Throw exception
        return null;
    }
};

const fetchHasUserEnrolled = async function() {
    // TODO: Implement
    return false;
}

const enrollUser = async function(account) {
    // TODO: Implement
}

const fetchEntries = async function() {
    // TODO: Implement
    return [];
}

createApp({
    template: appTmpl,
    data() {
        return {
            error: null,
            account: null,
            hasUserEnrolled: false,
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
            this.hasUserEnrolled = await fetchHasUserEnrolled();
            if (!this.hasUserEnrolled) {
                return;
            }
            this.entries = await fetchEntries();
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
        async handleEnrollUser() {
            this.error = null;
            try {
                await enrollUser(this.account);
                this.hasUserEnrolled = true;
            } catch (e) {
                this.error = e;
            }
        }
    }
}).mount("#app");