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
import org.web3j.protocol.core.DefaultBlockParameter;
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
import net.wzero.wewallet.core.domain.Account;
import net.wzero.wewallet.core.domain.Balance;
import net.wzero.wewallet.core.domain.EthereumAccount;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.repo.AccountRepository;
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
	private AccountRepository accountRepository;
	
	@Override
	public Transaction sendTransaction(Transaction transaction, String pwd) {
		// ether 的发送交易，这里获取 account 对象的方式不对，后期需要通过restTemplate 获取
		EthereumAccount account = (EthereumAccount)this.accountRepository.findByMemberIdAndAddr(transaction.getAccount().getMemberId(), transaction.getFromAddr());
		try {
			ECKeyPair kp = KeystoreUtils.readKeystore(account.getKeystore(), pwd);
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
		// Token 交易的发送,这里获取 account 对象的方式不对，后期需要通过restTemplate 获取
		EthereumAccount account = (EthereumAccount)this.accountRepository.findByMemberIdAndAddr(transaction.getAccount().getMemberId(), transaction.getFromAddr());
		try {
			ECKeyPair kp = KeystoreUtils.readKeystore(account.getKeystore(), pwd);
			// 获取认证信息
			Credentials credentials = Credentials.create(kp);
			Web3j web3j = ethEnvMap.get(transaction.getEnv());
			if(web3j == null) throw new WalletException("env_error","不存在指定环境");
			// 获取一个发送账户的 有效nonce
			EthGetTransactionCount ethGetTransactionCount = web3j
					.ethGetTransactionCount("0x"+account.getAddr(), DefaultBlockParameterName.LATEST).sendAsync().get();
			BigInteger nonce = ethGetTransactionCount.getTransactionCount();
			log.info("nonce->\t" + nonce);
			//创建合约交易 data
			String methodName ="transfer";
			@SuppressWarnings("rawtypes")
			List<Type> inputParameters = new ArrayList<>();
			List<TypeReference<?>> outputParameters = new ArrayList<>();
			// Address
			Address toAddr = new Address("0x"+transaction.getToAddr());
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
					RawTransaction.createTransaction(nonce, DefaultGasProvider.GAS_PRICE, DefaultGasProvider.GAS_LIMIT, "0x"+transaction.getContractAddr(), data);
			// 编码 RawTransaction 对象 ,签名
			byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
			String hexValue = Numeric.toHexString(signedMessage);
			
			// 发送
			EthSendTransaction ethSendTransaction = web3j.ethSendRawTransaction(hexValue).sendAsync().get();
			log.info("txHash->\t"+ethSendTransaction.getTransactionHash());
			// 会提示有错误，但是交易却能成功，一次这里不抛出异常，除非 txHash 没有才异常,我估计这个方法会查询 交易状态，但是这个时候交易状态还没完成
			if(ethSendTransaction.hasError()) {
				//throw new WalletException("send_transaction_failed",ethSendTransaction.getError().getMessage());
				// 如果有错误 看下是否有txHash
				if(ethSendTransaction.getTransactionHash() != null) {// 保存下交易 txHash
					transaction.setTxHash(ethSendTransaction.getTransactionHash());
					transaction.setNonce(nonce.longValue());
					return transaction;
				}else {
					// 如果交易hash 也没有，无语了，无法进行下去了,
					return transaction;
				}
			}else {//没有错误再去查询
				transaction.setNonce(nonce.longValue());
				// 获取 交易hash ,这个交易最好保存，方便后期查询 状态
				String txHash = ethSendTransaction.getTransactionHash();
				// 这里只更新下 txHash ，获取交易结果
				EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(txHash).send();
				transaction.setTxHash(txHash);
				// 如果没有错误 保存详细数据，有错误忽略掉
				if(!transactionReceipt.hasError()) {
					transaction.setStatus(new BigInteger(transactionReceipt.getResult().getStatus().substring(2),16).toString(10));
					transaction.setGasLimit(DefaultGasProvider.GAS_LIMIT+"");
					transaction.setGasPrice(DefaultGasProvider.GAS_PRICE+"");
					transaction.setGasUsed(transactionReceipt.getResult().getGasUsed()+"");
					transaction.setCumulativeGasUsed(transactionReceipt.getResult().getCumulativeGasUsed()+"");
				}
				return transaction;
			}
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
		// 这里不需要账户信息，因为 查询交易不需要签名
//		EthereumAccount account = (EthereumAccount)this.accountRepository.findByMemberIdAndAddr(transaction.getMemberId(), transaction.getFromAddr());
		// 通过交易的 环境获取 客户端实例
		Web3j web3j = ethEnvMap.get(transaction.getEnv());

		try {
			EthGetTransactionReceipt transactionReceipt = web3j.ethGetTransactionReceipt(transaction.getTxHash()).send();
			log.info("transactionHash->\t" + transactionReceipt.getResult().getTransactionHash());
			
			// transaction.setTxHash(transactionReceipt.getResult().getTransactionHash()); // 通过交易txHash查询，回来不应该重新设置
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
	public Account refreshEthBalance(EthereumAccount account,String envs) {
		String [] envArr = envs.split("\\|");
		for (String env : envArr) {
			log.info("--env:\t"+env);
			Web3j web3 = ethEnvMap.get(env);
//			web3.ethBlockNumber().send().getBlockNumber()|DefaultBlockParameter.valueOf("latest")
			try {
//				EthGetBalance balance = web3.ethGetBalance(account.getAddr(), new DefaultBlockParameterNumber(web3.ethBlockNumber().send().getBlockNumber())).send();
				// 数据库里的地址是没有 0x 的
				EthGetBalance balance = web3.ethGetBalance("0x"+account.getAddr(), DefaultBlockParameter.valueOf("latest")).send();
				log.info("---result:"+balance.getResult());
				log.info("---balance:"+balance.getBalance().toString(10));
				account.getBalances().put(env, new Balance(balance.getBalance().toString(10),false));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return account;
	}

	@Override
	public Token refreshTokenBalance(Token token) {
		Web3j web3j = ethEnvMap.get(token.getEnv());//token 肯定是部署在某个 环境上的，因此 token结构体里应该自带 env
		String methodName = "balanceOf";
		@SuppressWarnings("rawtypes")
		List<Type> inputParameters = new ArrayList<>();
		List<TypeReference<?>> outputParameters = new ArrayList<>();
		// 输入参数
		Address address = new Address("0x"+token.getAccount().getAddr());
		inputParameters.add(address);
		// 返回参数
		TypeReference<Uint256> typeReference = new TypeReference<Uint256>() {};
		outputParameters.add(typeReference);
		Function function = new Function(methodName,inputParameters,outputParameters);
		String data = FunctionEncoder.encode(function);
		org.web3j.protocol.core.methods.request.Transaction transaction 
				= org.web3j.protocol.core.methods.request.Transaction.createEthCallTransaction(
						"0x"+token.getAccount().getAddr(), // 这里的地址 是否也需要 0x 开头？
						"0x"+token.getContractAddr(),
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
