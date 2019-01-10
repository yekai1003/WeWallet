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
import net.wzero.wewallet.gateway.domain.Client;
import net.wzero.wewallet.gateway.domain.WechatApp;
import net.wzero.wewallet.gateway.serv.WechatAppService;
import net.wzero.wewallet.gateway.serv.WechatService;
import net.wzero.wewallet.utils.AppConstants;
import net.wzero.wewallet.wx.WxMpDbConfigStorage;

@Service("wechatService")
public class WechatServiceImpl extends SysParamSupport implements WechatService {

	private static Map<Integer, WxMpService> wxMpServDic;
	private static Map<Integer, WxPayService> wxPayServDic;
	private static Map<Integer, WxMaService> wxMaServDic;

	@Autowired
	private WechatAppService wechatAppService;

	@Override
	public WxMpService getWxMpService(int wxAppId) {
		if(wxMpServDic != null && wxMpServDic.containsKey(wxAppId))
			return wxMpServDic.get(wxAppId);
		else {
			WechatApp wxApp = this.wechatAppService.findById(wxAppId);
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
			WechatApp wxApp = this.wechatAppService.findById(wxAppId);
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
			WechatApp wxApp = this.wechatAppService.findById(wxAppId);
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
	
	@Override
	public WxMpUser getWxMpUser(WxMpService wxMpService, String code) throws WxErrorException {
		WxMpOAuth2AccessToken token = wxMpService.oauth2getAccessToken(code); // 获取access_token
		if (token != null) {
			WxMpUser user = wxMpService.oauth2getUserInfo(token, "zh_CN"); // 获取用户数据
			if (user != null) {
				return user;
			}else // 授权不成功，会异常吧
				throw new WalletException("get_weixin_access_token_error", "获取微信access_token失败");
		} else {
			throw new WalletException("get_weixin_access_token_error", "获取微信access_token失败");
		}
	}
	
	@Override
	public WxMaJscode2SessionResult getWxMaJscode2SessionResult(WxMaService wxMaService, String code) throws WxErrorException {
		return wxMaService.getUserService().getSessionInfo(code);
	}
	
	@Override
	public WxMaUserInfo getWxMaUserInfo(WxMaService wxService, String sessionKey, String encryptedData, String iv) {
		return wxService.getUserService().getUserInfo(sessionKey, encryptedData, iv); // 解密用户信息
	}
	@Override
	public WxMaUserInfo getWxMaUserInfo(WxMaService wxService, String sessionKey, String signature, String rawData,
			String encryptedData, String iv) {
		if(!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
			throw new WalletException("op_failed","数据校验不通过");
		}
		return this.getWxMaUserInfo(wxService, sessionKey, encryptedData, iv);
	}
	
	@Override
	public String getPhone(int wxAppId, String sessionKey, String signature, String rawData, String encryptedData,
			String iv) {
		WxMaService wxService = this.getWxMaService(wxAppId);
		if(!wxService.getUserService().checkUserInfo(sessionKey, rawData, signature)) {
			throw new WalletException("op_failed","数据校验不通过");
		}
        WxMaPhoneNumberInfo phoneNoInfo = wxService.getUserService().getPhoneNoInfo(sessionKey, encryptedData, iv); // 解密
		return phoneNoInfo.getPhoneNumber();
	}
	
	@Override
	public Integer getWxappIdByClient() {
		Client client = this.getClient();
		if(client.getClientData() == null) // 检查是否配置
			 throw new WalletException("weixin_config_empty","客户端没有配置微信");
		String wxAppIdStr = client.getClientData().get(AppConstants.WX_APP_ID_KEY); // 获取 wxAppId
		if(wxAppIdStr == null) throw new WalletException("weixin_login_not_allowed","客户端不允许微信登录或者未配置微信登录");
		return Integer.parseInt(wxAppIdStr);
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
	
}
