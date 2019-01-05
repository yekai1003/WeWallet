package net.wzero.wewallet.core.controller;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.Card;
import net.wzero.wewallet.core.domain.Mnemonic;
import net.wzero.wewallet.core.repo.CardRepository;
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
	private CardRepository cardRepository;
	
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
	 * @param cardType
	 * @return
	 */
	@RequestMapping("/createCard")
	public Card createCard(@RequestParam(name="cardType")Integer cardType,
			@RequestParam(name="pwd",required=false)String pwd) {
		if(cardType == AppConstants.BITCOIN_CARD_TYPE)
			throw new WalletException("not_supported","还不支持比特币");
		if(cardType == AppConstants.ETHEREUM_CARD_TYPE) {
			if(pwd == null) throw new WalletException("pwd_exist","当选择以太币的时候pwd参数不能为空");
		}else {
			throw new WalletException("not_supported","还不支持的币种");
		}
		Card card = this.walletService.createCard(this.getMember().getId(),pwd);
		//做些啥？
		return card; 
	}
	/**
	 * 用处比较多
	 * 客户端生成助记词
	 * 或者其他方式生成的助记词
	 * @param mnemonic
	 * @param cardType
	 * @return
	 */
	@RequestMapping("/createCardByMnemonic")
	public Card createCardByMnemonic(
			@RequestParam(name="mnemonic")String mnemonic,
			@RequestParam(name="cardType")Integer cardType,
			@RequestParam(name="pwd",required=false)String pwd) {
		if(cardType == AppConstants.BITCOIN_CARD_TYPE)
			throw new WalletException("not_supported","还不支持比特币");
		if(cardType == AppConstants.ETHEREUM_CARD_TYPE) {
			if(pwd == null) throw new WalletException("pwd_exist","当选择以太币的时候pwd参数不能为空");
		}else {
			throw new WalletException("not_supported","还不支持的币种");
		}
		//验证助记词是否符合要求
		List<String> words =  Arrays.asList(mnemonic.replaceAll("\r\n", " ").split("[\\s|\n]"));
		return this.walletService.createCard(this.getMember().getId(),words, pwd);
	}
	@RequestMapping("/get")
	public Card get(@RequestParam(name = "id") Integer id) {
		return this.cardRepository.findOne(id);
	}
	/**
	 * 列出账户下所有的卡片
	 * @return
	 */
	@RequestMapping("/listCards")
	public List<Card> listCards(){
		return this.cardRepository.findByMemberId(this.getMember().getId());
	}
	/**
	 * 刷新余额
	 * 方案1、同步刷新，可能会耗费很长时间
	 * 方案2、异步刷新，UI需要多次主动调用 /core/wallet/get  来获取信息知道获取新信息
	 * @param id
	 * @return
	 */
	@RequestMapping("/refresh")
	public Card refresh(@RequestParam(name = "id") Integer id) {
		throw new WalletException("not_implemented"," 未实现");
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
