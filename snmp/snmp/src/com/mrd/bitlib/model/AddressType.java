package com.mrd.bitlib.model;

import java.io.Serializable;

public enum AddressType implements Serializable {
    P2PKH, // Legacy
    P2WPKH, // Supported
    P2SH_P2WPKH; // Default
    //P2PK, // Not supported
    //P2SH, // Not supported, use P2SH_P2WPKH
    //P2WSH, // Not supported
    //P2SH_P2WSH // Not supported

    private final long serialVersionUID = 1L;
}
