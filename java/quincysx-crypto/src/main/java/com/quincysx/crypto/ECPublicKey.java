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


import com.quincysx.crypto.utils.RIPEMD160;

import org.spongycastle.util.Arrays;

public class ECPublicKey implements Key {
    private byte[] pub;
    private boolean compressed;

    public ECPublicKey(byte[] pub, boolean compressed) {
        this.pub = pub;
        this.compressed = compressed;
    }

    @Override
    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    @Override
    public byte[] getRawPrivateKey() {
        throw new RuntimeException("Please use private key to sign signature");
    }

    @Override
    public byte[] getRawPublicKey(boolean isCompressed) {
        if (!isCompressed) {
            throw new RuntimeException("No compressed public key");
        }
        return Arrays.clone(pub);
    }

    @Override
    public byte[] getRawPublicKey() {
        return Arrays.clone(pub);
    }

    @Override
    public byte[] getRawAddress() {
        return RIPEMD160.hash160(pub);
    }

    @Override
    public String getPrivateKey() {
        throw new RuntimeException("Please use private key to sign signature");
    }

    @Override
    public String getPublicKey() {
        throw new RuntimeException("No formatted public Key");
    }

    @Override
    public String getAddress() {
        throw new RuntimeException("No formatted address");
    }

    @Override
    public ECPublicKey clone() throws CloneNotSupportedException {
        ECPublicKey c = (ECPublicKey) super.clone();
        c.pub = Arrays.clone(pub);
        return c;
    }

    @Override
    public <T> T sign(byte[] messageHash) {
        throw new RuntimeException("Please use private key to sign signature");
    }

}
