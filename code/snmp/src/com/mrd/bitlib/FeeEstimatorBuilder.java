package com.mrd.bitlib;

import com.mrd.bitlib.model.AddressType;
import com.mrd.bitlib.model.ScriptOutputP2PKH;
import com.mrd.bitlib.model.ScriptOutputP2SH;
import com.mrd.bitlib.model.ScriptOutputP2WPKH;
//import com.mrd.bitlib.model.ScriptOutputP2WPKH;
import com.mrd.bitlib.model.TransactionOutput;
import com.mrd.bitlib.model.UnspentTransactionOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
//import kotlin.Metadata;
//import kotlin.TypeCastException;
//import kotlin.collections.CollectionsKt;
//import kotlin.jvm.internal.Intrinsics;

public final class FeeEstimatorBuilder {
    private int bechInputs;
    private int bechOutputs;
    private int legacyInputs;
    private int legacyOutputs;
    private long minerFeePerKb;
    private int p2shOutputs;
    private int p2shSegwitInputs;

//    public final /* synthetic */ class WhenMappings {
//        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
//
//        static {
//            int[] iArr = new int[AddressType.values().length];
//            $EnumSwitchMapping$0 = iArr;
//            iArr[AddressType.P2PKH.ordinal()] = 1;
//            iArr[AddressType.P2WPKH.ordinal()] = 2;
//            iArr[AddressType.P2SH_P2WPKH.ordinal()] = 3;
//        }
//    }

    public final FeeEstimatorBuilder setLegacyInputs(int i) {
        this.legacyInputs = i;
        return this;
    }

    public final FeeEstimatorBuilder setP2shSegwitInputs(int i) {
        this.p2shSegwitInputs = i;
        return this;
    }

    public final FeeEstimatorBuilder setBechInputs(int i) {
        this.bechInputs = i;
        return this;
    }

    public final FeeEstimatorBuilder setLegacyOutputs(int i) {
        this.legacyOutputs = i;
        return this;
    }

    public final FeeEstimatorBuilder addOutput(AddressType addressType) {
//        Intrinsics.checkParameterIsNotNull(addressType, "addressType");
        int i = 1;//WhenMappings.$EnumSwitchMapping$0[addressType.ordinal()];
        if (i == 1) {
            this.legacyOutputs++;
        } else if (i == 2) {
            this.bechOutputs++;
        } else if (i == 3) {
            this.p2shOutputs++;
        }
        return this;
    }

    public final FeeEstimatorBuilder setP2shOutputs(int i) {
        this.p2shOutputs = i;
        return this;
    }

    public final FeeEstimatorBuilder setBechOutputs(int i) {
        this.bechOutputs = i;
        return this;
    }

    public final FeeEstimatorBuilder setArrayOfInputs(Iterable<? extends UnspentTransactionOutput> iterable) {
//        Intrinsics.checkParameterIsNotNull(iterable, "inputsList");
//        Object[] array = null;//CollectionsKt.toList(iterable).toArray(new UnspentTransactionOutput[0]);
//        if (array != null) {
//            return setArrayOfInputs((UnspentTransactionOutput[]) array);
//        }
        Collection arrayList = new ArrayList();
        for (UnspentTransactionOutput unspentTransactionOutput : iterable) {
            if (unspentTransactionOutput.script instanceof ScriptOutputP2PKH) {
                arrayList.add(unspentTransactionOutput);
            }
        }
        this.legacyInputs = ((List) arrayList).size();
        return this;
        //throw new Exception("null cannot be cast to non-null type kotlin.Array<T>");
    }

    public final FeeEstimatorBuilder setArrayOfOutputs(Iterable<? extends TransactionOutput> iterable) {
//        Intrinsics.checkParameterIsNotNull(iterable, "inputsList");
//        Object[] array = null;//Collections.toList(iterable).toArray(new TransactionOutput[0]);
//        if (array != null) {
//            return setArrayOfOutputs((TransactionOutput[]) array);
//        }
    	Collection arrayList = new ArrayList();
        for (TransactionOutput transactionOutput : iterable) {
            if (transactionOutput.script instanceof ScriptOutputP2PKH) {
                arrayList.add(transactionOutput);
            }
        }
        this.legacyOutputs = ((List) arrayList).size();
        return this;
        //throw new Exception("null cannot be cast to non-null type kotlin.Array<T>");
    }

    public final FeeEstimatorBuilder setMinerFeePerKb(long j) {
        this.minerFeePerKb = j;
        return this;
    }

    public final FeeEstimator createFeeEstimator() {
        return new FeeEstimator(this.legacyInputs, this.p2shSegwitInputs, this.bechInputs, this.legacyOutputs, this.p2shOutputs, this.bechOutputs, this.minerFeePerKb);
    }

    public final FeeEstimatorBuilder setArrayOfInputs(UnspentTransactionOutput[] unspentTransactionOutputArr) {
//        Intrinsics.checkParameterIsNotNull(unspentTransactionOutputArr, "inputsArray");
        Collection arrayList = new ArrayList();
        for (UnspentTransactionOutput unspentTransactionOutput : unspentTransactionOutputArr) {
            if (unspentTransactionOutput.script instanceof ScriptOutputP2PKH) {
                arrayList.add(unspentTransactionOutput);
            }
        }
        this.legacyInputs = ((List) arrayList).size();
        Collection arrayList2 = new ArrayList();
        for (UnspentTransactionOutput unspentTransactionOutput2 : unspentTransactionOutputArr) {
            if (unspentTransactionOutput2.script instanceof ScriptOutputP2SH) {
                arrayList2.add(unspentTransactionOutput2);
            }
        }
        this.p2shSegwitInputs = ((List) arrayList2).size();
        Collection arrayList3 = new ArrayList();
        for (UnspentTransactionOutput unspentTransactionOutput3 : unspentTransactionOutputArr) {
            if (unspentTransactionOutput3.script instanceof ScriptOutputP2WPKH) {
                arrayList3.add(unspentTransactionOutput3);
            }
        }
        this.bechInputs = ((List) arrayList3).size();
        return this;
    }

    public final FeeEstimatorBuilder setArrayOfOutputs(TransactionOutput[] transactionOutputArr) {
        if (transactionOutputArr != null) {
            Collection arrayList = new ArrayList();
            for (TransactionOutput transactionOutput : transactionOutputArr) {
                if (transactionOutput.script instanceof ScriptOutputP2PKH) {
                    arrayList.add(transactionOutput);
                }
            }
            this.legacyOutputs = ((List) arrayList).size();
            Collection arrayList2 = new ArrayList();
            for (TransactionOutput transactionOutput2 : transactionOutputArr) {
                if (transactionOutput2.script instanceof ScriptOutputP2SH) {
                    arrayList2.add(transactionOutput2);
                }
            }
            this.p2shOutputs = ((List) arrayList2).size();
            Collection arrayList3 = new ArrayList();
            for (TransactionOutput transactionOutput3 : transactionOutputArr) {
                if (transactionOutput3.script instanceof ScriptOutputP2WPKH) {
                    arrayList3.add(transactionOutput3);
                }
            }
            this.bechOutputs = ((List) arrayList3).size();
        } else {
            this.legacyOutputs = 2;
        }
        return this;
    }
}
