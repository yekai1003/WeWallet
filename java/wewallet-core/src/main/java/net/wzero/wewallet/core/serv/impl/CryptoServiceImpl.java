package net.wzero.wewallet.core.serv.impl;

import org.springframework.stereotype.Service;

import com.quincysx.crypto.bip44.AddressIndex;
import com.quincysx.crypto.ethereum.EthECKeyPair;

import net.wzero.wewallet.core.domain.Mnemonic;
import net.wzero.wewallet.core.serv.CryptoService;

@Service("cryptoService")
public class CryptoServiceImpl implements CryptoService {

	@Override
	public Mnemonic randomMnemonic() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AddressIndex paseAddress(String path) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EthECKeyPair decryptKeystore(String keystore, String pwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encryptKeystore(String privateKey, String pwd) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String encryptKeystore(EthECKeyPair keyPair, String pwd) {
		// TODO Auto-generated method stub
		return null;
	}

}
