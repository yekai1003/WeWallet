package net.wzero.wewallet.core.serv.impl;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quincysx.crypto.bip32.ExtendedKey;
import com.quincysx.crypto.ethereum.EthECKeyPair;

import net.wzero.wewallet.core.domain.Card;
import net.wzero.wewallet.core.domain.CardType;
import net.wzero.wewallet.core.domain.Mnemonic;
import net.wzero.wewallet.core.repo.CardRepository;
import net.wzero.wewallet.core.repo.CardTypeRepository;
import net.wzero.wewallet.core.serv.CryptoService;
import net.wzero.wewallet.core.serv.WalletService;
import net.wzero.wewallet.core.utils.AppConstant;

@Service("walletService")
public class WalletServiceImpl implements WalletService {

	@Autowired
	private CryptoService cryptoService;
	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private CardTypeRepository cardTypeRepository;
	
	/**
	 * 不遵循 BIP44
	 */
	@Override
	public Card createCard(String pwd) {
		//取得卡类型
		CardType ct = this.cardTypeRepository.getOne(AppConstant.ETHEREUM_CARD_TYP);
		// 直接创建一个key
		ExtendedKey ek = ExtendedKey.createNew();
		EthECKeyPair kp = new EthECKeyPair(ek.getMaster());
		// 得到一个keystore
		String keystore = this.cryptoService.encryptKeystore(kp, pwd);//password 需要外部提供
		// 创建 一个card
		Card card = Card.builder()
				.addr(kp.getAddress())//账号地址 address 0xC1F741b993F8715468b5e7c3B3ff62541aF7A578
				.amount(new BigDecimal(0))//因为创建的时候默认是0
				.cardType(ct)
				.keystore(keystore)
				.memberId(0)
				.path("m/44'/60'/0'/0/0")//默认以太坊地址
				.build();
		
//		card.setAddr(kp.getAddress());
//		card.setAmount(new BigDecimal(0));
//		card.setCardType(null);//需要传递参数
//		card.setKeystore(keystore);//
//		card.setMemberId(0);
//		card.setPath("");
		return card;
	}

	@Override
	public Card createCard(Mnemonic mnemonic) {
		// TODO Auto-generated method stub
		return null;
	}

}
