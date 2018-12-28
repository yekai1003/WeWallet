package net.wzero.wewallet.gateway.serv;

import java.io.File;
import java.util.List;

import com.github.binarywang.wxpay.service.WxPayService;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaCodeLineColor;
import cn.binarywang.wx.miniapp.bean.template.WxMaTemplateAddResult;
import cn.binarywang.wx.miniapp.bean.template.WxMaTemplateLibraryGetResult;
import cn.binarywang.wx.miniapp.bean.template.WxMaTemplateLibraryListResult;
import cn.binarywang.wx.miniapp.bean.template.WxMaTemplateListResult;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.template.WxMpTemplate;
import me.chanjar.weixin.mp.bean.template.WxMpTemplateIndustry;
import net.wzero.wewallet.gateway.domain.MemberWechat;
import net.wzero.wewallet.gateway.domain.WechatApp;

public interface WechatService {
	
	public interface SessionHandler{
		void handler(String sessionKey);
	}
	
	//----------------------应用级别
	MemberWechat getUserByAuth(String code, int wxAppId) throws WxErrorException ;
	MemberWechat getUserByAuth(String code, int wxAppId,SessionHandler handler) throws WxErrorException ;
	MemberWechat getUserByWxMa(int wxAppId,String sessionKey, String signature, String rawData, String encryptedData, String iv);
	MemberWechat getUserByWxMa(Integer wxAppId, String sessionKey, String encryptedData, String iv);
	String getPhone(int wxAppId,String sessionKey, String signature, String rawData, String encryptedData, String iv);
	MemberWechat getUserByMemberIdAndClientId(Integer memberId, Integer clientId);
	List<MemberWechat> getUserByMemberIdAndClientTypeCode(Integer memberId, String clientTypeCode);
	List<MemberWechat> getUserByMemberId(Integer memberId);
	
	File createWxaCode(Integer wxAppId, String path, Integer width) throws WxErrorException;
	File createWxaCodeUnlimit(Integer wxAppId, String scene, String page) throws WxErrorException;
	File createWxaCodeUnlimit(Integer wxAppId, String scene, String page, int width, boolean autoColor, WxMaCodeLineColor lineColor, boolean isHyaline) throws WxErrorException;
	// 微信小程序模板消息
	WxMaTemplateLibraryListResult findTemplateLibraryList(Integer wxAppId, int offset, int count) throws WxErrorException;
	WxMaTemplateLibraryGetResult findTemplateLibraryKeywordList(Integer wxAppId, String id) throws WxErrorException;
	WxMaTemplateAddResult addTemplate(Integer wxAppId, String id, List<Integer> keywordIdList) throws WxErrorException;
	WxMaTemplateListResult findTemplateList(Integer wxAppId, int offset, int count) throws WxErrorException;
	boolean delTemplate(Integer wxAppId, String templateId) throws WxErrorException;
	// 微信公众号模板消息
	WxMpTemplateIndustry getIndustry(Integer wxAppId) throws WxErrorException;
	String addTemplate(Integer wxAppId, String shortTemplateId) throws WxErrorException;
	List<WxMpTemplate> getAllPrivateTemplate(Integer wxAppId) throws WxErrorException;
	boolean delPrivateTemplate(Integer wxAppId, String templateId) throws WxErrorException;
	//----------------------微信框架级别
	/**
	 * 通过配置的wxApp 表里的id获取微信服务对象，缓存
	 * @param wxAppId
	 * @return
	 */
	WxMpService getWxMpService(int wxAppId);
	WxMpService getWxMpService(WechatApp wxApp);
	/**
	 * 通过配置的wxApp 表里的id获取微信服务对象，缓存
	 * @param wxAppId
	 * @return
	 */
	WxPayService getWxPayService(int wxAppId);
	WxPayService getWxPayService(WechatApp wxApp);
	/**
	 * 通过wxApp 表里的id获取小程序服务对象，缓存
	 * @param wxAppId
	 * @return
	 */
	WxMaService getWxMaService(int wxAppId);
	WxMaService getWxMaService(WechatApp wxApp);
	
	
}
