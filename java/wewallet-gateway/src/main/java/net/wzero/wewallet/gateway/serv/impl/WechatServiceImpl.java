package net.wzero.wewallet.gateway.serv.impl;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.binarywang.wxpay.config.WxPayConfig;
import com.github.binarywang.wxpay.service.WxPayService;
import com.github.binarywang.wxpay.service.impl.WxPayServiceImpl;

import cn.binarywang.wx.miniapp.api.WxMaQrcodeService;
import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.api.WxMaTemplateService;
import cn.binarywang.wx.miniapp.api.impl.WxMaServiceImpl;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import cn.binarywang.wx.miniapp.bean.template.WxMaTemplateAddResult;
import cn.binarywang.wx.miniapp.bean.template.WxMaTemplateLibraryGetResult;
import cn.binarywang.wx.miniapp.bean.template.WxMaTemplateLibraryListResult;
import cn.binarywang.wx.miniapp.bean.template.WxMaTemplateListResult;
import cn.binarywang.wx.miniapp.config.WxMaInMemoryConfig;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.bean.result.WxMpOAuth2AccessToken;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateIndustry;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.controller.SysParamSupport;
import net.wzero.wewallet.gateway.domain.MemberWechat;
import net.wzero.wewallet.gateway.domain.WechatApp;
import net.wzero.wewallet.gateway.repo.MemberWechatRepository;
import net.wzero.wewallet.gateway.repo.WechatAppRepository;
import net.wzero.wewallet.gateway.serv.WechatService;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.utils.EmojiFilter;
import net.wzero.wewallet.utils.JsonUtils;
import net.wzero.wewallet.wx.WxMpDbConfigStorage;

@Service("wechatService")
public class WechatServiceImpl extends SysParamSupport implements WechatService {

	private static Map<Integer, WxMpService> wxMpServDic;
	private static Map<Integer, WxPayService> wxPayServDic;
	private static Map<Integer, WxMaService> wxMaServDic;

	@Autowired
	private WechatAppRepository wechatAppRepository;
	@Autowired
	private MemberWechatRepository memberWechatRepository;

	@Override
	public WxMpService getWxMpService(int wxAppId) {
		if(wxMpServDic != null && wxMpServDic.containsKey(wxAppId))
			return wxMpServDic.get(wxAppId);
		else {
			WechatApp wxApp = this.wechatAppRepository.findOne(wxAppId);
			return this.getWxMpService(wxApp);
		}
	}

	@Override
	public WxMpService getWxMpService(WechatApp wxApp) {
		if (wxApp == null)
			throw new WalletException("weixin_app_not_exist", "微信应用不存在");
		if (wxMpServDic == null)
			wxMpServDic = new HashMap<Integer, WxMpService>();
		WxMpService wxMpServ = null;
		if (wxMpServDic.containsKey(wxApp.getId()))
			wxMpServ = wxMpServDic.get(wxApp.getId());
		else {
			wxMpServ = new WxMpServiceImpl();
			WxMpDbConfigStorage dbConfig = new WxMpDbConfigStorage(wxApp);
			wxMpServ.setWxMpConfigStorage(dbConfig);
			wxMpServDic.put(wxApp.getId(), wxMpServ);
		}
		return wxMpServ;
	}

	@Override
	public WxPayService getWxPayService(int wxAppId) {
		if(wxPayServDic != null && wxPayServDic.containsKey(wxAppId))
			return wxPayServDic.get(wxAppId);
		else {
			WechatApp wxApp = this.wechatAppRepository.findOne(wxAppId);
			return this.getWxPayService(wxApp);
		}
	}
	@Override
	public WxPayService getWxPayService(WechatApp wxApp) {
		if (wxApp == null)
			throw new WalletException("weixin_app_not_exist", "微信应用不存在");
		if (wxPayServDic == null)
			wxPayServDic = new HashMap<Integer, WxPayService>();
		WxPayService wxPayServ = null;
		if (wxPayServDic.containsKey(wxApp.getId()))
			wxPayServ = wxPayServDic.get(wxApp.getId());
		else {
			wxPayServ = new WxPayServiceImpl();
			WxPayConfig wxPayConf = new WxPayConfig();
			wxPayConf.setAppId(wxApp.getAppid());
			wxPayConf.setMchId(wxApp.getMchId());
			wxPayConf.setMchKey(wxApp.getMchKey());
			wxPayConf.setNotifyUrl(wxApp.getNotifyUrl());
			if(wxApp.getSubAppId() != null)
				wxPayConf.setSubAppId(wxApp.getSubAppId());
			if(wxApp.getSubMchId() != null)
				wxPayConf.setSubMchId(wxApp.getSubMchId());
			wxPayServ.setConfig(wxPayConf);
			wxPayServDic.put(wxApp.getId(), wxPayServ);
		}
		return wxPayServ;
	}

	@Override
	public WxMaService getWxMaService(int wxAppId) {
		if(wxMaServDic != null && wxMaServDic.containsKey(wxAppId))
			return wxMaServDic.get(wxAppId);
		else {
			WechatApp wxApp = this.wechatAppRepository.findOne(wxAppId);
			return this.getWxMaService(wxApp);
		}
	}

	@Override
	public WxMaService getWxMaService(WechatApp wxApp) {
		if(wxApp == null)
			throw new WalletException("weixin_app_not_exist", "微信应用不存在");
		if(wxMaServDic == null)
			wxMaServDic = new HashMap<Integer,WxMaService>();
		WxMaService wxMaServ = null;
		if(wxMaServDic.containsKey(wxApp.getId())) {
			wxMaServ = wxMaServDic.get(wxApp.getId());
		}else {
	        WxMaInMemoryConfig config = new WxMaInMemoryConfig();
	        config.setAppid(wxApp.getAppid());
	        config.setSecret(wxApp.getAppsecret());
	        config.setToken(wxApp.getToken());
	        config.setAesKey(wxApp.getEncodingaeskey());
	        config.setMsgDataFormat("JOSN");
	        wxMaServ = new WxMaServiceImpl();
	        wxMaServ.setWxMaConfig(config);
	        wxMaServDic.put(wxApp.getId(), wxMaServ);
		}
        
		return wxMaServ;
	}
	private MemberWechat getMpUser(String code,WechatApp wxApp) throws WxErrorException {

		WxMpService wxMpService = this.getWxMpService(wxApp);
		// 获取access_token
		WxMpOAuth2AccessToken token = null;
		token = wxMpService.oauth2getAccessToken(code);
		if (token != null) {
			// 获取用户数据
			WxMpUser user = wxMpService.oauth2getUserInfo(token, "zh_CN");
			if (user != null) {
				System.out.println("------nick name:" + user.getNickname());
				// nickname 特殊处理
				String nickname = user.getNickname();
				if (EmojiFilter.containsEmoji(nickname))
					nickname = EmojiFilter.filterEmoji(nickname);
				// 检查用户是否存在
				MemberWechat userWx = this.memberWechatRepository.findByOpenId(user.getOpenId());
				// 如果不存在添加
				if (userWx == null) {
					// 存起来
					userWx = new MemberWechat();
					userWx.setCity(user.getCity());
					userWx.setProvince(user.getProvince());
					userWx.setCountry(user.getCountry());
					userWx.setAvatarUrl(user.getHeadImgUrl());
					userWx.setNickname(nickname);
					userWx.setOpenId(user.getOpenId());
					userWx.setGender(user.getSex());
					// userWx.setPrivilege(om.writeValueAsString(user.get));
					userWx.setUnionId(user.getUnionId());
					userWx.setLanguage(user.getLanguage());
					userWx.setClientId(super.getClient().getId());
					userWx.setAppId(wxApp.getId());
					userWx.setAppType(wxApp.getAppType());
					userWx.setSubscribeTime(user.getSubscribeTime());
					userWx = this.memberWechatRepository.save(userWx);
				} else {
					// 更新操作
					userWx.setCity(user.getCity());
					userWx.setProvince(user.getProvince());
					userWx.setCountry(user.getCountry());
					userWx.setAvatarUrl(user.getHeadImgUrl());
					userWx.setNickname(nickname);
					userWx.setOpenId(user.getOpenId());
					userWx.setGender(user.getSex());
					// userWx.setPrivilege(om.writeValueAsString(user.getPrivilege()));
					userWx.setUnionId(user.getUnionId());
					userWx.setLanguage(user.getLanguage());
					userWx.setClientId(super.getClient().getId());
					userWx.setAppId(wxApp.getId()); // 已存在的会没有
					userWx.setAppType(wxApp.getAppType());
					userWx = this.memberWechatRepository.save(userWx);
				}
				return userWx;
			}else // 授权不成功，会异常吧
				throw new WalletException("get_weixin_access_token_error", "获取微信access_token失败");
		}else
			throw new WalletException("get_weixin_access_token_error", "获取微信access_token失败");
	}
	private MemberWechat getMaUser(String code,WechatApp wxApp,SessionHandler handler) throws WxErrorException {
		WxMaService wxMaService = this.getWxMaService(wxApp);
		//获取 session ,MP里叫token 应该是一个理儿 session 里有 openid和 unionid有什么用
		WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
		if(handler != null)
			handler.handler(session.getSessionKey());
		/* 
		 * 这个是获取不到全部信息的... 只有 openid,unionid,还有个session key
		 * 如果存在 openid,unionid 就不保存了
		 * 一会儿就会有新的数据申请来保存完整数据
		 */
		// 检查用户是否存在
		MemberWechat userWx = this.memberWechatRepository.findByOpenId(session.getOpenid());
		if (userWx == null) {
			userWx = new MemberWechat();
			userWx.setOpenId(session.getOpenid());
			userWx.setUnionId(session.getUnionid());
			userWx.setClientId(super.getClient().getId());
			userWx.setAppId(wxApp.getId());
			userWx.setAppType(wxApp.getAppType());
			userWx = this.memberWechatRepository.save(userWx);
		}//找到了就不更新了，因为openid 和unionid不会变
		return userWx;
	}
	@Override
	public MemberWechat getUserByAuth(String code, int wxAppId) throws WxErrorException {
		WechatApp wxApp = this.wechatAppRepository.findOne(wxAppId);
		if(wxApp == null)
			throw new WalletException("weixin_app_not_exist", "微信应用不存在");
		
		if(wxApp.getAppType() == AppConstants.WX_APP_TYPE_MP) {
			return this.getMpUser(code, wxApp);
		}else if(wxApp.getAppType() == AppConstants.WX_APP_TYPE_MINIAPP) {
			return this.getMaUser(code, wxApp,null);
		}else
			throw new WalletException("APP_TYPE_NOT_HANLD","尚未实现此类型app登陆");
	}

	@Override
	public MemberWechat getUserByAuth(String code, int wxAppId, SessionHandler handler) throws WxErrorException {
		WechatApp wxApp = this.wechatAppRepository.findOne(wxAppId);
		if(wxApp == null)
			throw new WalletException("weixin_app_not_exist", "微信应用不存在");
		
		if(wxApp.getAppType() == AppConstants.WX_APP_TYPE_MP) {
			return this.getMpUser(code, wxApp);
		}else if(wxApp.getAppType() == AppConstants.WX_APP_TYPE_MINIAPP) {
			return this.getMaUser(code, wxApp,handler);
		}else
			throw new WalletException("APP_TYPE_NOT_HANLD","尚未实现此类型app登陆");
	}

	@Override
	public MemberWechat getUserByWxMa(int wxAppId, String sessionKey, String signature, String rawData, String encryptedData, String iv) {
		WxMaService wxService = this.getWxMaService(wxAppId);
		if(!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
			throw new WalletException("op_failed","数据校验不通过");
		}
		// 解密用户信息
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

		// nickname 特殊处理
		String nickname = userInfo.getNickName();
		if (EmojiFilter.containsEmoji(nickname))
			nickname = EmojiFilter.filterEmoji(nickname);
		
		// 检查用户是否存在 理论上在上面已经保存过一点了
		MemberWechat userWx = this.memberWechatRepository.findByOpenId(userInfo.getOpenId());
		if (userWx == null) {
			userWx = new MemberWechat();
			userWx.setOpenId(userInfo.getOpenId());
		}
		//通用的每次都更新，性别... 也更新
		userWx.setUnionId(userInfo.getUnionId());
		userWx.setCity(userInfo.getCity());
		userWx.setProvince(userInfo.getProvince());
		userWx.setCountry(userInfo.getCountry());
		userWx.setAvatarUrl(userInfo.getAvatarUrl());
		userWx.setNickname(nickname);
		userWx.setLanguage(userInfo.getLanguage());
		userWx.setGender(Integer.parseInt(userInfo.getGender()));
		if(userInfo.getWatermark() != null) {
			userWx.setRemark("appId:"+userInfo.getWatermark().getAppid());
		}
		userWx.setAppId(wxAppId);  // 已存在的会没有
		userWx = this.memberWechatRepository.save(userWx);
		return userWx;
	}
	
	@Override
	public MemberWechat getUserByWxMa(Integer wxAppId, String sessionKey, String encryptedData, String iv) {
		WxMaService wxService = this.getWxMaService(wxAppId);
        WxMaUserInfo userInfo = wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv); // 解密用户信息
//		String jData = WxMaCryptUtils.decrypt(sessionKey, encryptedData, iv);
//		System.out.println("json_data: "+jData);
//		WxMaUserInfo userInfo = WxMaUserInfo.fromJson(jData);
		System.out.println("userInfo: " + JsonUtils.serialize(userInfo));
		
		String nickname = userInfo.getNickName(); // nickname 特殊处理
		if (EmojiFilter.containsEmoji(nickname)) nickname = EmojiFilter.filterEmoji(nickname);
		
		MemberWechat userWx = this.memberWechatRepository.findByOpenId(userInfo.getOpenId()); // 检查用户是否存在 理论上在上面已经保存过一点了
		if (userWx == null) {
			userWx = new MemberWechat();
			userWx.setOpenId(userInfo.getOpenId());
		}
		userWx.setUnionId(userInfo.getUnionId()); // 没绑定到平台时可能没有
		userWx.setCity(userInfo.getCity());
		userWx.setProvince(userInfo.getProvince());
		userWx.setCountry(userInfo.getCountry());
		userWx.setAvatarUrl(userInfo.getAvatarUrl());
		userWx.setNickname(nickname);
		userWx.setLanguage(userInfo.getLanguage());
		userWx.setGender(Integer.parseInt(userInfo.getGender()));
		if(userInfo.getWatermark() != null) userWx.setRemark("appId:"+userInfo.getWatermark().getAppid());
		userWx.setClientId(super.getClient().getId());
		userWx.setAppId(wxAppId); // 已存在的会没有
		userWx = this.memberWechatRepository.save(userWx);
		return userWx;
	}

	@Override
	public String getPhone(int wxAppId, String sessionKey, String signature, String rawData, String encryptedData,
			String iv) {
		WxMaService wxService = this.getWxMaService(wxAppId);
		if(!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
			throw new WalletException("op_failed","数据校验不通过");
		}
        // 解密
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv);
        
		return phoneNoInfo.getPhoneNumber();
	}
	
	@Override
	public File createWxaCode(Integer wxAppId, String path, Integer width) throws WxErrorException {
		WxMaService wxMaService = this.getWxMaService(wxAppId);
		WxMaQrcodeService qrcodeService = wxMaService.getQrcodeService();
		return qrcodeService.createWxaCode(path, width);
	}
	
	@Override
	public File createWxaCodeUnlimit(Integer wxAppId, String scene, String page) throws WxErrorException {
		WxMaService wxMaService = this.getWxMaService(wxAppId);
		WxMaQrcodeService qrcodeService = wxMaService.getQrcodeService();
		return qrcodeService.createWxaCodeUnlimit(scene, page);
	}

	@Override
	public File createWxaCodeUnlimit(Integer wxAppId, String scene, String page, int width, boolean autoColor,
			WxMaCodeLineColor lineColor, boolean isHyaline) throws WxErrorException {
		WxMaService wxMaService = this.getWxMaService(wxAppId);
		WxMaQrcodeService qrcodeService = wxMaService.getQrcodeService();
		return qrcodeService.createWxaCodeUnlimit(scene, page, width, autoColor, lineColor, isHyaline);
	}

	@Override
	public WxMaTemplateLibraryListResult findTemplateLibraryList(Integer wxAppId, int offset, int count)
			throws WxErrorException {
		WxMaService wxMaService = this.getWxMaService(wxAppId);
		WxMaTemplateService templateService = wxMaService.getTemplateService();
		return templateService.findTemplateLibraryList(offset, count);
	}

	@Override
	public WxMaTemplateLibraryGetResult findTemplateLibraryKeywordList(Integer wxAppId, String id)
			throws WxErrorException {
		WxMaService wxMaService = this.getWxMaService(wxAppId);
		WxMaTemplateService templateService = wxMaService.getTemplateService();
		return templateService.findTemplateLibraryKeywordList(id);
	}

	@Override
	public WxMaTemplateAddResult addTemplate(Integer wxAppId, String id, List<Integer> keywordIdList)
			throws WxErrorException {
		WxMaService wxMaService = this.getWxMaService(wxAppId);
		WxMaTemplateService templateService = wxMaService.getTemplateService();
		return templateService.addTemplate(id, keywordIdList);
	}

	@Override
	public WxMaTemplateListResult findTemplateList(Integer wxAppId, int offset, int count) throws WxErrorException {
		WxMaService wxMaService = this.getWxMaService(wxAppId);
		WxMaTemplateService templateService = wxMaService.getTemplateService();
		return templateService.findTemplateList(offset, count);
	}

	@Override
	public boolean delTemplate(Integer wxAppId, String templateId) throws WxErrorException {
		WxMaService wxMaService = this.getWxMaService(wxAppId);
		WxMaTemplateService templateService = wxMaService.getTemplateService();
		return templateService.delTemplate(templateId);
	}
	
	@Override
	public WxMpTemplateIndustry getIndustry(Integer wxAppId) throws WxErrorException {
		WxMpService wxMpService = this.getWxMpService(wxAppId);
		return wxMpService.getTemplateMsgService().getIndustry();
	}
	
	@Override
	public String addTemplate(Integer wxAppId, String shortTemplateId) throws WxErrorException {
		WxMpService wxMpService = this.getWxMpService(wxAppId);
		return wxMpService.getTemplateMsgService().addTemplate(shortTemplateId);
	}
	
	@Override
	public List<WxMpTemplate> getAllPrivateTemplate(Integer wxAppId) throws WxErrorException {
		WxMpService wxMpService = this.getWxMpService(wxAppId);
		List<WxMpTemplate> list = wxMpService.getTemplateMsgService().getAllPrivateTemplate();
		return list;
	}
	
	@Override
	public boolean delPrivateTemplate(Integer wxAppId, String templateId) throws WxErrorException {
		WxMpService wxMpService = this.getWxMpService(wxAppId);
		return wxMpService.getTemplateMsgService().delPrivateTemplate(templateId);
	}
	
	@Override
	public List<MemberWechat> getUserByMemberId(Integer memberId) {
		return this.memberWechatRepository.findByMemberId(memberId);
	}
	
	@Override
	public MemberWechat getUserByMemberIdAndClientId(Integer memberId, Integer clientId) {
		throw new WalletException("not_implemented","未实现");
		//return this.memberWechatRepository.findByMemberIdAndClientId(memberId, clientId);
	}
	
	@Override
	public List<MemberWechat> getUserByMemberIdAndClientTypeCode(Integer memberId, String clientTypeCode) {
		throw new WalletException("not_implemented","未实现");
		//return this.memberWechatRepository.findByMemberIdAndClientTypeCode(memberId, clientTypeCode);
	}
	
}
