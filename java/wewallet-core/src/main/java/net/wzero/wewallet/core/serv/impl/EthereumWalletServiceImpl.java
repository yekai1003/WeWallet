package net.wzero.wewallet.core.serv.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.quincysx.crypto.ECKeyPair;
import com.quincysx.crypto.bip32.ExtendedKey;
import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.bip39.SeedCalculator;
import com.quincysx.crypto.bip44.AddressIndex;
import com.quincysx.crypto.bip44.CoinPairDerive;
import com.quincysx.crypto.ethereum.EthECKeyPair;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.controller.SysParamSupport;
import net.wzero.wewallet.core.domain.Card;
import net.wzero.wewallet.core.domain.CardType;
import net.wzero.wewallet.core.domain.EthereumCard;
import net.wzero.wewallet.core.repo.CardRepository;
import net.wzero.wewallet.core.repo.CardTypeRepository;
import net.wzero.wewallet.core.serv.CryptoService;
import net.wzero.wewallet.core.serv.WalletService;
import net.wzero.wewallet.utils.AppConstants;

@Slf4j
@Service("walletService")
public class EthereumWalletServiceImpl extends SysParamSupport implements WalletService {

	@Autowired
	private CryptoService cryptoService;
	@Autowired
	private CardRepository cardRepository;
	@Autowired
	private CardTypeRepository cardTypeRepository;

	/**
	 * 默认遵循 BIP44
	 */
	@Override
	public Card createCard(String pwd) {
		try {
			// 取得卡类型
			CardType ct = this.cardTypeRepository.getOne(AppConstants.ETHEREUM_CARD_TYPE);
			// 直接创建一个key
			ExtendedKey ek = ExtendedKey.createNew();
			EthECKeyPair kp = this.makeKeyPairByPath(ek, "m/44'/60'/0'/0/0");
			// 得到一个keystore
			String keystore = this.cryptoService.encryptKeystore(kp, pwd);// password 需要外部提供
			log.debug("keystore->\t" + keystore);
			// 创建 一个card
			EthereumCard card = new EthereumCard();
			card.setAddr(kp.getAddress());
			card.setAmount(new BigDecimal(0));
			card.setCardType(ct);// 需要传递参数
			card.setKeystore(keystore);//
			card.setMemberId(this.getMember().getId());
			card.setPath("m/44'/60'/0'/0/0");
			// 保存
			return this.cardRepository.save(card);
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("validation_exception","编码错误");
		}
	}

	@Override
	public Card createCard(List<String> words,String pwd) {
		// 取得卡类型
		CardType ct = this.cardTypeRepository.getOne(AppConstants.ETHEREUM_CARD_TYPE);
		try {
			// 生产 seed 如果内容一样 种子就会一样
			byte[] seed = new SeedCalculator().calculateSeed(words, "");//密码就不要了吧
			// 通过seed 生产公私钥 master
			ExtendedKey extendedKey = ExtendedKey.create(seed);
			// 提供的方法来操作 这是 BIP44 标准的子私钥获取方式 m/44'/60'/0'/0/0 以太坊获取第一个就好了
			EthECKeyPair eKey = this.makeKeyPairByPath(extendedKey, "m/44'/60'/0'/0/0");
			String keystore = this.cryptoService.encryptKeystore(eKey,pwd);// password 需要外部提供

			log.info("address->\t"+eKey.getAddress());
			
			EthereumCard card = new EthereumCard();
			card.setAddr(eKey.getAddress());
			card.setAmount(new BigDecimal(0));
			card.setCardType(ct);// 需要传递参数
			card.setKeystore(keystore);//
			card.setMemberId(this.getMember().getId());
			card.setPath("m/44'/60'/0'/0/0");//以太坊现在默认
			// 保存
			return this.cardRepository.save(card);
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("validation_exception","编码错误");
		}
	}
	private EthECKeyPair makeKeyPairByPath(ExtendedKey extendedKey,String path) throws ValidationException {
		AddressIndex address = this.cryptoService.paseAddress(path);
		return this.makeKeyPairByPath(extendedKey, address);
	}
	private EthECKeyPair makeKeyPairByPath(ExtendedKey extendedKey,AddressIndex address) throws ValidationException {
		CoinPairDerive coinKeyPair = new CoinPairDerive(extendedKey);
		ECKeyPair master = coinKeyPair.derive(address);
		return EthECKeyPair.parse(master);
	}
}
