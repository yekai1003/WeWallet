package net.wzero.wewallet.gateway.controller;

import java.util.HashMap;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.domain.SessionData;
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
import net.wzero.wewallet.gateway.repo.UserGroupRepository;
import net.wzero.wewallet.gateway.repo.UserResourceRepository;
import net.wzero.wewallet.gateway.serv.MemberSessionService;
import net.wzero.wewallet.gateway.serv.WechatService;
import net.wzero.wewallet.res.OkResponse;
import net.wzero.wewallet.serv.SessionService;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.AppConstants.EthEnv;
import net.wzero.wewallet.utils.JsonUtils;
import net.wzero.wewallet.utils.ValidateUtils;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

	@Autowired
	private MemberPhoneRepository memberPhoneRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private UserResourceRepository userResourceRepository;
	@Autowired
	private MemberAccountRepository memberAccountRepository;
	@Autowired
	private UserGroupRepository userGroupRepository;
	@Autowired
	private WechatService wechatService;
	@Autowired
	private MemberSessionService memberSessionService;
	
	/**
	 * 用户创建member、member_account
	 * @param loginName
	 * @param loginPwd
	 * @return
	 */
	@RequestMapping("/join")
	public OkResponse join(
			@RequestParam(name="loginName")String loginName,
			@RequestParam(name="loginPwd")String loginPwd) {
		if(loginName.trim().length() < 6)
			throw new WalletException("login_name_length_exception","登录名长度至少为6位");
		MemberAccount tmpMa = this.memberAccountRepository.findByLoginName(loginName); // 检查 loginname存在否
		if(tmpMa != null) throw new WalletException("login_name_exist","登录名存在");
		
		Member member = addMember(loginName.trim(), loginName.trim(), loginPwd, AppConstants.NORMAL_USER_GROUP_ID, null);
		if(ValidateUtils.validateEmail(loginName.trim())) { // 如果登录名是邮箱，保存邮箱
			member.setEmail(loginName.trim());
			this.memberRepository.save(member);
		} 
		return new OkResponse();
	}
	
	/**
	 * 管理员创建member、member_account
	 * @param userName
	 * @param loginName
	 * @param loginPwd
	 * @param groupId
	 * @param phone
	 * @return
	 */
	@Transactional
	@RequestMapping("/addMember")
	public Member addMember(
			@RequestParam(name="userName")String userName,
			@RequestParam(name="loginName")String loginName,
			@RequestParam(name="loginPwd")String loginPwd,
			@RequestParam(name="groupId") Integer groupId,
			@RequestParam(name="phone",required=false)String phone) {
		MemberAccount tmpMa = this.memberAccountRepository.findByLoginName(loginName); // 检查 loginname存在否
		if(tmpMa != null) throw new WalletException("login_name_exist","登录名存在");
		UserGroup ug = this.userGroupRepository.findOne(groupId);
		if(ug == null) throw new WalletException("user_group_not_exist","用户组不存在");
		if(userName.trim().length() < 1) throw new WalletException("param_error","username不能为空");
		
		Member m = new Member();
		m.setGroup(ug);
		UserResource userResource = new UserResource(); // 设置用户资源
		userResource.setApis(ug.getApis());
		userResource.setClients(ug.getClients());
		userResource.setMember(m);
		this.userResourceRepository.save(userResource);
		m.setUserResource(userResource);
		m.setEnable(true);
		m.setNickname(userName);
		if(phone != null) {
			ValidateUtils.validatePhone(phone);
			if(this.memberRepository.findByPhone(phone) != null) throw new WalletException("phone_exist_member","该手机号已存在与其它账户中");
			m.setPhone(phone);
			
			MemberPhone memberPhone = this.memberPhoneRepository.findByNumber(phone);
			if(memberPhone != null) throw new WalletException("phone_exist_member_phone","该手机号已存在与member_phone中");
			memberPhone = new MemberPhone();
			memberPhone.setMember(m);
			memberPhone.setNumber(phone);
			this.memberPhoneRepository.save(memberPhone);
		}
		m = this.memberRepository.save(m);
		
		MemberAccount ma = new MemberAccount();
		ma.setMember(m);
		ma.setLoginName(loginName);
		ma.setLoginPwd(DigestUtils.md5Hex(loginPwd).toUpperCase());
		this.memberAccountRepository.save(ma);
		return m;
	}
	
	/**
	 * 根据memberId获取member
	 * @param mid
	 * @return
	 */
	@RequestMapping("/getMember")
	public Member getMember(@RequestParam(name="mid") int mid) {
		Member member = this.memberRepository.findOne(mid);
		if(member == null) throw new WalletException("member_not_found","账户不存在");
		return member;
	}
	@Autowired
	private SessionService sessionService;
	
	/**
	 * 保存到 member mData里，下次启动加载配置到session
	 * @return
	 */
	@RequestMapping("/setCurrEnv")
	public OkResponse setCurrEnv(@RequestParam(name="env") String envName) {
		EthEnv env = EthEnv.valueOf(envName);
		SessionData sd = this.getSessionData();
		sd.getMember().setCurrEnv(env.getName());
		this.sessionService.save(sd);
		// 保存到缓存
		Member member = this.memberRepository.findOne(this.getMember().getId());
		if(member.getmData() == null)
			member.setmData(new HashMap<>());
		member.getmData().put(AppConstants.ETH_ENV_KEY, env.getName());
		// 保存 到用户session 
		this.memberRepository.save(member);
		return new OkResponse();
	}
	
	/**
	 * 修改指定member的信息（修改后没有对该member的token进行）
	 * @param memberId
	 * @param userName
	 * @param loginName
	 * @param loginPwd
	 * @param groupId
	 * @param phone
	 * @param email
	 * @return
	 */
	@Transactional
	@RequestMapping("/updateMember")
	public Member updateMember(
			@RequestParam(name="memberId")Integer memberId,
			@RequestParam(name="userName",required=false)String userName,
			@RequestParam(name="loginName",required=false)String loginName,
			@RequestParam(name="loginPwd",required=false)String loginPwd,
			@RequestParam(name="groupId",required=false) Integer groupId,
			@RequestParam(name="phone",required=false)String phone,
			@RequestParam(name="email",required=false)String email) {
		Member m = this.memberRepository.findOne(memberId);
		if(m==null) throw new WalletException("member_not_exist","指定的成员不存在");
		if(groupId != null && !groupId.equals(m.getGroup().getId())) {
			UserGroup ug = this.userGroupRepository.findOne(groupId);
			if(ug == null) throw new WalletException("user_group_not_exist","用户组不存在");
			m.setGroup(ug);
			
			UserResource userResource = m.getUserResource();
			if(userResource == null) {
				userResource = new UserResource();
				userResource.setMember(m);
			}
			userResource.setApis(ug.getApis());
			userResource.setClients(ug.getClients());
			userResourceRepository.save(userResource);
			m.setUserResource(userResource);
		}
		if(userName != null) m.setNickname(userName);
		if(phone != null && !phone.equals(m.getPhone())) {
			ValidateUtils.validatePhone(phone);
			m.setPhone(phone);
			
			m = this.memberRepository.findByPhone(phone);
			if(m != null) throw new WalletException("phone_exist_member","该手机号已存在与其它账户中");
			MemberPhone memberPhone = this.memberPhoneRepository.findByMemberId(memberId);
			if(memberPhone == null) {
				memberPhone = new MemberPhone();
				memberPhone.setMember(m);
			}
			memberPhone.setNumber(phone);
			this.memberPhoneRepository.save(memberPhone);
		}
		if(email != null) m.setEmail(email);
		this.memberRepository.save(m);
		if(loginName != null) {
			MemberAccount tmpMa = this.memberAccountRepository.findByLoginName(loginName);
			if(tmpMa != null) throw new WalletException("login_name_exist","登录名存在或不能与当前登录名相同");
			MemberAccount ma = this.memberAccountRepository.findByMemberId(m.getId());
			ma.setLoginName(loginName);
			if(loginPwd != null) {
				ma.setLoginPwd(DigestUtils.md5Hex(loginPwd).toUpperCase());
			}
			this.memberAccountRepository.save(ma);
		}
		return m;
	}
	
	/**
	 * 通过userName、phone模糊查询所有member
	 * @param keywords
	 * @param pageNo
	 * @return
	 */
	@RequestMapping("/getMembers")
	public Object getMembers(@RequestParam(name="keywords") String keywords,
			@RequestParam(name="pageNo",required=false,defaultValue="0") Integer pageNo) {
		Pageable pageable = new PageRequest(pageNo,20);
		return this.memberRepository.findByNicknameLikeOrPhoneLike("%"+keywords+"%", "%"+keywords+"%", pageable);
	}
	
	/**
	 * 通过token缓存获取当前member信息
	 * @return
	 */
	@RequestMapping("/getSelfInfo")
	public Object getSelfInfo() {
		MemberInfo mi = this.getSessionData().getMember();
		if(mi == null) throw new WalletException("session_error","找不到账户状态");
		return mi;
	}
	
	/**
	 * 管理员对指定member设置是否禁用
	 * @param memberId
	 * @param enable
	 * @return
	 */
	@RequestMapping("/enableMember")
	public Member enableMember(
			@RequestParam(name="memberId")Integer memberId,
			@RequestParam(name="enable") Boolean enable) {
		Member m = this.memberRepository.findOne(memberId);
		if(m == null) throw new WalletException("member_not_exist","指定的成员不存在");
		m.setEnable(enable);
		m = this.memberRepository.save(m);
		
		if(!enable)
			this.memberSessionService.deleteMemberSession(memberId);
		return m;
	}
	
	/**
	 * 更新member_weixin信息
	 * @param encryptedData
	 * @param iv
	 * @param rawData
	 * @return
	 */
	
	@RequestMapping("/updateMemberWeixin")
	public MemberWechat updateMemberWeixin(
			@RequestParam(name="encryptedData") String encryptedData,
			@RequestParam(name="iv") String iv,
			@RequestParam(name="rawData", required=false) String rawData) {
		Client client = this.getClient();
		if(client.getClientData() == null) throw new WalletException("weixin_config_empty","客户端没有配置微信");
		String wxAppIdStr = client.getClientData().get(AppConstants.WX_APP_ID_KEY);
		if(wxAppIdStr == null) throw new WalletException("weixin_login_not_allowed","客户端不允许微信登录或者未配置微信登录");
		String sessionKey = this.getSessionData().getWxSessionKey();
		if(sessionKey == null) throw new WalletException("op_failed","不应该没有session_key，重新登陆可能解决此问题");
		log.info("sessionKey: " + sessionKey);
		MemberWechat memberWeixin = this.wechatService.getUserByWxMa(Integer.parseInt(wxAppIdStr), sessionKey, encryptedData, iv);
		log.info("更新{}号WemberWeixin信息", memberWeixin.getId());
		return memberWeixin;
	}
	
	/**
	 * 通过memberId、clientId获取member_weixin
	 * @param mid
	 * @param cid
	 * @return
	 */
	@RequestMapping("/getMemberWeixin")
	public Object getMemberWeixin(
			@RequestParam(name="mid") Integer mid,
			@RequestParam(name="cid") Integer cid) {
		MemberWechat memberWx = this.wechatService.getUserByMemberIdAndClientId(mid, cid);
		return memberWx;
	}
	
	/**
	 * 获取member下的所有member_wechat
	 * @param mid
	 * @return
	 */
	
	@RequestMapping("/getMemberWeixins")
	public List<MemberWechat> getMemberWeixins(@RequestParam(name="mid") Integer mid) {
		List<MemberWechat> memberWeixins = this.wechatService.getUserByMemberId(mid);
		log.info(JsonUtils.serialize(memberWeixins));
		for (MemberWechat memberWeixin : memberWeixins) {
			log.info("getMemberWeixins: "+memberWeixin.getAppType());
		}
		return memberWeixins;
	}
	
}
