package net.wzero.wewallet.core.serv;

import net.wzero.wewallet.core.domain.Card;
import net.wzero.wewallet.core.domain.Mnemonic;

public interface WalletService {

	/**
	 * 随机创建一个卡片（卡片也就是虚拟币账号）
	 * 用BIP44 方式创建，这个作为统一项
	 * @return
	 */
	Card createCard(String pwd);
	/**
	 * 通过助记词创建一个卡片
	 * 卡应该是统一的，默认使用根地址，比特币不知道是否也是那个根地址
	 * @param mnemonic
	 * @return
	 */
	Card createCard(Mnemonic mnemonic);
}
