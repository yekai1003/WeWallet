package net.wzero.wewallet.serv.impl;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.serv.SmsService;
import net.wzero.wewallet.utils.JsonUtils;

@Slf4j
@Service("smsService")
public class SmsServiceImpl implements SmsService,InitializingBean {

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";
    
    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
	@Value("${alidayu.accessKeyId}")  
	private String accessKeyId;
	@Value("${alidayu.accessKeySecret}")  
	private String accessKeySecret;
	
	private IAcsClient acsClient;

	@Override
	public void afterPropertiesSet() throws Exception {
        //可自助调整超时时间
        System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
        System.setProperty("sun.net.client.defaultReadTimeout", "10000");
		//初始化acsClient,暂不支持region化
        IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
        DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
        this.acsClient = new DefaultAcsClient(profile);
	}
    
	@Override
	public void send(String to, String template, Map<String, Object> params) {
		//组装请求对象-具体描述见控制台-文档部分内容
        SendSmsRequest request = new SendSmsRequest();
        //必填:待发送手机号
        request.setPhoneNumbers(to);
        //必填:短信签名-可在短信控制台中找到
        request.setSignName("");
        //必填:短信模板-可在短信控制台中找到
        request.setTemplateCode(template);
        //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
        request.setTemplateParam(JsonUtils.serialize(params));

        //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
        //request.setSmsUpExtendCode("90997");

        //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
        //request.setOutId("yourOutId");

        //hint 此处可能会抛出异常，注意catch
        try {
			SendSmsResponse res = acsClient.getAcsResponse(request);
			log.info("code:"+res.getCode()+"\t->\tmessage:"+res.getMessage());
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("sms_server_error","短信服务器异常");
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("sms_client_error","短信客户端异常");
		}


	}

}
