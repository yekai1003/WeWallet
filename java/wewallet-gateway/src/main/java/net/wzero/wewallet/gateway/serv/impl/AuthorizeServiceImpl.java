package net.wzero.wewallet.gateway.serv.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.codec.digest.DigestUtils;
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
import net.wzero.wewallet.gateway.domain.UserGroup;
import net.wzero.wewallet.gateway.domain.UserResource;
import net.wzero.wewallet.gateway.repo.MemberAccountRepository;
import net.wzero.wewallet.gateway.repo.MemberPhoneRepository;
import net.wzero.wewallet.gateway.repo.MemberRepository;
import net.wzero.wewallet.gateway.repo.MemberWechatRepository;
import net.wzero.wewallet.gateway.repo.UserGroupRepository;
import net.wzero.wewallet.gateway.repo.UserResourceRepository;
import net.wzero.wewallet.gateway.serv.AuthorizeService;
import net.wzero.wewallet.gateway.serv.MemberSessionService;
import net.wzero.wewallet.gateway.serv.WechatService;
import net.wzero.wewallet.gateway.serv.WechatService.SessionHandler;
import net.wzero.wewallet.serv.SessionService;
import net.wzero.wewallet.serv.SmsService;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.JsonUtils;
import net.wzero.wewallet.utils.RandomNumber;

@Slf4j
@Service
public class AuthorizeServiceImpl extends SysParamSupport implements AuthorizeService {

	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MemberPhoneRepository memberPhoneRepository;
	@Autowired
	private MemberAccountRepository memberAccountRepository;
	@Autowired
	private MemberWechatRepository memberWechatRepository;
	@Autowired
	private SessionService sessionService;
	@Autowired
	private WechatService wechatService;
	@Autowired
	private RedisTemplate<String, String> redisTemplate;
	@Autowired
	private MemberSessionService memberSessionService;
	@Autowired
	private SmsService smsService;

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
	private MemberInfo createMemberInfoByWeixin(MemberWechat mw) {
		MemberInfo mi = this.createMemberInfo(mw.getMember());
		mi.setId(mw.getMember().getId());
		mi.setOpenId(mw.getOpenId());
		mi.setUnionId(mw.getUnionId());
		return mi;
	}
	
	/**
	 * 登入前检查member是否可用，补全user_resource，补全member_phone
	 * @param member
	 */
	private void checkMemberInfo(Member member) {
		if(!member.getEnable()) throw new WalletException("member_not enabled","当前账户已被禁用");
		
		if(member.getUserResource() == null) {
			UserResource userResource = new UserResource();
			UserGroup userGroup = member.getGroup();
			userResource.setMember(member);
			userResource.setApis(userGroup.getApis());
			userResource.setClients(userGroup.getClients());
			this.userResourceRepository.save(userResource);
			member.setUserResource(userResource);
		}
		
		if(member.getPhone() != null) {
			MemberPhone memberPhone = this.memberPhoneRepository.findByNumber(member.getPhone());
			if(memberPhone == null) {
				memberPhone = new MemberPhone();
				memberPhone.setNumber(member.getPhone());
				memberPhone.setMember(member);
				this.memberPhoneRepository.save(memberPhone);
			}
		}
	}
	
	/**
	 * 更新member登陆信息，更新memberSession缓存(有缓存就删除token)
	 * @param member
	 */
	private void updateMemberInfo(Member member, String token) {
		member.setLastLoginIp(this.getSystemParam().getUserIp());
		member.setLastLoginTime(new Date());
		this.memberRepository.save(member);
		
		this.memberSessionService.updateMemberSession(member.getId(), token);
	}
	
	@Override
	public SessionData login(int userId) {
		return this.login(userId,false);
	}
	@Override
	public SessionData login(int memberId,boolean isTest) {
		// 这是用于测试的方法，直接用userId登陆
		Member member = this.memberRepository.findOne(memberId);
		if(member == null) throw new WalletException("user_login_failed","用ID不存在");
		checkMemberInfo(member);
		
		SessionData sd = new SessionData();
		sd.setLoginType(AppConstants.LOGIN_TYPE_ID);
		sd.setClientId(this.getClient().getId());
	    sd.setMember(this.createMemberInfo(member));
	    sd = this.sessionService.save(sd);
	    
	    updateMemberInfo(member, sd.getToken()); // 更新member登陆信息等
		return sd;
	}
	
	@Override
	public SessionData login(String username, String password) {
		MemberAccount aMember = this.memberAccountRepository.findByLogin(username, DigestUtils.md5Hex(password).toUpperCase());
		if(aMember == null) throw new WalletException("user_login_failed","用户名不存在或者密码错误");
		checkMemberInfo(aMember.getMember());
		
		SessionData sd = new SessionData();
		sd.setLoginType(AppConstants.LOGIN_TYPE_ACCOUNT);
		sd.setClientId(this.getClient().getId());
		sd.setMember(this.createMemberInfo(aMember.getMember()));
		sd = this.sessionService.save(sd);
	    
	    updateMemberInfo(aMember.getMember(), sd.getToken()); // 更新member登陆信息等
		return sd;
	}
	
	/**
	 * 手机账户登陆，手机号如何检查
	 */
	@Override
	public SessionData phoneLogin(String phone,String verificationCode) {
		String jsonData = this.redisTemplate.opsForValue().get(phone); // 获取phone data
		if(jsonData == null) throw new WalletException("verification_code_not_found","请先获得验证码，或者验证码失效");
		VerificationCode vc = JsonUtils.deserialize(jsonData, VerificationCode.class);
		if(verificationCode.equals(vc.getCode())) {
			MemberPhone memberPhone = this.memberPhoneRepository.findByNumber(phone); // 查看手机用户是否存在
			if(memberPhone == null) { // memberPhone不存在，可能是新用户
				Member member = this.memberRepository.findByPhone(phone);
				if(member == null) { // 新用户
					member = this.createNormalMember();
					member.setPhone(phone);
					this.memberRepository.save(member);
				}
				memberPhone = new MemberPhone();
				memberPhone.setNumber(phone);
				memberPhone.setMember(member);
				memberPhone = this.memberPhoneRepository.save(memberPhone);
			}
			checkMemberInfo(memberPhone.getMember());
			
			SessionData sd = new SessionData();
			sd.setLoginType(AppConstants.LOGIN_TYPE_PHONE);
			sd.setClientId(this.getClient().getId());
			sd.setMember(this.createMemberInfo(memberPhone.getMember())); // 手机登陆一般 member 都已经有了
			sd = this.sessionService.save(sd);
			
			updateMemberInfo(memberPhone.getMember(), sd.getToken()); // 更新member登陆信息
			this.redisTemplate.delete(phone); // 清除验证码
			return sd;
		}else
			throw new WalletException("verification_code_error","验证码错误，多次获取请用最后一次");
	}

	/**
	 * 微信登陆，如果没有member 可能需要创建账号
	 * 控制 同一个用户微信 在一个账号里
	 */
	@Override
	public SessionData weixinLogin(String code) throws NumberFormatException, WxErrorException {
		Client client = this.getClient();
		if(client.getClientData() == null) // 检查是否配置
			 throw new WalletException("weixin_config_empty","客户端没有配置微信");
		String wxAppIdStr = client.getClientData().get(AppConstants.WX_APP_ID_KEY); // 获取 wxAppId
		if(wxAppIdStr == null) throw new WalletException("weixin_login_not_allowed","客户端不允许微信登录或者未配置微信登录");

		SessionData sd = new SessionData(); // 返回一个授权对象
		sd.setLoginType(AppConstants.LOGIN_TYPE_WEIXIN);
		MemberWechat userWx = this.wechatService.getUserByAuth(code, Integer.parseInt(wxAppIdStr),new SessionHandler() { // 获取用户信息
			public void handler(String sessionKey) {
				if(sessionKey != null)
					sd.setWxSessionKey(sessionKey);
			}
		});
		sd.setClientId(client.getId());
		log.info("userWx: " + JsonUtils.serialize(userWx));
		if(userWx.getMember() == null){
			// 如果没有member,通过unionid 查看是否有member
			List<MemberWechat> userWxOthers = this.memberWechatRepository.findByUnionId(userWx.getUnionId());
			if(userWxOthers != null && !userWxOthers.isEmpty()) {
				for(int i=0;i<userWxOthers.size();i++) {
					MemberWechat mw = userWxOthers.get(i);
					if(!mw.getOpenId().equals(userWx.getOpenId())) {
						userWx.setMember(mw.getMember()); //已经存在的肯定有，如果没有会创建的
						userWx = this.memberWechatRepository.save(userWx);
						break;
					}
				}
			}
		}
		if(userWx.getMember() == null) { // 创建账户
			Member m = this.createNormalMember();
			userWx.setMember(m);
			userWx = this.memberWechatRepository.save(userWx);
		}
		checkMemberInfo(userWx.getMember());
		
		sd.setMember(this.createMemberInfoByWeixin(userWx));
		SessionData sessionData = this.sessionService.save(sd);
		log.info("-----memberid:"+sessionData.getMember().getId());
		
		updateMemberInfo(userWx.getMember(), sessionData.getToken()); // 更新member登陆信息
		return sessionData;
	}
	
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private UserResourceRepository userResourceRepository;
	
	private Member createNormalMember() {
		Member m = new Member();
		UserGroup ug = this.userGroupRepository.findOne(3);//3号是普通用户组
		m.setGroup(ug);
		m.setEnable(true);
		m.setLastLoginIp(this.getSystemParam().getUserIp());
		m.setLastLoginTime(new Date());
		m = this.memberRepository.save(m);
		
		UserResource ur = m.getUserResource(); //设置资源
		if(ur == null) {
			ur = new UserResource();
		}
		ur.setClients(ug.getClients());
		ur.setApis(ug.getApis());
		ur.setMember(m);
		this.userResourceRepository.save(ur);
		m.setUserResource(ur);
		m = this.memberRepository.findOne(m.getId());
		if(m.getUserResource() == null)
			log.info("用户资源为空");
		else
			log.info(JsonUtils.serialize(m.getUserResource()));
		return m;
	}
	
	@Override
	public void sendVerificationCode(String phone) {
		Client client = this.getClient();
		// 建立一个Session对象
		VerificationCode vc = new VerificationCode();
		vc.setPhone(phone);
		vc.setClientId(client.getId());
		//随机一个编码
		vc.setCode(RandomNumber.getVerificationCode());
		this.redisTemplate.opsForValue().set(phone, JsonUtils.serialize(vc), 360l, TimeUnit.SECONDS);
		log.info("--------------VerificationCode:  "+ vc.getCode()+"  -------------------");
		
		Map<String,Object> model = new HashMap<>(); // 正式发短信
		model.put("vcode", vc.getCode());
		this.smsService.send(phone, "phone.vcode", model);
		log.info("--------------send success-------------");
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
}
