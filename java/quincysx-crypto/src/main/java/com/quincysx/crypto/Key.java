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


import com.quincysx.crypto.bip32.ValidationException;

public interface Key extends Cloneable {
    /**
     * 获取原生私钥
     *
     * @return
     */
    public byte[] getRawPrivateKey();

    /**
     * 获取公钥
     *
     * @param isCompressed 是否压缩
     * @return 原生公钥
     */
    public byte[] getRawPublicKey(boolean isCompressed);

    /**
     * 获取原生压缩公钥
     *
     * @return
     */
    public byte[] getRawPublicKey();

    /**
     * 获取地址
     *
     * @return
     */
    public byte[] getRawAddress();

    /**
     * 获取格式化的私钥
     *
     * @return
     */
    public String getPrivateKey();

    /**
     * 获取格式化的公钥
     *
     * @return
     */
    public String getPublicKey();

    /**
     * 获取格式化的地址
     *
     * @return
     */
    public String getAddress();

    /**
     * 获取判断公钥是否是压缩格式
     *
     * @return
     */
    public boolean isCompressed();

    public Key clone() throws CloneNotSupportedException;

    public <T extends Object> T sign(byte[] messageHash) throws ValidationException;

}