package com.mrd.bitlib.model;

import java.io.Serializable;

import com.mrd.bitlib.model.SegwitAddress.SegwitAddressException;
import com.mrd.bitlib.util.BitUtils;

/* compiled from: ScriptOutputP2WPKH.kt */
public final class ScriptOutputP2WPKH extends ScriptOutput implements Serializable {
    private static final long serialVersionUID = 1;
    private final byte[] addressBytes;

    public static final boolean isScriptOutputP2WPKH(byte[][] chunks) {
        return isScriptOutputP2WPKH(chunks);
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ScriptOutputP2WPKH(byte[][] chunks, byte[] scriptBytes) {
        super(scriptBytes);
        this.addressBytes = chunks[1];
    }

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ScriptOutputP2WPKH(byte[] scriptBytes) {
        super(scriptBytes);
        addressBytes = BitUtils.copyOfRange(scriptBytes, 2, scriptBytes.length);;
        ////this.addressBytes = ArraysKt.copyOfRange(bArr, 2, bArr.length);
    }

    public byte[] getAddressBytes() {
        return this.addressBytes;
    }

    public BitcoinAddress getAddress(NetworkParameters networkParameters) {
        try {
			return new SegwitAddress(networkParameters, 0, this.addressBytes);
		} catch (SegwitAddressException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return null;
    }

    public final boolean isScriptOutputP2WPKH2(byte[][] chunks) {
        if (!(((Object[]) chunks).length == 0) && Script.isOP(chunks[0], 0) && chunks[1].length == 20) {
            return true;
        }
        return false;
    }
}
