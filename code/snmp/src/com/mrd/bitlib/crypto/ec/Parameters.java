/*
 * Copyright 2013, 2014 Megion Research & Development GmbH
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mrd.bitlib.crypto.ec;

import java.math.BigInteger;
import com.mrd.bitlib.util.HexUtils;

public class Parameters {
	public static BigInteger p = new BigInteger(1,
            HexUtils.toBytes("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F"));
	public static BigInteger a = BigInteger.ZERO;
	public static BigInteger b = BigInteger.valueOf(7);
	public static Curve curve = new Curve(p, a, b);
    public static Point G = curve.decodePoint(HexUtils.toBytes("04" + "79BE667EF9DCBBAC55A06295CE870B07029BFCDB2DCE28D959F2815B16F81798"
            + "483ADA7726A3C4655DA4FBFC0E1108A8FD17B448A68554199C47D08FFB10D4B8"));
    public static BigInteger n = new BigInteger(1, HexUtils.toBytes("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141"));
    public static BigInteger h = BigInteger.ONE;
    /**
     * The maximum number a signature can have in version 3 transactions
     */
    public static BigInteger MAX_SIG_S = new BigInteger(1, HexUtils.toBytes("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF5D576E7357A4501DDFE92F46681B20A0"));

}
