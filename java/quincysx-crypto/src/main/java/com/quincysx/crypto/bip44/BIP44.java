package com.quincysx.crypto.bip44;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quincysx.crypto.CoinTypes;
import com.quincysx.crypto.exception.CoinNotFindException;
import com.quincysx.crypto.exception.NonSupportException;

/**
 * @author QuincySx
 * @date 2018/3/5 下午3:36
 */
public final class BIP44 {
	private static final Logger logger = (Logger) LoggerFactory.getLogger(BIP44.class);

    private static final M M = new M();

    BIP44() {
    }

    /**
     * Start creating a BIP44 path.
     *
     * @return A fluent factory for creating BIP44 paths
     */
    public static M m() {
        return M;
    }

    public static AddressIndex parsePath(String path) throws NonSupportException,
            CoinNotFindException {
        // m/44'/60'/0'/0/0
        String regEx = "m(/\\d+'?){3}/[0,1]/\\d+'?";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(path);
        // 字符串是否与正则表达式相匹配
        if (!matcher.matches()) {
            throw new NonSupportException("Path format is not correct");
        }
        String[] split = path.split("/");
        String purposeStr = split[1].substring(0, split[1].length() - 1);
        String coinTypeStr = split[2].substring(0, split[2].length() - 1);
        String accountStr = split[3].substring(0, split[3].length() - 1);
        String changeStr = split[4];
        String addressStr;
        boolean addressHard = false;
        if (split[5].contains("'")) {
            addressStr = split[5].substring(0, split[5].length() - 1);
            addressHard = true;
        } else {
            addressStr = split[5];
        }

        if ((Integer.parseInt(purposeStr)) != 44) {
            throw new NonSupportException("Only support the 44 protocol");
        }
        //Log.e("++++def", "ddddd  " + Integer.parseInt(accountStr));
        logger.info("account -> " + Integer.parseInt(accountStr));
        Account account = BIP44.m()
                .purpose44()
                .coinType(CoinTypes.parseCoinType(Integer.parseInt(coinTypeStr)))
                .account(Integer.parseInt(accountStr));

        Change change;
        if (Integer.parseInt(changeStr) == 0) {
            change = account.external();
        } else {
            change = account.internal();
        }

        return change.address(Integer.parseInt(addressStr), addressHard);
    }
}
