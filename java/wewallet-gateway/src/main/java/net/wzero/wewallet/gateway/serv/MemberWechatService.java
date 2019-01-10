package net.wzero.wewallet.gateway.serv;

import java.util.List;

import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import net.wzero.wewallet.gateway.domain.Member;
import net.wzero.wewallet.gateway.domain.MemberWechat;
import net.wzero.wewallet.gateway.domain.WechatApp;
import net.wzero.wewallet.gateway.serv.WechatService.SessionHandler;

public interface MemberWechatService {

	MemberWechat create(WxMpUser wxMpUser, WechatApp wxApp);
	MemberWechat create(WxMaJscode2SessionResult wxMaJscode2SessionResult, WechatApp wxApp);
	
	MemberWechat update(MemberWechat memberWechat, WxMpUser wxMpUser, WechatApp wxApp);
	MemberWechat update(MemberWechat memberWechat, WxMaUserInfo wxMaUserInfo, WechatApp wxApp);
	
	MemberWechat getMpUser(String code, WechatApp wxApp) throws WxErrorException;
	MemberWechat getMaUser(String code,WechatApp wxApp,SessionHandler handler) throws WxErrorException;
	MemberWechat getUserByAuth(String code, int wxAppId) throws WxErrorException ;
	MemberWechat getUserByAuth(String code, int wxAppId, SessionHandler handler) throws WxErrorException ;
	
	MemberWechat getUserByWxMa(Integer wxAppId,String sessionKey, String signature, String rawData, String encryptedData, String iv);
	MemberWechat getUserByWxMa(Integer wxAppId, String sessionKey, String encryptedData, String iv);
	
	MemberWechat updateMemberWechat(MemberWechat memberWechat, Member member);
	
	MemberWechat findByMemberIdAndClientId(Integer memberId, Integer clientId);
	MemberWechat findByOpenId(String openId);
	
	List<MemberWechat> getUserByMemberId(Integer memberId);
	List<MemberWechat> getMemberWechatsByUnionId(String unionId);
	
}
