package com.mrd.bitlib;

import com.mrd.bitlib.model.CompactInt;

public final class FeeEstimator {
    private static final int MAX_BECH32_INPUT_SIZE = 125;
    private static final int MAX_INPUT_SIZE = 148;
    private static final int MAX_SEGWIT_COMPAT_INPUT_SIZE = 87;
    private static final int MAX_SEGWIT_NATIVE_INPUT_SIZE = 51;
    private static final int OUTPUT_SIZE = 34;
    private static final int SEGWIT_COMPAT_OUTPUT_SIZE = 32;
    private static final int SEGWIT_NATIVE_OUTPUT_SIZE = 31;
    private final int bechInputs;
    private final int bechOutputs;
    private final int legacyInputs;
    private final int legacyOutputs;
    private final long minerFeePerKb;
    private final int p2shOutputs;
    private final int p2shSegwitInputs;

    public FeeEstimator(int i, int i2, int i3, int i4, int i5, int i6, long j) {
        this.legacyInputs = i;
        this.p2shSegwitInputs = i2;
        this.bechInputs = i3;
        this.legacyOutputs = i4;
        this.p2shOutputs = i5;
        this.bechOutputs = i6;
        this.minerFeePerKb = j;
    }

    public final int estimateTransactionSize() {
        int i = (this.bechOutputs * 31) + (this.p2shOutputs * 32) + (this.legacyOutputs * 34);
        int i2 = this.bechInputs;
        int i3 = this.p2shSegwitInputs;
        int length = (i2 + i3 > 0 ? 2 : 0) + 4 + CompactInt.toBytes((long) (this.legacyInputs + i3 + i2)).length + CompactInt.toBytes((long) (this.legacyOutputs + this.p2shOutputs + this.bechOutputs)).length + i + 4;
        int i4 = this.legacyInputs;
        int i5 = this.p2shSegwitInputs;
        int i6 = this.bechInputs;
        return (((((length + (i5 * 87)) + (i6 * 51)) + (i4 * 148)) * 3) + ((((i4 + i5) * 148) + length) + (i6 * 125))) / 4;
    }

    public final long estimateFee() {
        double estimateTransactionSize = (double) estimateTransactionSize();
        Double.isNaN(estimateTransactionSize);
        return (long) (((float) (estimateTransactionSize / 1000.0d)) * ((float) this.minerFeePerKb));
    }

}
