package net.wzero.wewallet.core.serv.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthCall;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.Card;
import net.wzero.wewallet.core.domain.EthereumCard;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.repo.CardRepository;
import net.wzero.wewallet.core.serv.EthService;
import net.wzero.wewallet.core.utils.KeystoreUtils;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

@Slf4j
@Service
public class EthServiceImpl implements EthService,InitializingBean {

	private Map<String,Web3j> ethEnvMap = new HashMap<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		//循环初始化 Web3j客户端
		for (EthEnv env : EthEnv.values()) {
			Web3j web3j = Web3j.build(new HttpService(env.getUrl()));

			this.ethEnvMap.put(env.getName(), web3j);
		}
		
	}
	@Autowired
	private CardRepository cardRepository;
	
	@Override
	public Transaction sendTransaction(Transaction transaction, String pwd) {
		// ether 的发送交易，这里获取 card 对象的方式不对，后期需要通过restTemplate 获取
		EthereumCard card = (EthereumCard)this.cardRepository.findByMemberIdAndAddr(transaction.getMemberId(), transaction.getFromAddr());
		try {
			ECKeyPair kp = KeystoreUtils.readKeystore(card.getKeystore(), pwd);
			// 获取认证信息
			Credentials credentials = Credentials.create(kp);
			Web3j web3j = ethEnvMap.get(transaction.getEnv());
			if(web3j == null) throw new WalletException("env_error","不存在指定环境");
			// 发送交易
			TransactionReceipt transactionReceipt = Transfer
					.sendFunds(web3j, credentials, transaction.getToAddr(), new BigDecimal(transaction.getValue()), Convert.Unit.WEI).send();
			log.info("transactionHash->\t" + transactionReceipt.getTransactionHash());
			transaction.setTxHash(transactionReceipt.getTransactionHash());
			transaction.setStatus(new BigInteger(transactionReceipt.getStatus().substring(2),16).toString(10));
			transaction.setGasLimit(DefaultGasProvider.GAS_LIMIT+"");
			transaction.setGasPrice(DefaultGasProvider.GAS_PRICE+"");
			transaction.setGasUsed(transactionReceipt.getGasUsed()+"");
			transaction.setCumulativeGasUsed(transactionReceipt.getCumulativeGasUsed()+"");
			return transaction;
		} catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","keystore json 格式错误");
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","keystore json 转换失败");
		} catch (CipherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","keystore 解码失败");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","网络异常");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","Interrupted:\t"+e.getMessage());
		} catch (TransactionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","Transaction:\t"+e.getMessage());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","未知错误");
		}
	}
	
	@Override
	public Transaction sendTokenTransaction(Transaction transaction, String pwd) {
		// Token 交易的发送,这里获取 card 对象的方式不对，后期需要通过restTemplate 获取
		EthereumCard card = (EthereumCard)this.cardRepository.findByMemberIdAndAddr(transaction.getMemberId(), transaction.getFromAddr());
		try {
			ECKeyPair kp = KeystoreUtils.readKeystore(card.getKeystore(), pwd);
			// 获取认证信息
			Credentials credentials = Credentials.create(kp);
			Web3j web3j = ethEnvMap.get(transaction.getEnv());
			if(web3j == null) throw new WalletException("env_error","不存在指定环境");
			// 获取一个发送账户的 有效nonce
			EthGetTransactionCount ethGetTransactionCount = web3j
					.ethGetTransactionCount(card.getAddr(), DefaultBlockParameterName.LATEST).sendAsync().get();
			BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			log.info("nonce->\t" + nonce);
			//创建合约交易 data
			String methodName ="transfer";
			@SuppressWarnings("rawtypes")
			List<Type> inputParameters = new ArrayList<>();
			List<TypeReference<?>> outputParameters = new ArrayList<>();
			// Address
			Address toAddr = new Address(transaction.getToAddr());
			// amount 
			Uint256 amount  = new Uint256(new BigInteger(transaction.getValue()));
			// 方法参数
			inputParameters.add(toAddr);
			inputParameters.add(amount);
			// 方法返回值
			TypeReference<Bool> typeReference = new TypeReference<Bool>() {};
			outputParameters.add(typeReference);
			// 方法组装
			Function function = new Function(methodName, inputParameters, outputParameters);
			// 编码
			String data = FunctionEncoder.encode(function);
			// 创建 RawTransaction 对象，这个创建方法是用来交易token  
			// to 应该是 Contract address
			RawTransaction rawTransaction = 
					RawTransaction.createTransaction(nonce, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT, transaction.getContractAddr(), data);
			// 编码 RawTransaction 对象 ,签名
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			// 发送
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
			if(ethSendTransaction.hasError())
				throw new WalletException("send_transaction_failed",ethSendTransaction.getError().getMessage());
			// 获取 交易hash ,这个交易最好保存，方便后期查询 状态
			String txHash = ethSendTransaction.getTransactionHash();
			// 这里之更新下 txHash ，获取交易结果
			EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(txHash).send();
			transaction.setTxHash(txHash);
			if(!transactionReceipt.hasError()) {
				transaction.setStatus(new BigInteger(transactionReceipt.getResult().getStatus().substring(2),16).toString(10));
				transaction.setGasLimit(DefaultGasProvider.GAS_LIMIT+"");
				transaction.setGasPrice(DefaultGasProvider.GAS_PRICE+"");
				transaction.setGasUsed(transactionReceipt.getResult().getGasUsed()+"");
				transaction.setCumulativeGasUsed(transactionReceipt.getResult().getCumulativeGasUsed()+"");
			}
			return transaction;
		}  catch (JsonParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","keystore json 格式错误");
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","keystore json 转换失败");
		} catch (CipherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","keystore 解码失败");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","网络异常");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","Interrupted:\t"+e.getMessage());
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("send_transaction_failed","ExecutionException:\t"+e.getMessage());
		} 
	}



	@Override
	public Transaction getTransactionReceipt(Transaction transaction) {
		// 这里不需要卡片信息，因为 查询交易不需要签名
//		EthereumCard card = (EthereumCard)this.cardRepository.findByMemberIdAndAddr(transaction.getMemberId(), transaction.getFromAddr());
		// 通过交易的 环境获取 客户端实例
		Web3j web3j = ethEnvMap.get(transaction.getEnv());

		try {
			EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transaction.getTxHash()).send();
			log.info("transactionHash->\t" + transactionReceipt.getResult().getTransactionHash());
			
			transaction.setTxHash(transactionReceipt.getResult().getTransactionHash());
			transaction.setStatus(new BigInteger(transactionReceipt.getResult().getStatus().substring(2),16).toString(10));
			transaction.setGasLimit(DefaultGasProvider.GAS_LIMIT+"");
			transaction.setGasPrice(DefaultGasProvider.GAS_PRICE+"");
			transaction.setGasUsed(transactionReceipt.getResult().getGasUsed()+"");
			transaction.setCumulativeGasUsed(transactionReceipt.getResult().getCumulativeGasUsed()+"");
			return transaction;
		} catch (IOException e) {
			e.printStackTrace();
			throw new WalletException("get_transaction_failed","应该是网络异常!");
		}
		
	}

	/**
	 * 和交易不同，这里刷新余额 的环境应该是 session里当前的环境
	 * 但是这里不是web 请求，而是消息订阅服务，因此没有session可以获取...
	 * 带参数过来吧!
	 */
	@Override
	public Card refreshEthBalance(EthereumCard card,String env) {
		Web3j web3 = ethEnvMap.get(env);
		try {
			EthGetBalance balance = web3.ethGetBalance(card.getAddr(), DefaultBlockParameterName.LATEST).send();
			card.setBalance(balance.getBalance().toString(10));
			card.setIsRefreshing(false);
			return card;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("io_exception",e.getMessage());
		}
	}

	@Override
	public Token refreshTokenBalance(Token token) {
		Web3j web3j = ethEnvMap.get(token.getEnv());//token 肯定是部署在某个 环境上的，因此 token结构体里应该自带 env
		String methodName = "balanceOf";
		@SuppressWarnings("rawtypes")
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();
		// 输入参数
		Address address = new Address(token.getCard().getAddr());
		inputParameters.add(address);
		// 返回参数
		TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {};
		outputParameters.add(typeReference);
		Function function = new Function(methodName,inputParameters,outputParameters);
		String data = FunctionEncoder.encode(function);
		org.web3j.protocol.core.methods.request.Transaction transaction 
				= org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
						token.getCard().getAddr(), 
						token.getContractAddr(),
						data);
		
		try {
			EthCall ethCall = web3j.ethCall(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
			@SuppressWarnings("rawtypes")
			List<Type> results = FunctionReturnDecoder.decode(ethCall.getValue(), function.getOutputParameters());
			token.setBalance(results.get(0).getValue().toString());
			token.setIsRefreshing(false);
			return token;
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
