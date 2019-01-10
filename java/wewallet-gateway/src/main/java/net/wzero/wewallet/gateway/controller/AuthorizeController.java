package net.wzero.wewallet.gateway.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.gateway.res.AuthorizeResponse;
import net.wzero.wewallet.gateway.serv.AuthorizeService;
import net.wzero.wewallet.res.OkResponse;
import net.wzero.wewallet.res.WalletResponse;
import net.wzero.wewallet.utils.ValidateUtils;

@Slf4j
@RestController
@RequestMapping("/authorize")
public class AuthorizeController extends BaseController {
	
	@Autowired
	private AuthorizeService authorizeService;
	
	@RequestMapping(value="/idLogin")
	AuthorizeResponse idLogin(@RequestParam(name="memberId")Integer memberId) {
		throw new WalletException("null","测试时候用的!");
//		log.info("user Id login:"+memberId);
//		SessionData sd = this.authorizeService.login(memberId);
//		AuthorizeResponse res = new AuthorizeResponse();
//		res.setMemberId(sd.getMember().getId());
//		res.setToken(sd.getToken());
//		res.setExpired(sd.getExpired());
//		return res;
	}
	
	@RequestMapping("/login")
	AuthorizeResponse login(@RequestParam(name = "username") String username,
			@RequestParam(name = "password") String password) {
		log.info("login:"+this.getClient().getClientName());
		SessionData sd = this.authorizeService.login(username, password);
		AuthorizeResponse res = new AuthorizeResponse();
		res.setToken(sd.getToken());
		res.setExpired(sd.getExpired());
		return res;
	}
	
	@RequestMapping("/phoneLogin")
	AuthorizeResponse phoneLogin(@RequestParam(name="phone")String phone,@RequestParam(name="vcode")String vcode) throws WxErrorException {
		SessionData sd = this.authorizeService.phoneLogin(phone, vcode); // state是 clientId 这个是约定
		AuthorizeResponse res = new AuthorizeResponse();
		res.setToken(sd.getToken());
		res.setExpired(sd.getExpired());
		return res;
	}
	
	@RequestMapping(value="/wxLogin")
	AuthorizeResponse wxLogin(@RequestParam(name="code")String code) throws WxErrorException {
		SessionData sd = null;
		if("the code is a mock one".equals(code)) {//测试端发出的信息
			sd = this.authorizeService.login(1,true);//1号id是管理员,第二个参数表示 测试
		}else {
			sd = this.authorizeService.weixinLogin(code); //state是 clientId 这个是约定
		}
		AuthorizeResponse res = new AuthorizeResponse();
		res.setToken(sd.getToken());
		res.setExpired(sd.getExpired());
		return res;
	}
	
	@RequestMapping("/sendVerificationCode")
	WalletResponse sendVerificationCode(@RequestParam(name="phone")String phone) {
		ValidateUtils.validatePhone(phone);
		this.authorizeService.sendVerificationCode(phone);
		return new OkResponse();
	}
		
}
