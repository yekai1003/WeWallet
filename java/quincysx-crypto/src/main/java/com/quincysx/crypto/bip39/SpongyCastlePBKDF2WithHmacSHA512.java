package com.quincysx.crypto.bip39;

import org.spongycastle.crypto.PBEParametersGenerator;
import org.spongycastle.crypto.digests.SHA512Digest;
import org.spongycastle.crypto.generators.PKCS5S2ParametersGenerator;
import org.spongycastle.crypto.params.KeyParameter;

/**
 * @author QuincySx
 * @date 2018/5/15 上午10:20
 */
public enum SpongyCastlePBKDF2WithHmacSHA512 implements PBKDF2WithHmacSHA512 {
    INSTANCE;

    @Override
    public byte[] hash(char[] chars, byte[] salt) {
        PKCS5S2ParametersGenerator generator = new PKCS5S2ParametersGenerator(new SHA512Digest());
        generator.init(PBEParametersGenerator.PKCS5PasswordToUTF8Bytes(chars), salt, 2048);
        KeyParameter key = (KeyParameter) generator.generateDerivedMacParameters(512);
        return key.getKey();
    }
}
