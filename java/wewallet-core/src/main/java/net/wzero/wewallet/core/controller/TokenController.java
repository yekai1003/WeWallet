package net.wzero.wewallet.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.core.serv.WalletService;

@RestController
@RequestMapping("/token")
public class TokenController extends BaseController {
	@Autowired
	private WalletService walletService;
	
	/**
	 * 添加一个token
	 * token是基于卡片的，因为token也需要一个地址
	 * @param cardId
	 * @param contractAddr
	 * @param standard
	 * @return
	 */
	@RequestMapping("/addToken")
	public Token addToken(
			@RequestParam(name="cardId")Integer cardId,
			@RequestParam(name="contractAddr")String contractAddr,
			@RequestParam(name="standard",required=false,defaultValue="erc20")String standard) {
		return this.walletService.addToken(this.getMember().getId(), cardId, contractAddr, standard);
	}
}
