package net.wzero.wewallet.core.serv;

import com.quincysx.crypto.bip44.AddressIndex;
import com.quincysx.crypto.ethereum.EthECKeyPair;

import net.wzero.wewallet.core.domain.Mnemonic;

public interface CryptoService {
	/**
	 * 随机产生12个助记词
	 * @return
	 */
	Mnemonic randomMnemonic();
	/**
	 * 通过 路径产生 地址索引 m/44'/60'/0'/0/0"
	 * @param path
	 * @return
	 */
	AddressIndex paseAddress(String path);
	/**
	 * 以太坊使用 keystore存储
	 * @param keystore
	 * @param pwd
	 * @return
	 */
	EthECKeyPair decryptKeystore(String keystore,String pwd);
	/**
	 * 通过私钥创建 keystore
	 * @param privateKey 私钥
	 * @param pwd 加密密码
	 * @return
	 */
	String encryptKeystore(String privateKey,String pwd);
	/**
	 * 通过 秘钥对对象 来创建keystore
	 * @param keyPair 以太坊秘钥对 对象实例
	 * @param pwd 加密密码
	 * @return
	 */
	String encryptKeystore(EthECKeyPair keyPair,String pwd);
}
