package com.quincysx.crypto;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.spongycastle.util.encoders.Hex;

import com.quincysx.crypto.bip32.ExtendedKey;
import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.bip39.MnemonicGenerator;
import com.quincysx.crypto.bip39.RandomSeed;
import com.quincysx.crypto.bip39.SeedCalculator;
import com.quincysx.crypto.bip39.WordCount;
import com.quincysx.crypto.bip39.wordlists.English;
import com.quincysx.crypto.bip44.AddressIndex;
import com.quincysx.crypto.bip44.BIP44;
import com.quincysx.crypto.bip44.CoinPairDerive;
import com.quincysx.crypto.ethereum.EthECKeyPair;
import com.quincysx.crypto.ethereum.keystore.CipherException;
import com.quincysx.crypto.ethereum.keystore.KeyStore;
import com.quincysx.crypto.ethereum.keystore.KeyStoreFile;
import com.quincysx.crypto.exception.CoinNotFindException;
import com.quincysx.crypto.exception.NonSupportException;

public class TestAction {

	@Test
	public void doAction1() {
		//以最简单的方式生成根私钥
		ExtendedKey aNew = ExtendedKey.createNew();
		System.out.println("private\t"+aNew.getMaster().getPrivateKey());
		System.out.println("public\t"+aNew.getMaster().getPublicKey());
		System.out.println("address\t"+aNew.getMaster().getPublicKey());
//		//直接获取子私钥
//		extendedKey.getChild(index);
//		//获取 Bip44 标准的子私钥
//		AddressIndex address = BIP44.m().purpose44()
//                .coinType(CoinTypes.Ethereum)
//                .account(0)
//                .external()
//                .address(0);
//		CoinPairDerive coinKeyPair = new CoinPairDerive(extendedKey);
//		ECKeyPair master = coinKeyPair.derive(address);
	}
	@Test
	public void doAction2() throws ValidationException {
		//ExtendedKey root = new ExtendedKey(key, chainCode, depth, parent, sequence)
		String pk = "80b2453dd21f200177b3fa15831640199df6d3bdd6e400ffac8f9d13eb0f4e12";
		System.out.println(Hex.decode(pk).length);
		ECKeyPair key = new ECKeyPair(Hex.decode(pk), true); 
		System.out.println(key.getPublicKey());
	}
	@Test
	public void doAction3() throws ValidationException {
		String pk = "f7ac407861822b2403c136c7031eb19698bc9548db4c377b7c994cb0811ca576";
		
		ECKeyPair key = new ECKeyPair(Hex.decode(pk), true); 
		System.out.println(key.getPublicKey());
		
		EthECKeyPair eKey = EthECKeyPair.parse(key);
		System.out.println(eKey.getPrivateKey()+"\tprivate key");
		System.out.println(eKey.getPublicKey()+"\tpublic key");
		System.out.println(eKey.getAddress()+"\taddress");
	}
	@Test  //decrypt
	public void doAction4() throws IOException, CipherException, ValidationException, NoSuchAlgorithmException, InvalidKeySpecException {
		String keystore = "{\"address\":\"81dc57792eccf9bf026950ddcc67ea4aaf792009\",\"crypto\":{\"cipher\":\"aes-128-ctr\",\"ciphertext\":\"3c945c206929e04e5e6bd2f5f45d75e44a2274e33dae496b651968890a8488aa\",\"cipherparams\":{\"iv\":\"5b88519305af1ac5da7e06790b02a6f5\"},\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"n\":262144,\"p\":1,\"r\":8,\"salt\":\"a01b11c98190c3e21bfcf193a2c1e6aaef3e6fc44d301fa88455c31400c093c6\"},\"mac\":\"9515b554535bd8bc4e8c399b5e4054a6210533158423863e92f4b1bd0bbeb46b\"},\"id\":\"6d81bffc-fb6c-4bf0-bd44-982747b22dcc\",\"version\":3}";
		KeyStoreFile ksf = KeyStoreFile.parse(keystore);
		EthECKeyPair ksp = KeyStore.decrypt("123", ksf);
		System.out.println(ksp.getPrivateKey()+"\tprivate");
		System.out.println(ksp.getPublicKey() +"\tpublic");
		System.out.println(ksp.getAddress()+"\taddress");
		
	}
	@Test //encrypt
	public void doAction5() throws ValidationException, CipherException {
		String pk = "7222419c96716d209cca8d4737058c3d479c52b14e283ed551757adaee9e1d5f";
		
		ECKeyPair key = new ECKeyPair(Hex.decode(pk), true); 
		//System.out.println(key.getPublicKey());
		
		EthECKeyPair ecKeyPair = EthECKeyPair.parse(key);
		KeyStoreFile ksf = KeyStore.createStandard("123", ecKeyPair);
		System.out.println(ksf.toString());
	}
	@Test //从ECKeyPair 到 EthKeyPair  的变化 或者说 Raw Data 到String Data的区别
	public void doAction6() throws ValidationException {
		String pk = "f7ac407861822b2403c136c7031eb19698bc9548db4c377b7c994cb0811ca576";
		
		ECKeyPair key = new ECKeyPair(Hex.decode(pk), true); 
		System.out.println(key.getPublicKey());
		EthECKeyPair eKey = EthECKeyPair.parse(key);
		System.out.println(eKey.getPublicKey());
	}
	@Test//助记词 到 key
	public void doAction7() throws ValidationException {
		//随机12个数字
		byte[] random = RandomSeed.random(WordCount.TWELVE);
		//通过12个数字 取得助记词
		List<String> mnemonic = new MnemonicGenerator(English.INSTANCE).createMnemonic(random);
		mnemonic.stream().forEach(w-> System.out.println(w));
		//生产 seed 如果内容一样 种子就会一样
		byte[] seed = new SeedCalculator().calculateSeed(mnemonic, "");
		//通过seed 生产公私钥 master
		ExtendedKey extendedKey = ExtendedKey.create(seed);
		System.out.println(extendedKey.getMaster());
		/* 
		 * 疑问？
		 * 第一个产生的是 根 私钥
		 * 而后可以通过路径生成不同的子钥
		 * 路径定义 
		 * m / purpose' / coin_type' / account' / change / address_index
		 * 以太坊 路径 m/44'/60'/0'/0/0
		 * 44 代表 bip44
		 * 60 代表 以太币
		 * 0 账户索引
		 * 
		 * */
		//可以直接获取子钥
		ExtendedKey childIndex1 = extendedKey.getChild(1);
		//
	}
	@Test // turkey three bracket exclude aim gesture sheriff autumn fever box margin frequent
	public void doAction8() throws ValidationException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		String words = "derive horror enhance agent steel card quality snap post grape donate wagon";
		List<String> mnemonic =  Arrays.asList(words.split(" "));
		//生产 seed 如果内容一样 种子就会一样
		byte[] seed = new SeedCalculator().calculateSeed(mnemonic, "");
		//通过seed 生产公私钥 master
		ExtendedKey extendedKey = ExtendedKey.create(seed);
		//这种方式获取子私钥 不是准讯 BIP44标准的，那是什么标准呢... 不知道
		ExtendedKey childIndex1 = extendedKey.getChild(0);
		//制作成 以太坊key
		EthECKeyPair eKey = EthECKeyPair.parse(extendedKey.getKey(0));
		System.out.println(eKey.getAddress());
		//------------以上得到结果不对
		System.out.println("-------------以上不对--------------");
		//提供的方法来操作  这是 BIP44 标准的子私钥获取方式
		AddressIndex address = BIP44.m().purpose44()
                .coinType(CoinTypes.Ethereum)
                .account(0)
                .external()
                .address(0);
		CoinPairDerive coinKeyPair = new CoinPairDerive(extendedKey);
		ECKeyPair master = coinKeyPair.derive(address);
		eKey = EthECKeyPair.parse(master);
		System.out.println(eKey.getAddress());
		/**
		 * ----思考----
		 * 1、BIP44比BIP43多了一个币种的感念
		 * 2、UTXO 代表 Unspent Transaction Output。 比特币设计模式,简单点说就是没有余额的感念，钱是从别的交易所得里来的，所以就会找零
		 * 3、以太坊只需要 coin_type 来区分币种
		 * 4、masterkey (根私钥)
		 * 5、master key 用于显示地址？
		 * 6、直接获取子秘钥 和 BIP44模式获取不同的原因 可能是算法上不同，首先BIP44 里有 chaincode的感念
		 * 7、那master 应该是对外公开的唯一地址
		 * 8、以太坊地址 取的是 m/44'/60'/0'/0/0 路劲地址，目前只需要一个
		 *  */
	}
	@Test
	public void doAction9() {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		System.out.println(Security.getProvider("BC"));
	}
	@Test
	public void doAction10() throws NonSupportException, CoinNotFindException, ValidationException {
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		String words = "derive horror enhance agent steel card quality snap post grape donate wagon";
		List<String> mnemonic =  Arrays.asList(words.split(" "));
		//生产 seed 如果内容一样 种子就会一样
		byte[] seed = new SeedCalculator().calculateSeed(mnemonic, "");
		//通过seed 生产公私钥 master
		ExtendedKey extendedKey = ExtendedKey.create(seed);
		
		AddressIndex address = BIP44.parsePath("m/44'/60'/0'/0/0");
		CoinPairDerive coinKeyPair = new CoinPairDerive(extendedKey);
		ECKeyPair master = coinKeyPair.derive(address);
		EthECKeyPair eKey = EthECKeyPair.parse(master);
		System.out.println(eKey.getAddress());
	}
	@Test
	public void doAction11() {

		String words = "derive horror enhance agent steel card\nquality\r\nsnap post grape donate wagon";
		words = words.replaceAll("\r\n", " ");
		List<String> mnemonic =  Arrays.asList(words.split("[\\s|\n]"));
		System.out.println(mnemonic.size());
		mnemonic.stream().forEach(w-> System.out.println(w));
	}
}
