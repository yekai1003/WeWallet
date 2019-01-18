package net.wzero.wewallet.core;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.core.methods.response.Web3ClientVersion;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.AccountType;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

public class TestAction {

	@Test
	public void doAction1() {
		AccountType ct = AccountType.builder().name("一个名字").rootPath("m/44'/60'/0'/0/0").build();
		// Account c = Account
		AccountType ct2 = AccountType.builder().name("一个名字").rootPath("m/44'/60'/0'/0/0").build();
		System.out.println(ct2.equals(ct));
		System.out.println("id->" + ct.getId() + "\t" + ct.getName() + "\t" + ct.getRootPath());
	}

	@Test
	public void doAction2() throws IOException {
		Web3j web3 = Web3j.build(new HttpService(AppConstants.ROPSTEN));
		Web3ClientVersion web3ClientVersion = web3.web3ClientVersion().send();
		System.out.println(web3ClientVersion.getWeb3ClientVersion());
	}

	@Test
	public void doAction3() throws IOException, CipherException {
		// Credentials credentials = WalletUtils.loadCredentials("123",
		// "{\"address\":\"4c998ed8e1a43091a467a5fb3b065f024d0d6a0b\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"6989de299cdf6732b05dd9add89cfbd2bf332540f3b73dc4915713cd414f9e55\",\"cipherparams\":{\"iv\":\"a47f46c23e6f012d27889789825fa3e4\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"850be717b15310d44087c0b271536de17a2c4ace8c248cf245b905c8e1aacb8c\"},\"mac\":\"02f03c91b5cac9384f8cd23377336451f194e4a7d91595aacfaafba7454e97af\"},\"id\":\"e06482b2-26b1-4cdc-ae4f-404f2848a70f\",\"version\":3}");

		final ObjectMapper objectMapper = new ObjectMapper();
		WalletFile walletFile = objectMapper.readValue(
				"{\"address\":\"4c998ed8e1a43091a467a5fb3b065f024d0d6a0b\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"6989de299cdf6732b05dd9add89cfbd2bf332540f3b73dc4915713cd414f9e55\",\"cipherparams\":{\"iv\":\"a47f46c23e6f012d27889789825fa3e4\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"850be717b15310d44087c0b271536de17a2c4ace8c248cf245b905c8e1aacb8c\"},\"mac\":\"02f03c91b5cac9384f8cd23377336451f194e4a7d91595aacfaafba7454e97af\"},\"id\":\"e06482b2-26b1-4cdc-ae4f-404f2848a70f\",\"version\":3}",
				WalletFile.class);
		Credentials credentials = Credentials.create(Wallet.decrypt("123", walletFile));
		System.out.println(credentials.getAddress());
		System.out.println(credentials.getEcKeyPair().getPrivateKey());
		System.out.println(credentials.getEcKeyPair().getPublicKey());

	}

	private final ObjectMapper objectMapper = new ObjectMapper();

	private Credentials createCredentials(String keystore, String pwd)
			throws JsonParseException, JsonMappingException, IOException, CipherException {
		WalletFile walletFile = objectMapper.readValue(keystore, WalletFile.class);
		Credentials credentials = Credentials.create(Wallet.decrypt(pwd, walletFile));
		return credentials;
	}

	/**
	 * 这是一个离线签名方式的交易,也是本钱包的处理方式 ----------------------------------------------------
	 * 获取一个发送账户的 有效nonce Identify the next available nonce for the sender account 创建
	 * RawTransaction 交易对象 Create the RawTransaction object 用Recursive Length
	 * Prefix编码方式 编码 RawTransaction 对象 Encode the RawTransaction object using
	 * Recursive Length Prefix encoding RawTransaction 对象签名 Sign the RawTransaction
	 * object 发送 RawTransaction 到一个节点去处理 Send the RawTransaction object to a node
	 * for processing
	 * 
	 * @throws ExecutionException
	 * @throws InterruptedException
	 * @throws CipherException
	 * @throws IOException
	 * @throws JsonMappingException
	 * @throws JsonParseException
	 */
	@Test
	public void doAction4() throws InterruptedException, ExecutionException, JsonParseException, JsonMappingException,
			IOException, CipherException {
		String senderAddress = "0xC1F741b993F8715468b5e7c3B3ff62541aF7A578";
		String keystore = "{\"address\":\"C1F741b993F8715468b5e7c3B3ff62541aF7A578\",\"id\":\"fa57721d-970f-4670-b4c8-b3c1b56917a7\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"886fc254bef85a4e1a45debffd12fedff824bef9684c75118a28b675039e0f25\",\"cipherparams\":{\"iv\":\"78adc05ddc1e314dbc44b4b36f060872\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"36c502c6412fe24ec2010fb5afac19fe28954d414cc0d4e47126cef7ffd80c60\"},\"mac\":\"da4f7d1785aabdbe8ca2a6e82b4af98b013d357e439db4b8304b3315541dc05e\"}}\r\n";
		String toAddress = "0xa527c6Faf3eD312EA20E9fDC680aCaBF32d42740";
		// 定义个发送量
		BigInteger value = Convert.toWei("0.05", Convert.Unit.ETHER).toBigInteger();
		// 创建web3j对象
		Web3j web3j = Web3j.build(new HttpService(AppConstants.ROPSTEN));
		// 获取一个发送账户的 有效nonce
		EthGetTransactionCount ethGetTransactionCount = web3j
				.ethGetTransactionCount(senderAddress, DefaultBlockParameterName.LATEST).sendAsync().get();

		BigInteger nonce = ethGetTransactionCount.getTransactionCount();
		System.out.println("nonce->\t" + nonce);
		// 创建 RawTransaction 对象，这个创建方法是用来交易以太币
		RawTransaction rawTransaction = RawTransaction.createEtherTransaction(nonce, DefaultGasProvider.GAS_PRICE,
				BigInteger.valueOf(3000000l), toAddress, value);
		// 这个方法是用来创建智能合约交易的，因为没带value，个人认为是这样
		//RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, data)
		//这个方法既可以创建只能合约交易，又可以创建以太币交易
		//RawTransaction.createTransaction(nonce, gasPrice, gasLimit, to, value, data)
		// 证书对象 从头keystore 获得，这里可能需要重写 WalletUtils对象
		// Credentials credentials = WalletUtils.loadCredentials("123",
		// "/path/to/walletfile");
		// 模拟下从 keystore 转换
		Credentials credentials = createCredentials(keystore, "123");
		// 编码 RawTransaction 对象 ,签名
		byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
		String hexValue = Numeric.toHexString(signedMessage);
		// 发送
		// -----------------
		// 发送对象
		EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
		// 获取 交易hash ,这个交易最好保存，方便后期查询 状态
		String transactionHash = ethSendTransaction.getTransactionHash();
		System.out.println("transactionHash->\t" + transactionHash);

	}

	/**
	 * 另外一种推荐的方法 这个方法可以不考虑nonce 它负责管理现时管理和轮询响应
	 * 
	 * @throws Exception
	 * @throws TransactionException
	 * @throws InterruptedException
	 */
	@Test
	public void doAction5() throws InterruptedException, TransactionException, Exception {
		// String senderAddress = "0xC1F741b993F8715468b5e7c3B3ff62541aF7A578";
		String keystore = "{\"address\":\"C1F741b993F8715468b5e7c3B3ff62541aF7A578\",\"id\":\"fa57721d-970f-4670-b4c8-b3c1b56917a7\",\"version\":3,\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"886fc254bef85a4e1a45debffd12fedff824bef9684c75118a28b675039e0f25\",\"cipherparams\":{\"iv\":\"78adc05ddc1e314dbc44b4b36f060872\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"36c502c6412fe24ec2010fb5afac19fe28954d414cc0d4e47126cef7ffd80c60\"},\"mac\":\"da4f7d1785aabdbe8ca2a6e82b4af98b013d357e439db4b8304b3315541dc05e\"}}\r\n";
		String toAddress = "0xa527c6Faf3eD312EA20E9fDC680aCaBF32d42740";
		// 定义个发送量 这里不需要BigInteger 就可以 这里是转换前的值，因此不需要先转成 wei，否则余额不够，闹腾了一早上
		BigDecimal value = BigDecimal.valueOf(0.03);// Convert.toWei("0.03", Convert.Unit.ETHER);
		// 创建web3j对象
		Web3j web3j = Web3j.build(new HttpService(AppConstants.ROPSTEN));
		// 获取认证信息
		Credentials credentials = createCredentials(keystore, "123");
		TransactionReceipt transactionReceipt = Transfer
				.sendFunds(web3j, credentials, toAddress, value, Convert.Unit.ETHER).send();
		
		System.out.println("transactionHash->\t" + transactionReceipt.getTransactionHash());
	}

	/**
	 * 查看交易结果
	 * 
	 * @throws IOException
	 */
	@Test
	public void doAction6() throws IOException {
		String txHash = "0xa7530405d6e25ab92634ab22ead8421e34f2bc89d2e6fd8e8c28d48c853b5802";// "0x02ea84431cac0a2819c263a274b5ae4f4f511509e82746bb4aa40aeaee48d15e";
		// 创建web3j对象
		Web3j web3j = Web3j.build(new HttpService(AppConstants.ROPSTEN));

		EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(txHash).send();

		System.out.println(transactionReceipt.getResult().getGasUsedRaw());
		System.out.println(transactionReceipt.getResult().getGasUsed());
		// transactionReceipt.g
	}

	@Test
	public void doAction7() {
		// org.web3j.tx.gas.DefaultGasProvider
//		System.out.println(Integer.parseInt("5208", 16));
		BigInteger balance = new BigInteger("11c37937e080000",16);
		System.out.println(balance.toString(10));
	}

	/**
	 * 查询余额
	 * @throws IOException 
	 */
	@Test
	public void doAction8() throws IOException {
		// 创建web3j对象
		Web3j web3j = Web3j.build(new HttpService(AppConstants.PINKEBY));
		//DefaultBlockParameter
		EthGetBalance ethGetBalance = web3j.ethGetBalance("0xa527c6Faf3eD312EA20E9fDC680aCaBF32d42740", DefaultBlockParameter.valueOf("latest")).send();
		System.out.println(ethGetBalance.getResult());
		System.out.println(ethGetBalance.getBalance().toString(10));
		System.out.println(Integer.parseInt(ethGetBalance.getResult().substring(2), 16));
//		System.out.println(Long.parseLong(ethGetBalance.getResult().substring(2), 16));
//		BigInteger balance = new BigInteger(ethGetBalance.getResult(),16);
//		System.out.println(balance.toString(10));
		
	}
	@Test
	public void doAction9() {
		EthEnv envStr = EthEnv.fromString("ropsten");
		System.out.println(envStr);
//		String value = "1.0";
//		BigDecimal val = Convert.toWei(new BigDecimal(value), Convert.Unit.fromString("ether"));
//		System.out.println(val.toBigInteger().toString());
	}
	@Test
	public void doAction10() {
		String tmp = "a|b|c|d";
		String[] tmps = tmp.split("\\|");
		for (int i = 0; i < tmps.length; i++) {
			System.out.println(tmps[i]);
		}
	}
	@Test
	public void doAction11() {
		Web3j web3j = Web3j.build(new HttpService(AppConstants.ROPSTEN));
		String acctAddress = "C1F741b993F8715468b5e7c3B3ff62541aF7A578";
		String contractAddress = "0xa82927975eAA40a9E6EB9BF42C946Df637267CDB";
		String methodName = "balanceOf";
		@SuppressWarnings("rawtypes")
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();
		// 输入参数
		Address address = new Address(acctAddress);
		inputParameters.add(address);
		// 返回参数
		TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {};
		outputParameters.add(typeReference);
		Function function = new Function(methodName,inputParameters,outputParameters);
		String data = FunctionEncoder.encode(function);
		org.web3j.protocol.core.methods.request.Transaction transaction 
				= org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
						acctAddress, // 这里的地址 是否也需要 0x 开头？
						contractAddress,
						data);
		
		try {
			EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			@SuppressWarnings("rawtypes")
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			System.out.println(results.get(0).getValue().toString());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("op_failed",e.getMessage());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("op_failed",e.getMessage());
		}
	}
}
