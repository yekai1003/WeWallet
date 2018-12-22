package com.quincysx.crypto.bitcoin;

import com.quincysx.crypto.ECKeyPair;
import com.quincysx.crypto.Key;
import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.utils.Base58;
import com.quincysx.crypto.utils.Base58Check;
import com.quincysx.crypto.utils.HexUtils;
import com.quincysx.crypto.utils.RIPEMD160;
import com.quincysx.crypto.utils.SHA256;

import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.DERInteger;
import org.spongycastle.asn1.DERSequenceGenerator;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ParametersWithRandom;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * @author QuincySx
 * @date 2018/3/8 上午10:56
 */
public class BitCoinECKeyPair extends ECKeyPair {
    private static final int TEST_NET_ADDRESS_SUFFIX = 0x6f;
    private static final int MAIN_NET_ADDRESS_SUFFIX = 0x00;

    public static final int MAIN_NET_PRIVATE_KEY_PREFIX = 0x80;
    public static final int TEST_NET_PRIVATE_KEY_PREFIX = 0xef;
    private static final int MAIN_NET_PRIVATE_KEY_SUFFIX = 0x01;

    private static final int RAW_PRIVATE_KEY_COMPRESSED_LENGTH = 38;
    private static final int RAW_PRIVATE_KEY_NO_COMPRESSED_LENGTH = 37;

    private final boolean testNet;

    public static BitCoinECKeyPair parse(Key keyPair, boolean testNet) {
        return new BitCoinECKeyPair(keyPair, testNet);
    }

    public static BitCoinECKeyPair parseWIF(String wif) throws ValidationException {
        byte[] decode = Base58.decode(wif);

        //判断私钥格式
        byte[] check = new byte[4];
        System.arraycopy(decode, decode.length - 4, check, 0, check.length);
        byte[] privateCheck = SHA256.doubleSha256(decode, 0, decode.length - 4);
        for (int i = 0; i < 4; i++) {
            if (check[i] != privateCheck[i]) {
                throw new ValidationException("WIF Private Key Incorrect format");
            }
        }

        boolean isCompressed = false;
        if (decode.length == RAW_PRIVATE_KEY_COMPRESSED_LENGTH) {
            isCompressed = true;
        }

        boolean isTestNet = false;
        if ((decode[0] & 0xFF) == TEST_NET_PRIVATE_KEY_PREFIX) {
            isTestNet = true;
        }

        byte[] bytes = new byte[32];
        System.arraycopy(decode, 1, bytes, 0, bytes.length);

        return new BitCoinECKeyPair(bytes, isTestNet, isCompressed);
    }

    public BitCoinECKeyPair(byte[] p, boolean testNet, boolean compressed) throws
            ValidationException {
        super(p, compressed);
        this.testNet = testNet;
    }

    public BitCoinECKeyPair(BigInteger priv, boolean testNet, boolean compressed) {
        super(priv, compressed);
        this.testNet = testNet;
    }

    public BitCoinECKeyPair(Key keyPair, boolean testNet) {
        super(keyPair);
        this.testNet = testNet;
    }

    protected BitCoinECKeyPair(BigInteger priv, boolean compressed) throws ValidationException {
        super(priv, compressed);
        this.testNet = false;
    }

    public boolean isTestNet() {
        return testNet;
    }

    @Override
    public String getPrivateKey() {
        byte[] bytes = getRawPrivateKey();
        byte[] rawPrivateKey = new byte[isCompressed() ? RAW_PRIVATE_KEY_COMPRESSED_LENGTH :
                RAW_PRIVATE_KEY_NO_COMPRESSED_LENGTH];
        System.arraycopy(bytes, 0, rawPrivateKey, 1, bytes.length);

        rawPrivateKey[0] = (byte) (isTestNet() ? TEST_NET_PRIVATE_KEY_PREFIX :
                MAIN_NET_PRIVATE_KEY_PREFIX);
        if (isCompressed()) {
            rawPrivateKey[rawPrivateKey.length - 5] = MAIN_NET_PRIVATE_KEY_SUFFIX;
        }

        byte[] check = SHA256.doubleSha256(rawPrivateKey, 0, rawPrivateKey.length - 4);
        System.arraycopy(check, 0, rawPrivateKey, rawPrivateKey.length - 4, 4);
        return Base58.encode(rawPrivateKey);
    }

    @Override
    public String getPublicKey() {
        return HexUtils.toHex(getRawPublicKey());
    }

    @Override
    public String getAddress() {
        return Base58.encode(getRawAddress());
    }

    @Override
    public byte[] getRawPublicKey() {
        return getRawPublicKey(isCompressed());
    }

    @Override
    public byte[] getRawAddress() {
        byte[] hashedPublicKey;
        //进行 Sha256 Ripemd160 运算
        if (isCompressed()) {
            hashedPublicKey = RIPEMD160.hash160(pubComp);
        } else {
            hashedPublicKey = RIPEMD160.hash160(pub);
        }
        byte[] addressBytes = new byte[1 + hashedPublicKey.length + 4];
        //拼接测试网络或正式网络前缀
        addressBytes[0] = (byte) (testNet ? TEST_NET_ADDRESS_SUFFIX : MAIN_NET_ADDRESS_SUFFIX);

        System.arraycopy(hashedPublicKey, 0, addressBytes, 1, hashedPublicKey.length);
        //进行双 Sha256 运算
        byte[] check = SHA256.doubleSha256(addressBytes, 0, addressBytes.length - 4);

        //将双 Sha256 运算的结果前 4位 拼接到尾部
        System.arraycopy(check, 0, addressBytes, hashedPublicKey.length + 1, 4);

        Arrays.fill(hashedPublicKey, (byte) 0);
        Arrays.fill(check, (byte) 0);
        return addressBytes;
    }

    @Override
    public BTCTransaction sign(byte[] messageHash) throws ValidationException {
        try {
            BTCTransaction btcTransaction = new BTCTransaction(messageHash);
            return signTransaction(btcTransaction, priv, getRawPublicKey(), getAddress());
        } catch (BitcoinException e) {
            e.printStackTrace();
            throw new ValidationException(e);
        }
    }

    public byte[] signBTC(byte[] hash) {
        ECDSASigner signer = new ECDSASigner();
        signer.init(true, new ECPrivateKeyParameters(priv, domain));
        BigInteger[] signature = signer.generateSignature(hash);
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        try {
            DERSequenceGenerator seq = new DERSequenceGenerator(s);
            seq.addObject(new DERInteger(signature[0]));
            seq.addObject(new DERInteger(signature[1]));
            seq.close();
            return s.toByteArray();
        } catch (IOException e) {
        }
        return null;
    }

    public boolean verifyBTC(byte[] hash, byte[] signature) {
        return verify(hash, signature, pubComp);
    }

    private static void checkChecksum(byte[] store) throws ValidationException {
        byte[] checksum = new byte[4];
        System.arraycopy(store, store.length - 4, checksum, 0, 4);
        byte[] ekey = new byte[store.length - 4];
        System.arraycopy(store, 0, ekey, 0, store.length - 4);
        byte[] hash = SHA256.doubleSha256(ekey);
        for (int i = 0; i < 4; ++i) {
            if (hash[i] != checksum[i]) {
                throw new ValidationException("checksum mismatch");
            }
        }
    }

    public static BTCTransaction signTransaction(BTCTransaction transaction, BigInteger
            privateKeyBigInteger, byte[] publicKey, String address) {
        BTCTransaction.Input[] signedInput = new BTCTransaction.Input[transaction.inputs.length];
        for (int i = 0; i < transaction.inputs.length; i++) {
            signedInput[i] = sign(transaction, i, privateKeyBigInteger, publicKey, address,
                    BTCTransaction.Script.SIGHASH_ALL);
        }
        return new BTCTransaction(signedInput, transaction.outputs, transaction.lockTime);
    }

    /**
     * 对单个Input进行签名
     *
     * @param transaction 交易
     * @param index       Input索引
     * @reture 签名完毕的Input
     */
    public static BTCTransaction.Input sign(BTCTransaction transaction, int index, BigInteger
            privateKeyBigInteger, byte[] publicKey, String address, byte ScriptType) {
        String publicScript = mkPubKeyScript(address);
        //清空无关Input的Script,对相关Script进行签名
        signatureForm(transaction, index, publicScript);

        //双 Sha256 and 签名
        byte[] signature = sign(privateKeyBigInteger, BTCTransaction.Script
                .hashTransactionForSigning(transaction));

        //拼版本
        byte[] signatureAndHashType = new byte[signature.length + 1];
        System.arraycopy(signature, 0, signatureAndHashType, 0, signature.length);
        signatureAndHashType[signatureAndHashType.length - 1] = ScriptType;

        //拼出新的input
        return new BTCTransaction.Input(transaction.inputs[index].outPoint, new BTCTransaction
                .Script(signatureAndHashType, publicKey), transaction.inputs[index].sequence);
    }

    /**
     * 设置Input的地址签名
     *
     * @param transaction  交易
     * @param index        索引
     * @param publicScript 地址签名
     * @return 新的交易
     */
    private static BTCTransaction signatureForm(BTCTransaction transaction, int index, String
            publicScript) {
        //存放清空地址签名的Input
        for (int i = 0; i < transaction.inputs.length; i++) {
            BTCTransaction.Script script = null;
            if (i == index) {
                //设置地址签名
                script = new BTCTransaction.Script(HexUtils.fromHex(publicScript));

            }
            transaction.inputs[i] = new BTCTransaction.Input(transaction.inputs[i].outPoint,
                    script, transaction.inputs[i].sequence);
        }
        return transaction;
    }

    /**
     * 签署地址签名
     *
     * @param address 地址
     * @return 签名
     */
    private static String mkPubKeyScript(String address) {
        //设置加密格式
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("76a914");

        //设置签名
        byte[] bytes = Base58Check.base58ToBytes(address);
        byte[] checkByte = new byte[bytes.length - 1];
        System.arraycopy(bytes, 1, checkByte, 0, checkByte.length);
        stringBuilder.append(HexUtils.toHex(checkByte));

        Arrays.fill(bytes, (byte) 0);
        Arrays.fill(checkByte, (byte) 0);

        //设置签名后缀格式
        stringBuilder.append("88ac");
        return stringBuilder.toString();
    }

    public static byte[] sign(BigInteger privateKey, byte[] input) {
        synchronized (domain) {
            ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
            ECPrivateKeyParameters privateKeyParam = new ECPrivateKeyParameters(privateKey, domain);
            signer.init(true, new ParametersWithRandom(privateKeyParam, secureRandom));
            BigInteger[] sign = signer.generateSignature(input);
            BigInteger r = sign[0];
            BigInteger s = sign[1];
            BigInteger largestAllowedS = new BigInteger
                    ("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF5D576E7357A4501DDFE92F46681B20A0", 16);
            //SECP256K1_N_DIV_2
            if (s.compareTo(largestAllowedS) > 0) {
                //https://github.com/bitcoin/bips/blob/master/bip-0062.mediawiki#low-s-values-in
                // -signatures
                s = LARGEST_PRIVATE_KEY.subtract(s);
            }
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream(72);
                DERSequenceGenerator derGen = new DERSequenceGenerator(baos);
                derGen.addObject(new ASN1Integer(r));
                derGen.addObject(new ASN1Integer(s));
                derGen.close();
                return baos.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
