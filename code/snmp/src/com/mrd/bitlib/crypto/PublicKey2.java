/*
 * Copyright 2013 - 2018 Megion Research & Development GmbH
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

package com.mrd.bitlib.crypto;

import com.mrd.bitlib.crypto.ec.Parameters;
import com.mrd.bitlib.crypto.ec.Point;
import com.mrd.bitlib.model.*;
import com.mrd.bitlib.model.SegwitAddress.SegwitAddressException;
import com.mrd.bitlib.util.*;

import java.io.Serializable;
import java.util.*;
//import kotlin.experimental.and;

public final class PublicKey2 implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	byte[] pubKeyCompressed;
	byte[] publicKeyHash;
	byte[] pubKeyHashCompressed;
	Point Q;
	byte[] mpublicKeyBytes;

    /**
     * Is this a compressed public key?
     */
    public boolean isCompressed;
    
    public PublicKey2(byte[] publicKeyBytes) {
    	mpublicKeyBytes = publicKeyBytes;
    	pubKeyCompressed = compressPublicKey(publicKeyBytes);
    	publicKeyHash = HashUtils.addressHash(publicKeyBytes);
    	pubKeyHashCompressed =HashUtils.addressHash(pubKeyCompressed);
    	Q = Parameters.curve.decodePoint(publicKeyBytes);
    	isCompressed =  Q.isCompressed();
    }

    public final byte[] getPublicKeyBytes() {
        return mpublicKeyBytes;
    }

    public final byte[] getPubKeyCompressed() {
        return (byte[]) pubKeyCompressed;
    }

    public final byte[] getPubKeyHashCompressed() {
        return (byte[]) this.pubKeyHashCompressed;
    }

    public final byte[] getPublicKeyHash() {
        return (byte[]) this.publicKeyHash;
    }

    public final boolean isCompressed() {
        return Q.isCompressed();
    }

    public BitcoinAddress toAddress(NetworkParameters networkParameters, AddressType addressType) {
    	return toAddress(networkParameters, addressType, false);
    }
    /**
     * @param ignoreCompression allows deriving segwit addresses from uncompressed keys. This should
     * only be done to detect lost funds and under no circumstances should these addresses be shown
     * to the user.
     */
    public BitcoinAddress toAddress(NetworkParameters networkParameters, AddressType addressType, boolean ignoreCompression) {
        if (addressType == AddressType.P2PKH) {
        	return toP2PKHAddress(networkParameters);
        }
        if (addressType == AddressType.P2SH_P2WPKH) {
        	return toNestedP2WPKH(networkParameters, ignoreCompression);
        }
        if (addressType == AddressType.P2WPKH) {
        	return toP2WPKH(networkParameters, ignoreCompression);
        }
        return toP2PKHAddress(networkParameters);
    }

    public final Map<AddressType, BitcoinAddress> getAllSupportedAddresses(
    		NetworkParameters networkParameters) {
    	return getAllSupportedAddresses(networkParameters, false);
    }
    
    public final Map<AddressType, BitcoinAddress> getAllSupportedAddresses(
    		NetworkParameters networkParameters, boolean ignoreCompression) {
//        Iterable<AddressType> access$SUPPORTED_ADDRESS_TYPES = Companion.SUPPORTED_ADDRESS_TYPES(isCompressed() || z);
//        Collection arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(access$SUPPORTED_ADDRESS_TYPES, 10));
//        for (AddressType addressType : access$SUPPORTED_ADDRESS_TYPES) {
//            arrayList.add(TuplesKt.to(addressType, toAddress(networkParameters, addressType, z)));
//        }
        Map<AddressType, BitcoinAddress> addressMap = new HashMap<AddressType, BitcoinAddress>();
        if (isCompressed || ignoreCompression) {
        	addressMap.put(AddressType.P2PKH, toAddress(networkParameters, AddressType.P2PKH, ignoreCompression));
        	addressMap.put(AddressType.P2WPKH, toAddress(networkParameters, AddressType.P2WPKH, ignoreCompression));
        	addressMap.put(AddressType.P2SH_P2WPKH, toAddress(networkParameters, AddressType.P2SH_P2WPKH, ignoreCompression));
        	
        } else {
        	addressMap.put(AddressType.P2PKH, toAddress(networkParameters, AddressType.P2PKH, ignoreCompression));
        }
        return addressMap;
    }

    /**
     * @return [AddressType.P2SH_P2WPKH] address
     */
    private BitcoinAddress toNestedP2WPKH(NetworkParameters networkParameters, boolean ignoreCompression) {
        if (ignoreCompression || isCompressed) {
            return BitcoinAddress.fromP2SHBytes(HashUtils.addressHash(
                    BitUtils.concatenate(new byte[]{(byte) 0, (byte) pubKeyHashCompressed.length}, pubKeyHashCompressed)),
                    networkParameters);
        }
        return null;
        //throw IllegalStateException("Can't create segwit address from uncompressed key");
    }

    /**
     * @return [AddressType.P2WPKH] address
     */
    private SegwitAddress toP2WPKH(NetworkParameters networkParameters, boolean ignoreCompression) {
            if (ignoreCompression || isCompressed) {
            	try {
					return new SegwitAddress(networkParameters, 0, HashUtils.addressHash(getPubKeyCompressed()));
				} catch (SegwitAddressException e) {
					e.printStackTrace();
				}
            } else {
                //throw IllegalStateException("Can't create segwit address from uncompressed key")
            }
            return null;
    }

    /**
     * @return [AddressType.P2PKH] address
     */
    private BitcoinAddress toP2PKHAddress(NetworkParameters networkParameters) {
            return BitcoinAddress.fromStandardBytes(publicKeyHash, networkParameters);
    }

    public int hashCode() {
        int i = 0;
        for (byte b : publicKeyHash) {
            i = (i << 8) + ((byte) (b & ((byte) 255)));
        }
        return i;
    }

    boolean equals(PublicKey2 other) {
//        if (other !is PublicKey) {
//            return false
//        }
        return Arrays.equals(publicKeyHash, other.publicKeyHash);
    }

    public String toString() {
        return HexUtils.toHex(mpublicKeyBytes);
    }

//    fun verifyStandardBitcoinSignature(data: Sha256Hash, signature: ByteArray, forceLowS: Boolean): Boolean {
//        // Decode parameters r and s
//        val reader = ByteReader(signature)
//        val params = Signatures.decodeSignatureParameters(reader) ?: return false
//        // Make sure that we have a hash type at the end
//        if (reader.available() != HASH_TYPE) {
//            return false
//        }
//        return if (forceLowS) {
//            Signatures.verifySignatureLowS(data.bytes, params, Q)
//        } else {
//            Signatures.verifySignature(data.bytes, params, Q)
//        }
//    }
//
//    // same as verifyStandardBitcoinSignature, but don't enforce the hash-type check
//    fun verifyDerEncodedSignature(data: Sha256Hash, signature: ByteArray): Boolean {
//        // Decode parameters r and s
//        val reader = ByteReader(signature)
//        val params = Signatures.decodeSignatureParameters(reader) ?: return false
//        return Signatures.verifySignature(data.bytes, params, Q)
//    }

        private static int HASH_TYPE = 1;
        
//        private fun SUPPORTED_ADDRESS_TYPES(isCompressed: Boolean) = if (isCompressed) {
//            listOf(AddressType.P2PKH, AddressType.P2WPKH, AddressType.P2SH_P2WPKH
//            )
//        } else {
//            // P2WPKH (and native P2WSH) do not allow uncompressed public keys as per
//            // [BIP143](https://github.com/bitcoin/bips/blob/master/bip-0143.mediawiki#restrictions-on-public-key-type).
//            // although we create addresses compressing the uncompressed key first, this is not
//            // standard, so we don't show receiving addresses of this type and neither send change
//            // there in order to maintain compatibility.
//            listOf(AddressType.P2PKH)
//        }

        public static byte[] compressPublicKey(byte[] publicKey) {
            switch (publicKey[0]) {
               case 0x04:
                  if (publicKey.length != 65) {
                     //throw new BTChipException("Invalid public key");
                  }
                  break;
               case 0x02:
               case 0x03:
                  if (publicKey.length != 33) {
                     //throw new BTChipException("Invalid public key");
                  }
                  return publicKey;
               default:
                  //throw new BTChipException("Invalid public key");
            }
            byte[] result = new byte[33];
            result[0] = (((publicKey[64] & 1) != 0) ? (byte) 0x03 : (byte) 0x02);
            System.arraycopy(publicKey, 1, result, 1, 32);
            return result;
         }


 
}
