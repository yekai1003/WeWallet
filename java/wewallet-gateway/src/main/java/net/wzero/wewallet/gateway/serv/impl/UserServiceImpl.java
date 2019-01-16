package net.wzero.wewallet.gateway.serv.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.controller.SysParamSupport;
import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.gateway.domain.Client;
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberAccount;
import net.wzero.wewallet.gateway.domain.MemberPhone;
import net.wzero.wewallet.gateway.domain.UserGroup;
import net.wzero.wewallet.gateway.repo.MemberRepository;
import net.wzero.wewallet.gateway.serv.MemberAccountService;
import net.wzero.wewallet.gateway.serv.MemberPhoneService;
import net.wzero.wewallet.gateway.serv.UserGroupService;
import net.wzero.wewallet.gateway.serv.UserResourceService;
import net.wzero.wewallet.gateway.serv.UserService;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.JsonUtils;
import net.wzero.wewallet.utils.ValidateUtils;

@Slf4j
@Service("userService")
@Transactional
public class UserServiceImpl extends SysParamSupport implements UserService {
	
	@Autowired
	private MemberRepository memberRepository;
	@Autowired
	private MemberPhoneService memberPhoneService;
	@Autowired
	private UserGroupService userGroupService;
	@Autowired
	private UserResourceService userResourceService;
	@Autowired
	private MemberAccountService memberAccountService;
	
	@Override
	public Member addMemberAndMemberAccount(String userName, String loginName, String loginPwd, UserGroup userGroup, String phone) {
		MemberAccount tmpMa = this.memberAccountService.findByLoginName(loginName); // 检查 loginname存在否
		if(tmpMa != null) throw new WalletException("login_name_exist","登录名存在");
		if(userGroup == null) throw new WalletException("user_group_not_exist","用户组不存在");
		Member m = this.createMember(userName, userGroup, phone, null, null);
		
		if(phone != null) {
			MemberPhone memberPhone = this.memberPhoneService.findByNumber(phone);
			if(memberPhone != null) throw new WalletException("phone_exist_member_phone","该手机号已存在与member_phone中");
			this.memberPhoneService.createMemberPhone(m, phone);
		}
		this.memberAccountService.createMemberAccount(m, loginName, loginPwd);
		return m;
	}
	
	@Override
	public Member createMember(String userName, UserGroup userGroup, String phone, String loginIp, Date loginTime) {
		Member m = new Member();
		m.setNickname(userName);
		m.setGroup(userGroup);
		m.setUserResource(this.userResourceService.createUserResource(userGroup, m));
		m.setPhone(phone);
		m.setLastLoginIp(loginIp);
		m.setLastLoginTime(loginTime);
		m.setEnable(true);
		m.setmData(new HashMap<String, String>());
		return this.memberRepository.save(m);
	}
	
	@Override
	public Member createNormalMember() {
		return this.createMember(null, this.userGroupService.getNormalUserGroup(), null, this.getSystemParam().getUserIp(), new Date());
	}
	
	@Override
	public Member updateMemberLoginInfo(Member member) {
		member.setLastLoginIp(this.getSystemParam().getUserIp());
		member.setLastLoginTime(new Date());
		return this.memberRepository.save(member);
	}
	
	@Override
	public Member updateMember(Member member) {
		return this.memberRepository.save(member);
	}
	
	@Override
	public Member updateMember(Member member, String phone) {
		Member tmpMember = findByPhone(phone);
		if(tmpMember != null && !tmpMember.getId().equals(member.getId()))
			if(member != null) throw new WalletException("phone_exist_member","该手机号已存在与其它账户中");
		member.setPhone(phone);
		return this.memberRepository.save(member);
	}
	
	@Override
	public Member updateMember(Member member, String userName, Integer groupId, String phone, String email,
			Boolean enable, String loginName, String loginPwd) {
		if(userName != null) member.setNickname(userName);
		if(groupId != null && !groupId.equals(member.getGroup().getId())) {
			 UserGroup userGroup = this.userGroupService.findByGroupId(groupId);
			if(userGroup == null) throw new WalletException("user_group_not_exist","用户组不存在");
			member.setGroup(userGroup);
			member.setUserResource(this.userResourceService.updateUserResource(userGroup, member));
		}
		if(phone != null && !phone.equals(member.getPhone())) {
			ValidateUtils.validatePhone(phone);
			if(this.findByPhone(phone) != null) throw new WalletException("phone_exist_member","该手机号已存在与其它账户中");
			
			member.setPhone(phone);
			this.memberPhoneService.updateMemberPhone(member, phone);
		}
		if(email != null) member.setEmail(email);
		if(enable != null) member.setEnable(enable);
		if(loginName != null)
			this.memberAccountService.updateMemberAccount(member, loginName, loginPwd);
		return this.memberRepository.save(member);
	}
	
	@Override
	public Member findByMemberId(Integer memberId) {
		return this.memberRepository.findOne(memberId);
	}
	
	@Override
	public Member findByPhone(String phone) {
		return this.memberRepository.findByPhone(phone);
	}
	
	@Override
	public Page<Member> findByNicknameLikeOrPhoneLike(String keywords, String keywords2, Pageable pageable) {
		return this.memberRepository.findByNicknameLikeOrPhoneLike(keywords, keywords2, pageable);
	}
	
	@Override
	public MemberPhone createPhoneLogin(Integer memberId, String phone) {
		Member member = this.memberRepository.findOne(memberId);
		if(member == null ) throw new WalletException("member_not_exist","用户不存在");
		log.debug("-------------一般用不到这个方法----------进来异常");
		return this.createPhoneLogin(member, phone);
	}

	@Override
	public MemberPhone createPhoneLogin(Member member, String phone) {
		return this.memberPhoneService.createMemberPhone(member, phone);
	}
	
	@Override
	public void access(Client client, MemberInfo member, Map<String, Object> map) {
		log.info("clientTypeCode：{}，sceneId：{}", client.getClientType().getCode(), map.get("sceneId"));
		if("wx_xcx".equals(client.getClientType().getCode())) {
			if(map.get("sceneId") != null) { // 没有小程序场景值暂时不做处理
				if("1044".equals(map.get("sceneId").toString())) {
					if(map.get("activityId") == null || map.get("recommendMemberId") == null) 
						throw new WalletException("info_error", "访问活动时data中的activityId或recommendMemberId参数不存在");
					map.put("memberId", member.getId());
					map.put("memberName", member.getUserName());
					if(client.getClientData() == null) throw new WalletException("weixin_config_empty","客户端没有配置微信");
					String wxAppIdStr = client.getClientData().get(AppConstants.WX_APP_ID_KEY); // 获取 wxAppId
					if(wxAppIdStr == null) throw new WalletException("weixin_login_not_allowed","客户端不允许微信登录或者未配置微信登录");
					String sessionKey = this.getSessionData().getWxSessionKey();
					map.put("sessionKey", sessionKey);
					log.info("map：{}", JsonUtils.serialize(map));
				} else if("1047".equals(map.get("sceneId").toString()) || "1048".equals(map.get("sceneId").toString()) || "1049".equals(map.get("sceneId").toString())) {
					if(map.get("activityId") == null || map.get("recommendMemberId") == null) 
						throw new WalletException("info_error", "访问活动时data中的activityId或recommendMemberId参数不存在");
					map.put("memberId", member.getId());
					map.put("memberName", member.getUserName());
					log.info("map：{}", JsonUtils.serialize(map));
				}
			}
		}
		
	}

}
