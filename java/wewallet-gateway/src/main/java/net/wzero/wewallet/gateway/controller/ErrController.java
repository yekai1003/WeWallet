package net.wzero.wewallet.gateway.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ErrorAttributes;
import org.springframework.boot.autoconfigure.web.ErrorController;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.netflix.zuul.exception.ZuulException;

import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.res.ErrorResponse;

@RestController
@CrossOrigin
@EnableConfigurationProperties({ServerProperties.class})
public class ErrController implements ErrorController {
	private static Logger log = LoggerFactory.getLogger(ErrController.class);

	private ErrorAttributes errorAttributes;
	/**
     * 初始化ExceptionController
     * @param errorAttributes
     */
    @Autowired
    public ErrController(ErrorAttributes errorAttributes) {
        Assert.notNull(errorAttributes, "ErrorAttributes must not be null");
        this.errorAttributes = errorAttributes;
    }
	@Override
	public String getErrorPath() {
		//BasicErrorController c = 
		return "";
	}
	@RequestMapping("/error")
	public ErrorResponse error(HttpServletRequest request) {
		Integer statusCode = (Integer) request
                .getAttribute("javax.servlet.error.status_code");
		log.info("--------javax.servlet.error.status_code："+statusCode);
		if(statusCode == 404) {
			return new ErrorResponse("page_not_exist","页面不存在");
		}
		RequestAttributes requestAttributes = new ServletRequestAttributes(request);
		Throwable ex = this.errorAttributes.getError(requestAttributes);
		ErrorResponse err = null;
		if(ex instanceof WalletException) {
			err = new ErrorResponse(((WalletException)ex).getErrCode(),((WalletException)ex).getErrMsg());
		}else if(ex instanceof ZuulException) {
			Throwable ex2 = ex.getCause();
			if(ex2 !=null) {
				log.info(ex.getCause().getClass().getName()+":"+ex.getCause().getMessage());
				if(ex2 instanceof WalletException) {
					err =  new ErrorResponse(((WalletException)ex2).getErrCode(),((WalletException)ex2).getErrMsg());
				} else {
					err =  new ErrorResponse("unkown_err",ex == null?"":ex.getMessage());
				}
			}
		}else {
			err = new ErrorResponse("unkown_err",ex == null?"":ex.getMessage());
		}
		return err;
	}
}
