package com.mrd.bitlib.model;

public final class ScriptInputP2WPKH extends ScriptInput {
    private boolean isNested;

    /* JADX INFO: super call moved to the top of the method (can break code semantics) */
    public ScriptInputP2WPKH(byte[] bArr) {
        super(bArr);
    }

    public final boolean isNested() {
        return this.isNested;
    }

    public final void setNested(boolean z) {
        this.isNested = z;
    }
}
