package com.mrd.bitlib;

import com.mrd.bitlib.crypto.PublicKey;
import com.mrd.bitlib.util.Sha256Hash;
import java.io.Serializable;

public final class SigningRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    private PublicKey publicKey;
    private Sha256Hash toSign;

    public static SigningRequest copy$default(SigningRequest signingRequest, PublicKey publicKey2, Sha256Hash sha256Hash, int i, Object obj) {
        if ((i & 1) != 0) {
            publicKey2 = signingRequest.publicKey;
        }
        if ((i & 2) != 0) {
            sha256Hash = signingRequest.toSign;
        }
        return signingRequest.copy(publicKey2, sha256Hash);
    }

    public final PublicKey component1() {
        return this.publicKey;
    }

    public final Sha256Hash component2() {
        return this.toSign;
    }

    public final SigningRequest copy(PublicKey publicKey2, Sha256Hash sha256Hash) {
        return new SigningRequest(publicKey2, sha256Hash);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SigningRequest)) {
            return false;
        }
        SigningRequest signingRequest = (SigningRequest) obj;
        return true;//Intrinsics.areEqual((Object) this.publicKey, (Object) signingRequest.publicKey) && Intrinsics.areEqual((Object) this.toSign, (Object) signingRequest.toSign);
    }

    public int hashCode() {
        PublicKey publicKey2 = this.publicKey;
        int i = 0;
        int hashCode = (publicKey2 != null ? publicKey2.hashCode() : 0) * 31;
        Sha256Hash sha256Hash = this.toSign;
        if (sha256Hash != null) {
            i = sha256Hash.hashCode();
        }
        return hashCode + i;
    }

    public String toString() {
        return "SigningRequest(publicKey=" + this.publicKey + ", toSign=" + this.toSign + ")";
    }

    public SigningRequest(PublicKey publicKey2, Sha256Hash sha256Hash) {
        this.publicKey = publicKey2;
        this.toSign = sha256Hash;
    }

    public final PublicKey getPublicKey() {
        return this.publicKey;
    }

    public final Sha256Hash getToSign() {
        return this.toSign;
    }

    public final void setPublicKey(PublicKey publicKey2) {
        this.publicKey = publicKey2;
    }

    public final void setToSign(Sha256Hash sha256Hash) {
        this.toSign = sha256Hash;
    }
}
