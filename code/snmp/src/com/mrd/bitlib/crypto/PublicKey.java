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

public final class PublicKey implements Serializable {
	private static final long serialVersionUID = 1L;
	private byte[] pubKeyCompressed;
	private byte[] publicKeyHash;
	private byte[] pubKeyHashCompressed;
	private Point Q;
	private byte[] publicKeyBytes;

	/**
	 * Is this a compressed public key?
	 */
	public boolean isCompressed;

	public PublicKey(byte[] publicKeyBytesIn) {
		publicKeyBytes = publicKeyBytesIn;
		pubKeyCompressed = compressPublicKey(publicKeyBytesIn);
		publicKeyHash = HashUtils.addressHash(publicKeyBytesIn);
		pubKeyHashCompressed = HashUtils.addressHash(pubKeyCompressed);
		Q = Parameters.curve.decodePoint(publicKeyBytesIn);
		isCompressed = Q.isCompressed();
	}

	public final byte[] getPublicKeyBytes() {
		return publicKeyBytes;
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

	public BitcoinAddress toAddress(NetworkParameters networkParameters,
			AddressType addressType) {
		return toAddress(networkParameters, addressType, false);
	}

	/**
	 * @param ignoreCompression
	 *            allows deriving segwit addresses from uncompressed keys. This
	 *            should only be done to detect lost funds and under no
	 *            circumstances should these addresses be shown to the user.
	 */
	public BitcoinAddress toAddress(NetworkParameters networkParameters,
			AddressType addressType, boolean ignoreCompression) {
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
		// Iterable<AddressType> access$SUPPORTED_ADDRESS_TYPES =
		// Companion.SUPPORTED_ADDRESS_TYPES(isCompressed() || z);
		// Collection arrayList = new
		// ArrayList(CollectionsKt.collectionSizeOrDefault(access$SUPPORTED_ADDRESS_TYPES,
		// 10));
		// for (AddressType addressType : access$SUPPORTED_ADDRESS_TYPES) {
		// arrayList.add(TuplesKt.to(addressType, toAddress(networkParameters,
		// addressType, z)));
		// }
		Map<AddressType, BitcoinAddress> addressMap = new HashMap<AddressType, BitcoinAddress>();
		if (isCompressed || ignoreCompression) {
			addressMap.put(
					AddressType.P2PKH,
					toAddress(networkParameters, AddressType.P2PKH,
							ignoreCompression));
			addressMap.put(
					AddressType.P2WPKH,
					toAddress(networkParameters, AddressType.P2WPKH,
							ignoreCompression));
			addressMap.put(
					AddressType.P2SH_P2WPKH,
					toAddress(networkParameters, AddressType.P2SH_P2WPKH,
							ignoreCompression));

		} else {
			addressMap.put(
					AddressType.P2PKH,
					toAddress(networkParameters, AddressType.P2PKH,
							ignoreCompression));
		}
		return addressMap;
	}

	/**
	 * @return [AddressType.P2SH_P2WPKH] address
	 */
	private BitcoinAddress toNestedP2WPKH(NetworkParameters networkParameters,
			boolean ignoreCompression) {
		if (ignoreCompression || isCompressed) {
			return BitcoinAddress.fromP2SHBytes(HashUtils.addressHash(BitUtils
					.concatenate(new byte[] { (byte) 0,
							(byte) pubKeyHashCompressed.length },
							pubKeyHashCompressed)), networkParameters);
		}
		return null;
		// throw
		// IllegalStateException("Can't create segwit address from uncompressed key");
	}

	/**
	 * @return [AddressType.P2WPKH] address
	 */
	private SegwitAddress toP2WPKH(NetworkParameters networkParameters,
			boolean ignoreCompression) {
		if (ignoreCompression || isCompressed) {
			try {
				return new SegwitAddress(networkParameters, 0,
						HashUtils.addressHash(getPubKeyCompressed()));
			} catch (SegwitAddressException e) {
				e.printStackTrace();
			}
		} else {
			// throw
			// IllegalStateException("Can't create segwit address from uncompressed key")
		}
		return null;
	}

	/**
	 * @return [AddressType.P2PKH] address
	 */
	private BitcoinAddress toP2PKHAddress(NetworkParameters networkParameters) {
		return BitcoinAddress.fromStandardBytes(publicKeyHash,
				networkParameters);
	}

	public int hashCode() {
		byte[] bytes = getPublicKeyHash();
		int hash = 0;
		for (int i = 0; i < bytes.length; i++) {
			hash = (hash << 8) + (bytes[i] & 0xff);
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof PublicKey)) {
			return false;
		}
		PublicKey other = (PublicKey) obj;
		return Arrays.equals(getPublicKeyHash(), other.getPublicKeyHash());
	}

	public String toString() {
		return HexUtils.toHex(publicKeyBytes);
	}

	public boolean verifyStandardBitcoinSignature(Sha256Hash data,
			byte[] signature, boolean forceLowS) {
		// Decode parameters r and s
		ByteReader reader = new ByteReader(signature);
		Signature params = Signatures.decodeSignatureParameters(reader);
		if (params == null) {
			return false;
		}
		// Make sure that we have a hash type at the end
		if (reader.available() != 1) {
			return false;
		}
		if (forceLowS) {
			return Signatures.verifySignatureLowS(data.getBytes(), params,
					Q);
		} else {
			return Signatures.verifySignature(data.getBytes(), params, Q);
		}

	}

	// same as verifyStandardBitcoinSignature, but dont enforce the hash-type
	// check
	public boolean verifyDerEncodedSignature(Sha256Hash data, byte[] signature) {
		// Decode parameters r and s
		ByteReader reader = new ByteReader(signature);
		Signature params = Signatures.decodeSignatureParameters(reader);
		if (params == null) {
			return false;
		}
		return Signatures.verifySignature(data.getBytes(), params, Q);
	}

	private static int HASH_TYPE = 1;

	public static byte[] compressPublicKey(byte[] publicKey) {
		switch (publicKey[0]) {
		case 0x04:
			if (publicKey.length != 65) {
				//throw new Exception("Invalid public key");
				android.util.Log.e("PublicKey", "Invalid public key");
			}
			break;
		case 0x02:
		case 0x03:
			if (publicKey.length != 33) {
				// throw new BTChipException("Invalid public key");
				android.util.Log.e("PublicKey", "Invalid public key");
			}
			return publicKey;
		default:
			// throw new BTChipException("Invalid public key");
			android.util.Log.e("PublicKey", "Invalid public key");
		}
		byte[] result = new byte[33];
		result[0] = (((publicKey[64] & 1) != 0) ? (byte) 0x03 : (byte) 0x02);
		System.arraycopy(publicKey, 1, result, 1, 32);
		return result;
	}

}
