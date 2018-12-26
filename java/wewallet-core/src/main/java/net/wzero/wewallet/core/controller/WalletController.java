package net.wzero.wewallet.core.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.quincysx.crypto.bip39.MnemonicGenerator;
import com.quincysx.crypto.bip39.RandomSeed;
import com.quincysx.crypto.bip39.WordCount;
import com.quincysx.crypto.bip39.wordlists.English;

import net.wzero.wewallet.core.domain.Card;
import net.wzero.wewallet.core.domain.Mnemonic;

@RestController
@RequestMapping("/wallet")
public class WalletController {

	/**
	 * 随机一组助记词
	 * @return
	 */
	@RequestMapping("/randomMnemonic")
	public Mnemonic randomMnemonic() {
		//随机12个数字
		byte[] random = RandomSeed.random(WordCount.TWELVE);
		Mnemonic mnemonic=new Mnemonic();
		//通过12个数字 取得助记词
		mnemonic.setWords(new MnemonicGenerator(English.INSTANCE).createMnemonic(random));
		return mnemonic;
	}
	/**
	 * 基于随机种子创建一个钱包
	 * 这个钱包可能不基于助记词
	 * @param cardType
	 * @return
	 */
	@RequestMapping("/createCard")
	public Card createCard(Integer cardType) {
		return null; 
	}
	/**
	 * 用处比较多
	 * 客户端生成助记词
	 * 或者其他方式生成的助记词
	 * @param mnemonic
	 * @param cardType
	 * @return
	 */
	@RequestMapping("/createCardByMnemonic")
	public Card createCardByMnemonic(String mnemonic,Integer cardType) {
		return null;
	}
	/**
	 * 列出账户下所有的卡片
	 * @return
	 */
	@RequestMapping("/listCards")
	public List<Card> listCards(){
		return null;
	}
}
