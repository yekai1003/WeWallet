package net.wzero.wewallet.core.serv;

import java.util.List;

import net.wzero.wewallet.core.domain.Account;
import net.wzero.wewallet.core.domain.Token;
import net.wzero.wewallet.utils.AppConstants.EthEnv;

public interface WalletService {

	/**
	 * 随机创建一个账户（账户也就是虚拟币账号）
	 * 用BIP44 方式创建，这个作为统一项
	 * @return
	 */
	Account createAccount(Integer memberId,String pwd, String mark);
	/**
	 * 通过助记词创建一个账户
	 * 账应该是统一的，默认使用根地址，比特币不知道是否也是那个根地址
	 * @param mnemonic
	 * @return
	 */
	Account createAccount(Integer memberId,List<String> words,String pwd, String mark);
	/**
	 *   通过 私钥 创建账户
	 * @param memberId
	 * @param privateKey
	 * @param pwd
	 * @param mark
	 * @return
	 */
	Account createAccount(Integer memberId,String privateKey,String pwd,String mark);
	/**
	 * 通过keystore 创建账户
	 * @param memberId
	 * @param keystore
	 * @param mark
	 * @return
	 */
	Account createAccountByKeystore(Integer memberId,String keystore,String mark);
	Account updateAccount(Account account, String mark);
	Account findByAccountId(Integer accountId);
	/**
	 * 刷新账户以太坊余额
	 * @param accountId
	 * @return
	 */
	Account refreshBalance(Integer memberId,Integer accountId);
	/**
	 * 加载一个token 到账号
	 * @param memberId
	 * @param accountId
	 * @param contractAddr
	 * @param standard
	 * @return
	 */
	Token addToken(Integer memberId,Integer accountId,EthEnv env,String contractAddr,String standard, String icon, String name, String symbol, Integer decimals);
	Token updateTokn(Token token, String contractAddr,String standard, String icon);
	Token findByTokenId(Integer tokenId);
	/**
	 * 刷新token的余额
	 * @param memberId
	 * @param tokenId
	 * @return
	 */
	Token refreshTokenBalance(Integer memberId,Integer tokenId);
	/**
	 * 查询用户的所有账户
	 * @param memberId
	 * @return
	 */
	List<Account> findByMemberId(Integer memberId);
}
