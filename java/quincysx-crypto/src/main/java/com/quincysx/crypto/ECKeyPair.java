/*
 * Copyright 2013 bits of proof zrt.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.quincysx.crypto;

import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;

import org.spongycastle.asn1.ASN1InputStream;
import org.spongycastle.asn1.DERInteger;
import org.spongycastle.asn1.DLSequence;
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.AsymmetricCipherKeyPair;
import org.spongycastle.crypto.generators.ECKeyPairGenerator;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.crypto.params.ECKeyGenerationParameters;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.math.ec.ECPoint;
import org.spongycastle.util.Arrays;

import com.quincysx.crypto.bip32.ValidationException;
import com.quincysx.crypto.utils.RIPEMD160;


public class ECKeyPair implements Key {
    protected static final SecureRandom secureRandom = new SecureRandom();
    protected static final X9ECParameters CURVE = SECNamedCurves.getByName("secp256k1");
    protected static final ECDomainParameters domain = new ECDomainParameters(CURVE.getCurve(),
            CURVE.getG(), CURVE.getN(), CURVE.getH());
    protected static final BigInteger LARGEST_PRIVATE_KEY = new BigInteger
            ("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);

    protected BigInteger priv;
    protected byte[] pub;
    protected byte[] pubComp;
    protected boolean compressed;

    protected ECKeyPair() {
    }

    public ECKeyPair(byte[] p, boolean compressed) throws ValidationException {
        this(new BigInteger(1, p), compressed);
        if (!(p.length == 32 || p.length == 43 )) {
            throw new ValidationException("Invalid private key");
        }
    }

    public ECKeyPair(BigInteger priv, boolean compressed) {
        this.priv = priv;
        this.compressed = compressed;

        ECPoint multiply = CURVE.getG().multiply(priv);
        this.pub = multiply.getEncoded(false);
        this.pubComp = multiply.getEncoded(true);
    }

    protected ECKeyPair(Key keyPair) {
        this.priv = new BigInteger(1, keyPair.getRawPrivateKey());
        this.compressed = keyPair.isCompressed();
        this.pub = Arrays.clone(keyPair.getRawPublicKey(false));
        this.pubComp = Arrays.clone(keyPair.getRawPublicKey());
    }

    @Override
    public boolean isCompressed() {
        return compressed;
    }

    @Override
    public ECKeyPair clone() throws CloneNotSupportedException {
        ECKeyPair c = (ECKeyPair) super.clone();
        c.priv = new BigInteger(c.priv.toByteArray());
        c.pub = Arrays.clone(pub);
        c.pubComp = Arrays.clone(pubComp);
        c.compressed = compressed;
        return c;
    }

    public static ECKeyPair createNew(boolean compressed) {
        ECKeyPairGenerator generator = new ECKeyPairGenerator();
        ECKeyGenerationParameters keygenParams = new ECKeyGenerationParameters(domain,
                secureRandom);
        generator.init(keygenParams);
        AsymmetricCipherKeyPair keypair = generator.generateKeyPair();
        ECPrivateKeyParameters privParams = (ECPrivateKeyParameters) keypair.getPrivate();
        ECPublicKeyParameters pubParams = (ECPublicKeyParameters) keypair.getPublic();
        ECKeyPair k = new ECKeyPair();
        k.priv = privParams.getD();
        k.compressed = compressed;
        ECPoint multiply = CURVE.getG().multiply(k.priv);
        k.pub = multiply.getEncoded(false);
        k.pubComp = multiply.getEncoded(true);
        return k;
    }

    public void setPublic(byte[] pub) throws ValidationException {
        throw new ValidationException("Can not set public key if private is present");
    }

    @Override
    public byte[] getRawPrivateKey() {
        byte[] p = priv.toByteArray();

        if (p.length != 32) {
            byte[] tmp = new byte[32];
            System.arraycopy(p, Math.max(0, p.length - 32), tmp, Math.max(0, 32 - p.length), Math
                    .min(32, p.length));
            p = tmp;
        }
        return p;
    }

    @Override
    public byte[] getRawPublicKey(boolean isCompressed) {
        if (isCompressed) {
            return Arrays.clone(pubComp);
        } else {
            return Arrays.clone(pub);
        }
    }

    @Override
    public byte[] getRawPublicKey() {
        return getRawPublicKey(true);
    }

    @Override
    public byte[] getRawAddress() {
        return RIPEMD160.hash160(pubComp);
    }

    @Override
    public String getPrivateKey() {
        throw new RuntimeException("No formatted private Key");
//    	return Hex.toHexString(this.getRawPrivateKey());
    }

    @Override
    public String getPublicKey() {
        throw new RuntimeException("No formatted public Key");
    	
//        return Hex.toHexString(this.getRawPublicKey());
    }

    @Override
    public String getAddress() {
        throw new RuntimeException("No formatted address");
//        return Hex.toHexString(this.getRawAddress());
    }

    @Override
    public <T> T sign(byte[] messageHash) throws ValidationException {
        throw new ValidationException("Please convert to ECKeyPair subclass signature");
    }

    public static boolean verify(byte[] hash, byte[] signature, byte[] pub) {
        ASN1InputStream asn1 = new ASN1InputStream(signature);
        try {
            ECDSASigner signer = new ECDSASigner();
            signer.init(false, new ECPublicKeyParameters(CURVE.getCurve().decodePoint(pub),
                    domain));

            DLSequence seq = (DLSequence) asn1.readObject();
            BigInteger r = ((DERInteger) seq.getObjectAt(0)).getPositiveValue();
            BigInteger s = ((DERInteger) seq.getObjectAt(1)).getPositiveValue();
            
            return signer.verifySignature(hash, r, s);
        } catch (Exception e) {
            // threat format errors as invalid signatures
            return false;
        } finally {
            try {
                asn1.close();
            } catch (IOException e) {
            }
        }
    }
}
