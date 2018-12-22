package com.quincysx.crypto.utils;

import com.quincysx.crypto.bitcoin.BTCTransaction;

import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.ASN1Integer;
import org.spongycastle.asn1.DLSequence;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Stack;

/**
 * @author QuincySx
 * @date 2018/3/2 下午3:13
 */
public class BTCUtils {
    public static byte[] reverse(byte[] bytes) {
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[bytes.length - i - 1];
        }
        return result;
    }

    public static void verify(BTCTransaction.Script[] scripts, BTCTransaction spendTx) throws BTCTransaction.Script.ScriptInvalidException {
        for (int i = 0; i < scripts.length; i++) {
            Stack<byte[]> stack = new Stack<>();
            spendTx.inputs[i].script.run(stack);//load signature+public key
            scripts[i].run(i, spendTx, stack); //verify that this transaction able to spend that output
            if (BTCTransaction.Script.verifyFails(stack)) {
                throw new BTCTransaction.Script.ScriptInvalidException("Signature is invalid");
            }
        }
    }

    public static boolean verify(byte[] publicKey, byte[] signature, byte[] msg) {
        X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ECDomainParameters EC_PARAMS = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
        synchronized (EC_PARAMS) {
            boolean valid;
            ECDSASigner signerVer = new ECDSASigner(new HMacDSAKCalculator(new SHA256Digest()));
            try {
                ECPublicKeyParameters pubKey = new ECPublicKeyParameters(EC_PARAMS.getCurve().decodePoint(publicKey), EC_PARAMS);
                signerVer.init(false, pubKey);
                ASN1InputStream derSigStream = new ASN1InputStream(signature);
                DLSequence seq = (DLSequence) derSigStream.readObject();
                BigInteger r = ((ASN1Integer) seq.getObjectAt(0)).getPositiveValue();
                BigInteger s = ((ASN1Integer) seq.getObjectAt(1)).getPositiveValue();
                derSigStream.close();
                valid = signerVer.verifySignature(msg, r, s);
            } catch (IOException e) {
                throw new RuntimeException();
            }
            return valid;
        }
    }
}
