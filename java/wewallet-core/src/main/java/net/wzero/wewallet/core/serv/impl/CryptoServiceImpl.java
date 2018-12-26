package net.wzero.wewallet.core.serv.impl;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;

import org.spongycastle.util.encoders.Hex;
import org.springframework.stereotype.Service;

import com.quincysx.crypto.ECKeyPair;
import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.bip39.MnemonicGenerator;
import com.quincysx.crypto.bip39.RandomSeed;
import com.quincysx.crypto.bip39.WordCount;
import com.quincysx.crypto.bip39.wordlists.English;
import com.quincysx.crypto.bip44.AddressIndex;
import com.quincysx.crypto.bip44.BIP44;
import com.quincysx.crypto.ethereum.EthECKeyPair;
import com.quincysx.crypto.ethereum.keystore.CipherException;
import com.quincysx.crypto.ethereum.keystore.KeyStore;
import com.quincysx.crypto.ethereum.keystore.KeyStoreFile;
import com.quincysx.crypto.exception.CoinNotFindException;
import com.quincysx.crypto.exception.NonSupportException;

import lombok.extern.slf4j.Slf4j;
import net.wzero.wewallet.WalletException;
import net.wzero.wewallet.core.domain.Mnemonic;
import net.wzero.wewallet.core.serv.CryptoService;

@Slf4j
@Service("cryptoService")
public class CryptoServiceImpl implements CryptoService {
	static {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
	}

	@Override
	public Mnemonic randomMnemonic() {
		//随机12个数字
		byte[] random = RandomSeed.random(WordCount.TWELVE);
		Mnemonic mnemonic=new Mnemonic();
		//通过12个数字 取得助记词
		mnemonic.setWords(new MnemonicGenerator(English.INSTANCE).createMnemonic(random));
		return mnemonic;
	}

	@Override
	public AddressIndex paseAddress(String path) {
		// TODO Auto-generated method stub
		try {
			log.info("path->\t"+path);
			return BIP44.parsePath(path);
		} catch (NonSupportException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new WalletException("non_supported","不支持的路径");
		} catch (CoinNotFindException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new WalletException("coin_not_found","不支持的币种");
		}
	}

	@Override
	public EthECKeyPair decryptKeystore(String keystore, String pwd) {
		// TODO Auto-generated method stub
		EthECKeyPair ksp;
		try {
			KeyStoreFile ksf = KeyStoreFile.parse(keystore);
			ksp = KeyStore.decrypt(pwd, ksf);
			return ksp;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("no_such_algorithm","不支持的算法");
		} catch (InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("invalid_key_spec","key无效");
		} catch (CipherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("chipher_exception","解密失败？");
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("validation_exception","密码错误，解密失败");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("json_format_error","无效的keystore格式");
		}
	}

	@Override
	public String encryptKeystore(String privateKey, String pwd) {
		// TODO Auto-generated method stub
		
		try {
			ECKeyPair key = new ECKeyPair(Hex.decode(privateKey), true);
			EthECKeyPair ecKeyPair = EthECKeyPair.parse(key);
			KeyStoreFile ksf = KeyStore.createStandard(pwd, ecKeyPair);
			return ksf.toString();
		} catch (ValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("validation_exception","验证失败!");//以后探究
		} catch (CipherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("cipher_exception","编码失败！");
		} 
		
	}

	@Override
	public String encryptKeystore(EthECKeyPair keyPair, String pwd) {
		try {
			KeyStoreFile ksf = KeyStore.createStandard(pwd, keyPair);
			return ksf.toString();
		} catch (CipherException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new WalletException("cipher_exception","编码失败！");
		} 
	}

}
