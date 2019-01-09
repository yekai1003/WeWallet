package net.wzero.wewallet.utils;

import net.wzero.wewallet.WalletException;

public class ValidateUtils {
	
	public static String PHONE_REGEX = "^[1][3,4,5,6,7,8,9][0-9]{9}";
	public static String EMAIL_REGEX = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";;

	public static void validatePhone(String phone) {
		if(!phone.matches(PHONE_REGEX)) 
			throw new WalletException("phone_error", "手机号格式不正确");
	}
	
	public static Boolean validateEmail(String eamil) {
		if(eamil.matches(EMAIL_REGEX))
			return true;
		return false;
	}
	
}
