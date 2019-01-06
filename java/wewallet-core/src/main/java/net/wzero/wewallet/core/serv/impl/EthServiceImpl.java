package net.wzero.wewallet.core.serv.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.EthGetTransactionReceipt;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.EthereumCard;
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
		// TODO Auto-generated method stub
		EthereumCard card = (EthereumCard)this.cardRepository.findByMemberIdAndAddr(transaction.getMemberId(), transaction.getFromAddr());
		try {
			ECKeyPair kp = KeystoreUtils.readKeystore(card.getKeystore(), pwd);
			// 获取认证信息
			Credentials credentials = Credentials.create(kp);
			Web3j web3j = ethEnvMap.get(transaction.getEnv());
			if(web3j == null) throw new WalletException("env_error","不存在指定环境");
			// 获取认证信息
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("get_transaction_failed","应该是网络异常!");
		}
		
	}


}
