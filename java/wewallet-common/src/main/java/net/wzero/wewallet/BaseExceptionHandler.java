package net.wzero.wewallet;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import me.chanjar.weixin.common.error.WxErrorException;
import net.wzero.wewallet.res.ErrorResponse;

public class BaseExceptionHandler {
	@ExceptionHandler(value=MissingServletRequestParameterException.class)
	@ResponseBody
	public ErrorResponse MissingServletRequestParameterHandler(HttpServletRequest request,  
			MissingServletRequestParameterException exception) throws Exception{
		ErrorResponse err = new ErrorResponse("param_error","参数["+exception.getParameterName()+"]不能为空");
		return err;
	}
	@ExceptionHandler(value=WalletException.class)
	@ResponseBody
	public ErrorResponse jsonErrorHandler(HttpServletRequest req,WalletException we) throws Exception {
		ErrorResponse rb = new ErrorResponse();
		rb.setErrCode(we.getErrCode());
		rb.setErrMsg(we.getErrMsg());
		return rb;
	}
	@ExceptionHandler(value=WxErrorException.class)
	@ResponseBody
	public ErrorResponse weixinErrorHandler(HttpServletRequest req,WxErrorException e) throws Exception {
		ErrorResponse rb = new ErrorResponse();
		rb.setErrCode("weixin_err_"+e.getError().getErrorCode());
		rb.setErrMsg(e.getError().getErrorMsg());
		return rb;
	}
	@ExceptionHandler(value=Exception.class)
	@ResponseBody
	public ErrorResponse defaultErrorHandler(HttpServletRequest req, Exception e) throws Exception {
		e.printStackTrace();
		ErrorResponse rb = new ErrorResponse();
		rb.setErrCode("unknown_err");
		rb.setErrMsg(e.getMessage());
		return rb;
	}
}
