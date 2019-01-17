package net.wzero.wewallet.gateway.controller;

import java.util.HashMap;
import java.util.List;

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
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberWechat;
import net.wzero.wewallet.gateway.serv.MemberSessionService;
import net.wzero.wewallet.gateway.serv.MemberWechatService;
import net.wzero.wewallet.gateway.serv.SessionDataService;
import net.wzero.wewallet.gateway.serv.UserGroupService;
import net.wzero.wewallet.gateway.serv.UserService;
import net.wzero.wewallet.gateway.serv.WechatService;
import net.wzero.wewallet.res.OkResponse;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.AppConstants.EthEnv;
import net.wzero.wewallet.utils.ValidateUtils;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

	@Autowired
	private UserService userService;
	@Autowired
	private UserGroupService userGroupService;
	@Autowired
	private MemberWechatService memberWechatService;
	@Autowired
	private MemberSessionService memberSessionService;
	@Autowired
	private WechatService wechatService;
	@Autowired
	private SessionDataService sessionDataService;
	
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
		Member member = this.userService.addMemberAndMemberAccount(loginName.trim(), loginName.trim(), loginPwd, this.userGroupService.getNormalUserGroup(), null);
		if(ValidateUtils.validateEmail(loginName.trim())) { // 如果登录名是邮箱，保存邮箱
			this.userService.updateMember(member, null, null, null, loginName.trim(), null, null, null);
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
	@RequestMapping("/addMember")
	public Member addMember(
			@RequestParam(name="userName")String userName,
			@RequestParam(name="loginName")String loginName,
			@RequestParam(name="loginPwd")String loginPwd,
			@RequestParam(name="groupId") Integer groupId,
			@RequestParam(name="phone",required=false)String phone) {
		if(userName.trim().length() < 1) throw new WalletException("param_error","username不能为空");
		if(phone != null) {
			ValidateUtils.validatePhone(phone);
			if(this.userService.findByPhone(phone) != null) throw new WalletException("phone_exist_member","该手机号已存在与其它账户中");
		}
		return this.userService.addMemberAndMemberAccount(userName.trim(), loginName.trim(), loginPwd, this.userGroupService.findByGroupId(groupId), phone);
	}
	
	/**
	 * 根据memberId获取member
	 * @param mid
	 * @return
	 */
	@RequestMapping("/getMember")
	public Member getMember(@RequestParam(name="mid") int mid) {
		Member member = this.userService.findByMemberId(mid);
		if(member == null) throw new WalletException("member_not_found","账户不存在");
		return member;
	}
	
	/**
	 * 保存到 member mData里，下次启动加载配置到session
	 * @return
	 */
	@RequestMapping("/setEnv")
	public OkResponse setCurrEnv(@RequestParam(name="env") String envName) {
		this._setEnv(envName);
		return new OkResponse();
	}
	@RequestMapping("/currEnv")
	public EthEnv currEnv() {
		SessionData sd = this.getSessionData();
		if(sd.getMember().getCurrEnv() == null) {
			this._setEnv(AppConstants.EthEnv.MAINNET.getName());
			return EthEnv.MAINNET;
		}else {
			return EthEnv.fromString(sd.getMember().getCurrEnv());
		}
	}
	private void _setEnv(String envStr) {
		try {
			EthEnv env = EthEnv.fromString(envStr);
			this.sessionDataService.update(super.getSessionData(), env); // 保存到缓存
			
			// 保存到 member
			Member member = this.userService.findByMemberId(this.getMember().getId());
			if(member.getmData() == null)
				member.setmData(new HashMap<>());
			member.getmData().put(AppConstants.ETH_ENV_KEY, env.getName());
			this.userService.updateMember(member);
		}catch(java.lang.IllegalArgumentException ex) {
			ex.printStackTrace();
			throw new WalletException("env_not_found","指定的环境找不到");
		}
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
		Member m = this.userService.findByMemberId(memberId);
		if(m==null) throw new WalletException("member_not_exist","指定的成员不存在");
		return this.userService.updateMember(m, userName, groupId, phone, email, null, loginName, loginPwd);
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
		return this.userService.findByNicknameLikeOrPhoneLike("%"+keywords+"%", "%"+keywords+"%", pageable);
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
		Member m = this.userService.findByMemberId(memberId);
		if(m == null) throw new WalletException("member_not_exist","指定的成员不存在");
		m = this.userService.updateMember(m, null, null, null, null, enable, null, null);
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
		String sessionKey = this.getSessionData().getWxSessionKey();
		if(sessionKey == null) throw new WalletException("op_failed","不应该没有session_key，重新登陆可能解决此问题");
		log.info("sessionKey: " + sessionKey);
		MemberWechat memberWeixin = this.memberWechatService.getUserByWxMa(this.wechatService.getWxappIdByClient(), sessionKey, encryptedData, iv);
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
		return this.memberWechatService.findByMemberIdAndClientId(mid, cid);
	}
	
	/**
	 * 获取member下的所有member_wechat
	 * @param mid
	 * @return
	 */
	@RequestMapping("/getMemberWeixins")
	public List<MemberWechat> getMemberWeixins(@RequestParam(name="mid") Integer mid) {
		List<MemberWechat> memberWeixins = this.memberWechatService.getUserByMemberId(mid);
		for (MemberWechat memberWeixin : memberWeixins) {
			log.info("memberWeixin: "+memberWeixin.getAppType());
		}
		return memberWeixins;
	}
	
}
