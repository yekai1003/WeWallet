package net.wzero.wewallet.gateway.serv.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.controller.SysParamSupport;
import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.domain.SessionData;
import net.wzero.wewallet.domain.VerificationCode;
import net.wzero.wewallet.gateway.domain.Client;
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberAccount;
import net.wzero.wewallet.gateway.domain.MemberPhone;
import net.wzero.wewallet.gateway.domain.MemberWechat;
import net.wzero.wewallet.gateway.domain.UserResource;
import net.wzero.wewallet.gateway.serv.AuthorizeService;
import net.wzero.wewallet.gateway.serv.MemberAccountService;
import net.wzero.wewallet.gateway.serv.MemberPhoneService;
import net.wzero.wewallet.gateway.serv.MemberSessionService;
import net.wzero.wewallet.gateway.serv.MemberWechatService;
import net.wzero.wewallet.gateway.serv.SessionDataService;
import net.wzero.wewallet.gateway.serv.UserResourceService;
import net.wzero.wewallet.gateway.serv.UserService;
import net.wzero.wewallet.gateway.serv.WechatService;
import net.wzero.wewallet.gateway.serv.WechatService.SessionHandler;
import net.wzero.wewallet.serv.SmsService;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.JsonUtils;
import net.wzero.wewallet.utils.RandomNumber;

@Slf4j
@Service
public class AuthorizeServiceImpl extends SysParamSupport implements AuthorizeService {

	@Autowired
	private MemberPhoneService memberPhoneService;
	@Autowired
	private MemberAccountService memberAccountService;
	@Autowired
	private MemberWechatService memberWechatService;
	@Autowired
	private UserService userService;
	@Autowired
	private UserResourceService userResourceService;
	@Autowired
	private WechatService wechatService;
	@Autowired
	private SessionDataService sessionDataService;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private MemberSessionService memberSessionService;
	@Autowired
	private SmsService smsService;

	@Override
	public SessionData login(int userId) {
		return this.login(userId,false);
	}
	@Override
	public SessionData login(int memberId, boolean isTest) { // 这是用于测试的方法，直接用userId登陆
		Member member = this.userService.findByMemberId(memberId);
		if(member == null) throw new WalletException("user_login_failed","用ID不存在");
		SessionData sd = this.doLogin(member, AppConstants.LOGIN_TYPE_ID, this.getClient().getId(), this.createMemberInfo(member));
		return sd;
	}
	
	@Override
	public SessionData login(String username, String password) {
		MemberAccount memberAccount = this.memberAccountService.findByLogin(username, password);
		if(memberAccount == null) throw new WalletException("user_login_failed","用户名不存在或者密码错误");
		SessionData sd = this.doLogin(memberAccount.getMember(), AppConstants.LOGIN_TYPE_ACCOUNT, this.getClient().getId(), this.createMemberInfo(memberAccount.getMember()));
		return sd;
	}
	
	/**
	 * 手机账户登陆，手机号如何检查
	 */
	@Override
	public SessionData phoneLogin(String phone,String verificationCode) {
		if(this.checkVerificationCode(phone, verificationCode)) {
			MemberPhone memberPhone = this.memberPhoneService.findByNumber(phone); // 查看手机用户是否存在
			if(memberPhone == null) { // memberPhone不存在，可能是新用户
				Member member = this.userService.findByPhone(phone);
				if(member == null) { // 新用户
					member = this.userService.createNormalMember();
					this.userService.updateMember(member, phone);
				}
				memberPhone = this.memberPhoneService.createMemberPhone(member, phone);
			}
			SessionData sd = this.doLogin(memberPhone.getMember(), AppConstants.LOGIN_TYPE_PHONE, this.getClient().getId(), this.createMemberInfo(memberPhone.getMember()));
			this.deleteVerificationCode(phone);
			return sd;
		} else
			throw new WalletException("verification_code_error","验证码错误，多次获取请用最后一次");
	}

	/**
	 * 微信登陆，如果没有member 可能需要创建账号
	 * 控制 同一个用户微信 在一个账号里
	 */
	@Override
	public SessionData weixinLogin(String code) throws NumberFormatException, WxErrorException {
		SessionData sd = new SessionData(); // 返回一个授权对象
		MemberWechat memberWechat = this.memberWechatService.getUserByAuth(code, this.wechatService.getWxappIdByClient(), new SessionHandler() { // 获取用户信息
			public void handler(String sessionKey) {
				if(sessionKey != null)
					sd.setWxSessionKey(sessionKey);
			}
		});
		log.info("memberWechat: " + JsonUtils.serialize(memberWechat));
		if(memberWechat.getMember() == null){
			List<MemberWechat> memberWechatOthers = this.memberWechatService.getMemberWechatsByUnionId(memberWechat.getUnionId()); // 如果没有member,通过unionid查看是否有member
			if(memberWechatOthers != null && !memberWechatOthers.isEmpty()) {
				for(int i=0;i<memberWechatOthers.size();i++) {
					MemberWechat mw = memberWechatOthers.get(i);
					if(!mw.getOpenId().equals(memberWechat.getOpenId())) {
						memberWechat = this.memberWechatService.updateMemberWechat(memberWechat, mw.getMember());
						break;
					}
				}
			}
			if(memberWechat.getMember() == null) // 创建账户
				memberWechat = this.memberWechatService.updateMemberWechat(memberWechat, this.userService.createNormalMember());
		}
		SessionData sessionData = this.doLogin(memberWechat.getMember(), sd, AppConstants.LOGIN_TYPE_WEIXIN, this.getClient().getId(), this.createMemberInfoByWeixin(memberWechat));
		return sessionData;
	}
	
	@Override
	public SessionData doLogin(Member member, Integer loginType, Integer clientId, MemberInfo memberInfo) {
		checkMemberInfo(member);
	    SessionData sd = this.sessionDataService.save(loginType, clientId, memberInfo);
	    updateMemberInfo(member, sd.getToken()); // 更新member登录信息等
	    return sd;
	}
	
	@Override
	public SessionData doLogin(Member member, SessionData sessionData, Integer loginType, Integer clientId, MemberInfo memberInfo) {
		checkMemberInfo(member);
		SessionData sd = this.sessionDataService.save(sessionData, loginType, clientId, memberInfo);
		updateMemberInfo(member, sd.getToken()); // 更新member登录信息等
		return sd;
	}
	
	@Override
	public void sendVerificationCode(String phone) {
		Client client = this.getClient();
		VerificationCode vc = new VerificationCode(); // 建立一个Session对象
		vc.setPhone(phone);
		vc.setClientId(client.getId());
		vc.setCode(RandomNumber.getVerificationCode()); // 随机一个编码
		this.redisTemplate.opsForValue().set(phone, JsonUtils.serialize(vc), 360l, TimeUnit.SECONDS);
		log.info("--------------VerificationCode:  "+ vc.getCode()+"  -------------------");
		
		Map<String,Object> model = new HashMap<>(); // 正式发短信
		model.put("vcode", vc.getCode());
		this.smsService.send(phone, "phone.vcode", model);
		log.info("--------------send success-------------");
	}
	
	@Override
	public Boolean checkVerificationCode(String phone, String verificationCode) {
		String jsonData = this.redisTemplate.opsForValue().get(phone); // 获取phone data
		if(jsonData == null) throw new WalletException("verification_code_not_found","请先获得验证码，或者验证码失效");
		VerificationCode vc = JsonUtils.deserialize(jsonData, VerificationCode.class);
		if(verificationCode.equals(vc.getCode()))
			return true;
		return false;
	}
	
	@Override
	public void deleteVerificationCode(String phone) {
		this.redisTemplate.delete(phone); // 清除验证码
	}
	
	@Override
	public void sendVerificationCode(Integer customerId, String phone) {
		Client client = this.getClient();
		VerificationCode vc = new VerificationCode(); // 建立一个Session对象
		vc.setPhone(phone);
		vc.setClientId(client.getId());
		vc.setCode(RandomNumber.getVerificationCode()); // 随机一个编码
		vc.setComCode(Integer.parseInt(vc.getCode())+customerId+"");
		this.redisTemplate.opsForValue().set(phone, JsonUtils.serialize(vc), 360l, TimeUnit.SECONDS);
		log.info("--------------VerificationCode:  "+ vc.getCode()+"  -------------------");
		Map<String,Object> model = new HashMap<>(); // 正式发短信
		model.put("vcode", vc.getCode());
//		Object sendSuccess = this.sendMessageService.sendToSms(phone, "phone.binding", JsonUtils.serialize(model) , AppConstants.SOURCE_CORE, client.getId());
		this.smsService.send(phone, "phone.vcode", model);
		log.info("--------------send success-------------");
	}
	
	@Override
	public MemberInfo createMemberInfo(Member member) {
		MemberInfo mi = new MemberInfo();
		mi.setId(member.getId());
		mi.setUserName(member.getNickname());
		mi.setPhone(member.getPhone());
		mi.setEmail(member.getEmail());
		mi.setApis(member.getUserResource().getApis());
		mi.setClients(member.getUserResource().getClients());
		mi.setLastLoginIp(member.getLastLoginIp());
		mi.setLastLoginTime(member.getLastLoginTime());
		mi.setMark(member.getMark());
		mi.setmData(member.getmData());
		mi.setGroupId(member.getGroup().getId());
		
		// 如果存在 eth env
		if(member.getmData() != null) {
			if(member.getmData().containsKey(AppConstants.ETH_ENV_KEY)) {
				mi.setCurrEnv(member.getmData().get(AppConstants.ETH_ENV_KEY));
			}
		}
		return mi;
	} 
	
	/**
	 * 登入前检查member是否可用，补全user_resource，补全member_phone
	 * @param member
	 */
	private void checkMemberInfo(Member member) {
		if(!member.getEnable()) throw new WalletException("member_not enabled","当前账户已被禁用");
		
		if(member.getUserResource() == null) {
			UserResource userResource = this.userResourceService.createUserResource(member.getGroup(), member);
			member.setUserResource(userResource);
		}
		
		if(member.getPhone() != null) {
			MemberPhone memberPhone = this.memberPhoneService.findByNumber(member.getPhone());
			if(memberPhone == null)
				this.memberPhoneService.createMemberPhone(member, member.getPhone());
		}
	}
	
	/**
	 * 更新member登陆信息，更新memberSession缓存(有缓存就删除token)
	 * @param member
	 */
	private void updateMemberInfo(Member member, String token) {
		this.userService.updateMemberLoginInfo(member);
		this.memberSessionService.updateMemberSession(member.getId(), token);
	}
	
	private MemberInfo createMemberInfoByWeixin(MemberWechat mw) {
		MemberInfo mi = this.createMemberInfo(mw.getMember());
		mi.setId(mw.getMember().getId());
		mi.setOpenId(mw.getOpenId());
		mi.setUnionId(mw.getUnionId());
		return mi;
	}
	
}
