package net.wzero.wewallet.gateway.serv.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.controller.SysParamSupport;
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberWechat;
import net.wzero.wewallet.gateway.domain.WechatApp;
import net.wzero.wewallet.gateway.repo.MemberWechatRepository;
import net.wzero.wewallet.gateway.serv.MemberWechatService;
import net.wzero.wewallet.gateway.serv.WechatAppService;
import net.wzero.wewallet.gateway.serv.WechatService;
import net.wzero.wewallet.gateway.serv.WechatService.SessionHandler;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.EmojiFilter;

@Slf4j
@Service("memberWechatService")
public class MemberWechatServiceImpl extends SysParamSupport  implements MemberWechatService {
	
	@Autowired
	private MemberWechatRepository memberWechatRepository;
	@Autowired
	private WechatAppService wechatAppService;
	@Autowired
	private WechatService wechatService;
	
	@Override
	public MemberWechat getUserByAuth(String code, int wxAppId) throws WxErrorException {
		WechatApp wxApp = this.wechatAppService.findById(wxAppId);
		if(wxApp == null)
			throw new WalletException("weixin_app_not_exist", "微信应用不存在");
		
		if(wxApp.getAppType() == AppConstants.WX_APP_TYPE_MP) {
			return this.getMpUser(code, wxApp);
		}else if(wxApp.getAppType() == AppConstants.WX_APP_TYPE_MINIAPP) {
			return this.getMaUser(code, wxApp, null);
		}else
			throw new WalletException("APP_TYPE_NOT_HANLD","尚未实现此类型app登陆");
	}
	
	@Override
	public MemberWechat getUserByAuth(String code, int wxAppId, SessionHandler handler) throws WxErrorException {
		WechatApp wxApp = this.wechatAppService.findById(wxAppId);
		if(wxApp == null)
			throw new WalletException("weixin_app_not_exist", "微信应用不存在");
		
		if(wxApp.getAppType() == AppConstants.WX_APP_TYPE_MP) {
			return this.getMpUser(code, wxApp);
		}else if(wxApp.getAppType() == AppConstants.WX_APP_TYPE_MINIAPP) {
			return this.getMaUser(code, wxApp, handler);
		}else
			throw new WalletException("APP_TYPE_NOT_HANLD","尚未实现此类型app登陆");
	}
	
	@Override
	public MemberWechat getUserByWxMa(Integer wxAppId, String sessionKey, String signature, String rawData, String encryptedData, String iv) {
		WxMaService wxService = this.wechatService.getWxMaService(wxAppId);
        WxMaUserInfo userInfo = this.wechatService.getWxMaUserInfo(wxService, sessionKey, signature, rawData, encryptedData, iv);
		MemberWechat memberWechat = this.memberWechatRepository.findByOpenId(userInfo.getOpenId());  // 理论上在上面已经保存过
		return this.update(memberWechat, userInfo, this.wechatAppService.findById(wxAppId));
	}
	
	@Override
	public MemberWechat getUserByWxMa(Integer wxAppId, String sessionKey, String encryptedData, String iv) {
		WxMaService wxService = this.wechatService.getWxMaService(wxAppId);
        WxMaUserInfo userInfo = this.wechatService.getWxMaUserInfo(wxService, sessionKey, encryptedData, iv);
		MemberWechat memberWechat = this.memberWechatRepository.findByOpenId(userInfo.getOpenId()); // 理论上在上面已经保存过
		return this.update(memberWechat, userInfo, this.wechatAppService.findById(wxAppId));
	}
	
	@Override
	public MemberWechat getMpUser(String code, WechatApp wxApp) throws WxErrorException {
		WxMpService wxMpService = this.wechatService.getWxMpService(wxApp);
		WxMpUser wxMpUser = this.wechatService.getWxMpUser(wxMpService, code);
		
		MemberWechat memberWechat = this.findByOpenId(wxMpUser.getOpenId()); // 检查用户是否存在
		if (memberWechat == null) // 如果不存在添加
			memberWechat = this.create(wxMpUser, wxApp);
		else // 更新操作
			memberWechat = this.update(memberWechat, wxMpUser, wxApp);
		return memberWechat;
	}

	@Override
	public MemberWechat getMaUser(String code,WechatApp wxApp,SessionHandler handler) throws WxErrorException {
		WxMaService wxMaService = this.wechatService.getWxMaService(wxApp);
		WxMaJscode2SessionResult session = this.wechatService.getWxMaJscode2SessionResult(wxMaService, code);
		if(handler != null)
			handler.handler(session.getSessionKey());
		MemberWechat memberWechat = this.findByOpenId(session.getOpenid()); // 检查用户是否存在
		if (memberWechat == null) { // 只有openid,unionid信息，之后会更新
			memberWechat = this.create(session, wxApp);
		} // 找到了就不更新了，因为openid 和unionid不会变
		return memberWechat;
	}
	
	@Override
	public MemberWechat create(WxMpUser wxMpUser, WechatApp wxApp) {
		MemberWechat memberWechat = new MemberWechat();
		memberWechat = this.update(memberWechat, wxMpUser, wxApp);
		memberWechat.setSubscribeTime(wxMpUser.getSubscribeTime());
		return this.memberWechatRepository.save(memberWechat);
	}
	
	@Override
	public MemberWechat create(WxMaJscode2SessionResult wxMaJscode2SessionResult, WechatApp wxApp) {
		MemberWechat memberWechat = new MemberWechat();
		memberWechat.setOpenId(wxMaJscode2SessionResult.getOpenid());
		memberWechat.setUnionId(wxMaJscode2SessionResult.getUnionid());
		memberWechat.setClientId(super.getClient().getId());
		memberWechat.setAppId(wxApp.getId());
		memberWechat.setAppType(wxApp.getAppType());
		return this.memberWechatRepository.save(memberWechat);
	}
	
	@Override
	public MemberWechat update(MemberWechat memberWechat, WxMpUser wxMpUser, WechatApp wxApp) {
		memberWechat.setCity(wxMpUser.getCity());
		memberWechat.setProvince(wxMpUser.getProvince());
		memberWechat.setCountry(wxMpUser.getCountry());
		memberWechat.setAvatarUrl(wxMpUser.getHeadImgUrl());
		memberWechat.setNickname(filterEmoji(wxMpUser.getNickname()));
		memberWechat.setOpenId(wxMpUser.getOpenId());
		memberWechat.setGender(wxMpUser.getSex());
		// userWx.setPrivilege(om.writeValueAsString(user.getPrivilege()));
		memberWechat.setUnionId(wxMpUser.getUnionId());
		memberWechat.setLanguage(wxMpUser.getLanguage());
		memberWechat.setClientId(super.getClient().getId());
		memberWechat.setAppId(wxApp.getId()); // 已存在的会没有
		memberWechat.setAppType(wxApp.getAppType());
		return this.memberWechatRepository.save(memberWechat);
	}
	
	@Override
	public MemberWechat update(MemberWechat memberWechat, WxMaUserInfo wxMaUserInfo, WechatApp wxApp) {
		if (memberWechat == null) {
			memberWechat = new MemberWechat();
			memberWechat.setOpenId(wxMaUserInfo.getOpenId());
		}
		memberWechat.setUnionId(wxMaUserInfo.getUnionId());
		memberWechat.setCity(wxMaUserInfo.getCity());
		memberWechat.setProvince(wxMaUserInfo.getProvince());
		memberWechat.setCountry(wxMaUserInfo.getCountry());
		memberWechat.setAvatarUrl(wxMaUserInfo.getAvatarUrl());
		memberWechat.setNickname(filterEmoji(wxMaUserInfo.getNickName()));
		memberWechat.setLanguage(wxMaUserInfo.getLanguage());
		memberWechat.setGender(Integer.parseInt(wxMaUserInfo.getGender()));
		if(wxMaUserInfo.getWatermark() != null) 
			memberWechat.setRemark("appId:"+wxMaUserInfo.getWatermark().getAppid());
		memberWechat.setClientId(super.getClient().getId());
		memberWechat.setAppId(wxApp.getId());  // 已存在的会没有
		memberWechat.setAppType(wxApp.getAppType());
		return this.memberWechatRepository.save(memberWechat);
	}
	
	@Override
	public MemberWechat updateMemberWechat(MemberWechat memberWechat, Member member) {
		memberWechat.setMember(member);
		return this.memberWechatRepository.save(memberWechat);
	}
	
	@Override
	public List<MemberWechat> getUserByMemberId(Integer memberId) {
		return this.memberWechatRepository.findByMemberId(memberId);
	}
	
	@Override
	public List<MemberWechat> getMemberWechatsByUnionId(String unionId) {
		return this.memberWechatRepository.findByUnionId(unionId);
	}
	
	@Override
	public MemberWechat findByOpenId(String openId) {
		return this.memberWechatRepository.findByOpenId(openId); // 检查用户是否存在
	}
	
	@Override
	public MemberWechat findByMemberIdAndClientId(Integer memberId, Integer clientId) {
		return this.memberWechatRepository.findByMemberIdAndClientId(memberId, clientId);
	}
	
	private String filterEmoji(String nickname) {
		log.info("------nick name:" + nickname);
		if (EmojiFilter.containsEmoji(nickname)) // nickname 特殊处理
			nickname = EmojiFilter.filterEmoji(nickname);
		return nickname;
	}
	
}
