package net.wzero.wewallet.core.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.web3j.utils.Convert;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.Transaction;
import net.wzero.wewallet.core.repo.TransactionRepository;
import net.wzero.wewallet.core.serv.TxService;
import net.wzero.wewallet.res.OkResponse;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

@RestController
@RequestMapping("/tx")
public class TxController extends BaseController {

	@Autowired
	private TxService txSerrvice;
	@Autowired
	private TransactionRepository transactionRepository;
	
	/**
	 * 转账业务设计
	 * 为了提高用户体验，不应该在用户请求的时候直接转账
	 * 具体流程如下
	 * 1、创建一个交易数据
	 * 2、发送一个转账消息到RabbitMQ
	 * 3、返回创建成功消息
	 * 4、转账业务订阅RabbitMQ 消息（其目的是为了随着业务量的提升，防止瓶颈）
	 * 5、某台转账服务器获得消息后，操作实际的转账业务，当业务量井喷的时候，随时扩大服务器量
	 * 6、转账完成后，发送成功后的消息到RabbitMQ
	 * 7、交易创建服务器订阅发送成功的消息，并接收消息，更新交易数据（可能会有两个消息：1、发送交易，2、检查状态）
	 * @param cardId
	 * @param toAddress
	 * @param value
	 * @param unit
	 * @return 交易结构体
	 */
	@RequestMapping("/transfer")
	public Transaction transfer(
			@RequestParam(name = "cardId") Integer cardId, 
			@RequestParam(name = "payPwd") String payPwd,
			@RequestParam(name = "toAddress") String toAddress,
			@RequestParam(name = "value") String value,
			@RequestParam(name = "unit") String unit,
			@RequestParam(name = "env",required=false) String env) {
		
		BigDecimal val = Convert.toWei(new BigDecimal(value), Convert.Unit.fromString(unit));
		if(val.compareTo(new BigDecimal("0")) <= 0) 
			throw new WalletException("value_error","转账金额必须大于0");
		//决定环境
		if(env == null)
			env = this.getMember().getCurrEnv();
		if(env == null) throw new WalletException("env_not_set","选择当前钱包环境");
		return this.txSerrvice.createTransaction(this.getMember().getId(), cardId, toAddress, val,EthEnv.fromString(env), payPwd);
	}
	@RequestMapping("/get")
	public Transaction get(@RequestParam(name = "id") Integer id) {
		Transaction tmp = this.transactionRepository.findOne(id);
		if(tmp == null) throw new WalletException("id_not_exist","交易ID不存在");
		return tmp;
	}
	@RequestMapping("/list")
	public List<Transaction> list() {
		return this.transactionRepository.findByMemberId(this.getMember().getId());
	}
	/**
	 * 刷新交易，这里是手动的刷新，理论上服务器也会自动去刷新
	 * @param id 交易ID号
	 * @return
	 */
	@RequestMapping("/refresh")
	public OkResponse refresh(@RequestParam(name = "id") Integer id) {
		// 获取交易
		Transaction tmp = this.transactionRepository.findOne(id);
		if(tmp == null) throw new WalletException("id_not_exist","交易ID不存在");
		// 检查操作者是否是自己
		if(this.getMember().getId() != tmp.getMemberId())
			throw new WalletException("op_failed","不能操作别人的交易");
		// 异步完成
		this.txSerrvice.refreshTransaction(tmp);
		return new OkResponse();
	}
}
