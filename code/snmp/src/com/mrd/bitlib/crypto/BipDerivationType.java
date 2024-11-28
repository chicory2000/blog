package com.mrd.bitlib.crypto;

import java.io.Serializable;

import com.mrd.bitlib.model.AddressType;
import com.mrd.bitlib.model.BitcoinAddress;

public enum BipDerivationType implements Serializable {
    	BIP44(44, AddressType.P2PKH),
        BIP49(49, AddressType.P2SH_P2WPKH),
        BIP84(84, AddressType.P2WPKH);

	private final long serialVersionUID = 1L;
    private final int mpurpose;
    private final AddressType maddressType;

	BipDerivationType(int purpose, AddressType addressType) {
		mpurpose = purpose;
		maddressType = addressType;
    }

	public final AddressType getAddressType() {
        return maddressType;
    }

    public final int getHardenedPurpose() {
        return mpurpose + ((int) 0x80000000);
    }

    public static BipDerivationType getDerivationTypeByAddress(BitcoinAddress bitcoinAddress) {
        //Intrinsics.checkParameterIsNotNull(bitcoinAddress, "address");
        AddressType type = bitcoinAddress.getType();
        //Intrinsics.checkExpressionValueIsNotNull(type, "address.type");
        return getDerivationTypeByAddressType(type);
    }
    
    public static BipDerivationType getDerivationTypeByAddressType(AddressType addressType) {
        if (addressType == AddressType.P2PKH) {
        	return BIP44;
        }
        if (addressType == AddressType.P2SH_P2WPKH) {
        	return BIP49;
        }
        if (addressType == AddressType.P2WPKH) {
        	return BIP84;
        }
        return BIP44;
    }

    
}