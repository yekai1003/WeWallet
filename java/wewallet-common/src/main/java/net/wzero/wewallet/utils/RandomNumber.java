package net.wzero.wewallet.utils;

public class RandomNumber {
	public static int getRandNum(int min, int max) {
	    int randNum = min + (int)(Math.random() * ((max - min) + 1));
	    return randNum;
	}
	public static String getVerificationCode() {
		return String.format("%06d", getRandNum(1,999999));
	}
}
