package net.wzero.wewallet.gateway.serv.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.controller.SysParamSupport;
import net.wzero.wewallet.domain.MemberInfo;
import net.wzero.wewallet.gateway.domain.Client;
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberPhone;
import net.wzero.wewallet.gateway.repo.MemberPhoneRepository;
import net.wzero.wewallet.gateway.repo.MemberRepository;
import net.wzero.wewallet.gateway.serv.UserService;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.JsonUtils;

@Slf4j
@Service("userService")
public class UserServiceImpl extends SysParamSupport implements UserService {
	
	@Autowired
	private MemberPhoneRepository memberPhoneRepository;
	@Autowired
	private MemberRepository memberRepository;
	@Override
	public MemberPhone createPhoneLogin(Integer memberId, String phone) {
		Member member = this.memberRepository.findOne(memberId);
		if(member == null ) throw new WalletException("member_not_exist","用户不存在");
		log.debug("-------------一般用不到这个方法----------进来异常");
		return this.createPhoneLogin(member, phone);
	}

	@Override
	public MemberPhone createPhoneLogin(Member member, String phone) {
		MemberPhone mp = new MemberPhone();
		mp.setMember(member);
		mp.setNumber(phone);
		return this.memberPhoneRepository.save(mp);
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
