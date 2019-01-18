package net.wzero.wewallet.core.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.core.repo.TokenRepository;
import net.wzero.wewallet.core.serv.WalletService;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

@RestController
@RequestMapping("/token")
public class TokenController extends BaseController {
	@Autowired
	private WalletService walletService;
	@Autowired
	private TokenRepository tokenRepository;

	@RequestMapping("/get")
	public Token get(@RequestParam(name="id")Integer tokenId) {
		Token token = this.tokenRepository.findOne(tokenId);
		if(token == null) throw new WalletException("id_not_exist","ID不存在");
		return token;
	}
	/**
	 * 添加一个token
	 * token是基于账户的，因为token也需要一个地址
	 * @param accountId
	 * @param contractAddr
	 * @param standard
	 * @return
	 */
	@RequestMapping("/addToken")
	public Token addToken(
			@RequestParam(name="accountId")Integer accountId,
			@RequestParam(name="env") String envStr,
			@RequestParam(name="contractAddr")String contractAddr,
			@RequestParam(name="standard",required=false,defaultValue="erc20")String standard,
			@RequestParam(name="icon",required=false)String icon,
			@RequestParam(name="name",required=false)String name,
			@RequestParam(name="tokenSymbol",required=false)String symbol,// symbol关键字 js上
			@RequestParam(name="decimals",required=false, defaultValue="0")Integer decimals) {
		EthEnv env = EthEnv.fromString(envStr);
		decimals = decimals & 0x00ff;
		return this.walletService.addToken(this.getMember().getId(), accountId,env, contractAddr, standard, icon, name, symbol, decimals);
	}

	@RequestMapping("/list")
	public List<Token> list(
			@RequestParam(name="accountId")Integer accountId,
			@RequestParam(name="env")String env){
		List<Token> tokens = this.tokenRepository.findByAccountIdAndEnv(accountId, env);
		if(tokens == null)
			tokens = new ArrayList<>(); // 不抛出异常了
		return tokens;
	}
	/**
	 * 和 accountId 一样实现异步刷新
	 * 和交易一样使用同一个消息通道
	 * 不同的是 这里的消息不是同一个类的实例
	 * @return
	 */
	@RequestMapping("/refresh")
	public Token refresh(@RequestParam(name="tokenId")Integer tokenId) {
		return this.walletService.refreshTokenBalance(this.getMember().getId(), tokenId);
	}
	
	@RequestMapping("/updateToken")
	public Token updateToken(
			@RequestParam(name="tokenId")Integer tokenId,
			@RequestParam(name="standard",required=false)String standard,
			@RequestParam(name="contractAddr",required=false)String contractAddr,
			@RequestParam(name="icon",required=false)String icon) {
		Token token = this.walletService.findByTokenId(tokenId);
		if(token == null)
			throw new WalletException("param_error", tokenId+"号token不存在");
		return this.walletService.updateTokn(token, contractAddr, standard, icon);
	}
}
