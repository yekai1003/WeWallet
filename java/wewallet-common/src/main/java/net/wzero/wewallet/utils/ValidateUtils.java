package net.wzero.wewallet.utils;

import net.wzero.wewallet.WalletException;

public class ValidateUtils {

	public static void validatePhone(String phone) {
		if(!phone.matches("^[1][3,4,5,6,7,8,9][0-9]{9}")) 
			throw new WalletException("phone_error", "手机号格式不正确");
	}
}
