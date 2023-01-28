const { expect } = require('chai');

describe('Hello contract', () => {
  let contract;

  beforeEach(async () => {
    const contractFactory = await hre.ethers.getContractFactory("Hello");
    contract = await contractFactory.deploy();
  });

  it('Correct initial counter', async () => {
    expect(await contract.getHelloCount()).to.equal(0);
  });

  it('Correct counter after hello', async () => {
    await contract.hello();
    expect(await contract.getHelloCount()).to.equal(1);
  });
});
