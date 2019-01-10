package net.wzero.wewallet.core.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.Account;
import net.wzero.wewallet.core.domain.Mnemonic;
import net.wzero.wewallet.core.repo.AccountRepository;
import net.wzero.wewallet.core.serv.CryptoService;
import net.wzero.wewallet.core.serv.WalletService;
import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.res.OkResponse;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

@RestController
@RequestMapping("/wallet")
public class WalletController extends BaseController {

	@Autowired
	private WalletService walletService;
	@Autowired
	private CryptoService cryptoService;
	@Autowired
	private AccountRepository accountRepository;
	
	/**
	 * 随机一组助记词
	 * @return
	 */
	@RequestMapping("/randomMnemonic")
	public Mnemonic randomMnemonic() {
		return this.cryptoService.randomMnemonic();
	}
	/**
	 * 基于随机种子创建一个钱包
	 * 这个钱包可能不基于助记词
	 * @param accountType
	 * @return
	 */
	@RequestMapping("/createAccount")
	public Account createAccount(@RequestParam(name="accountType")Integer accountType,
			@RequestParam(name="pwd",required=false)String pwd) {
		if(accountType == AppConstants.BITCOIN_ACCOUNT_TYPE)
			throw new WalletException("not_supported","还不支持比特币");
		if(accountType == AppConstants.ETHEREUM_ACCOUNT_TYPE) {
			if(pwd == null) throw new WalletException("pwd_exist","当选择以太币的时候pwd参数不能为空");
		}else {
			throw new WalletException("not_supported","还不支持的币种");
		}
		Account account = this.walletService.createAccount(this.getMember().getId(),pwd);
		//做些啥？
		return account; 
	}
	/**
	 * 用处比较多
	 * 客户端生成助记词
	 * 或者其他方式生成的助记词
	 * @param mnemonic
	 * @param accountType
	 * @return
	 */
	@RequestMapping("/createAccountByMnemonic")
	public Account createAccountByMnemonic(
			@RequestParam(name="mnemonic")String mnemonic,
			@RequestParam(name="accountType")Integer accountType,
			@RequestParam(name="pwd",required=false)String pwd) {
		if(accountType == AppConstants.BITCOIN_ACCOUNT_TYPE)
			throw new WalletException("not_supported","还不支持比特币");
		if(accountType == AppConstants.ETHEREUM_ACCOUNT_TYPE) {
			if(pwd == null) throw new WalletException("pwd_exist","当选择以太币的时候pwd参数不能为空");
		}else {
			throw new WalletException("not_supported","还不支持的币种");
		}
		//验证助记词是否符合要求
		List<String> words =  Arrays.asList(mnemonic.replaceAll("\r\n", " ").split("[\\s|\n]"));
		return this.walletService.createAccount(this.getMember().getId(),words, pwd);
	}
	@RequestMapping("/get")
	public Account get(@RequestParam(name = "id") Integer id) {
		return this.accountRepository.findOne(id);
	}
	/**
	 * 列出账户下所有的账户
	 * @return
	 */
	@RequestMapping("/listAccounts")
	public List<Account> listAccounts(){
		return this.accountRepository.findByMemberId(this.getMember().getId());
	}
	/**
	 * 刷新余额
	 * 方案1、同步刷新，可能会耗费很长时间
	 * 方案2、异步刷新，UI需要多次主动调用 /core/wallet/get  来获取信息知道获取新信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/refresh")
	public Account refresh(@RequestParam(name = "id") Integer id) {
//		throw new WalletException("not_implemented"," 未实现");
		return this.walletService.refreshBalance(this.getMember().getId(), id);
	}
	@RequestMapping("/setEnv")
	public OkResponse setEnv(@RequestParam(name = "env") String envStr) {
		try {
			EthEnv env = EthEnv.fromString(envStr);
			
			SessionData sd = this.getSessionData();
			sd.getMember().setCurrEnv(env.getName());
			this.sessionService.save(sd);
			return new OkResponse();
		}catch(java.lang.IllegalArgumentException ex) {
			ex.printStackTrace();
			throw new WalletException("env_not_found","指定的环境找不到");
		}
	}
}
