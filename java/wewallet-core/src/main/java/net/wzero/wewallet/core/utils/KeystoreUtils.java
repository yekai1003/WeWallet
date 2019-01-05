package net.wzero.wewallet.core.utils;

import java.io.IOException;

import org.web3j.crypto.CipherException;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Wallet;
import org.web3j.crypto.WalletFile;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class KeystoreUtils {

	private final static ObjectMapper objectMapper = new ObjectMapper();
	
	public static ECKeyPair readKeystore(String keystore,String pwd) throws CipherException, JsonParseException, JsonMappingException, IOException {
		WalletFile walletFile = objectMapper.readValue(keystore, WalletFile.class);
		return Wallet.decrypt(pwd, walletFile);
	}
}
