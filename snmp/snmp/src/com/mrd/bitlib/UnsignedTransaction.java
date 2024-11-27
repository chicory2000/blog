package com.mrd.bitlib;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.mrd.bitlib.crypto.IPublicKeyRing;
import com.mrd.bitlib.crypto.PublicKey;
import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.BitcoinTransaction;
import com.mrd.bitlib.model.NetworkParameters;
import com.mrd.bitlib.model.Script.ScriptParsingException;
import com.mrd.bitlib.model.ScriptInput;
import com.mrd.bitlib.model.ScriptInputP2WPKH;
import com.mrd.bitlib.model.ScriptOutput;
import com.mrd.bitlib.model.ScriptOutputP2SH;
import com.mrd.bitlib.model.ScriptOutputP2WPKH;
import com.mrd.bitlib.model.TransactionInput;
import com.mrd.bitlib.model.TransactionOutput;
import com.mrd.bitlib.model.UnspentTransactionOutput;
import com.mrd.bitlib.util.BitUtils;
import com.mrd.bitlib.util.CoinUtil;
import com.mrd.bitlib.util.Sha256Hash;

public class UnsignedTransaction implements Serializable {
    public static final int NO_SEQUENCE = -1;
    private static final long serialVersionUID = 1L;
    private int defaultSequenceNumber;
    private UnspentTransactionOutput[] fundingOutputs;
    private TransactionInput[] inputs;
    private int lockTime;
    private NetworkParameters network;
    private TransactionOutput[] outputs;
    private SigningRequest[] signingRequests;

    public UnsignedTransaction(List<TransactionOutput> outputsIn, List<UnspentTransactionOutput> fundingIn,
            IPublicKeyRing keyRingIn, NetworkParameters networkIn, int lockTimeIn, int defaultSequenceNumberIn) {
        network = networkIn;
        outputs = outputsIn.toArray(new TransactionOutput[outputsIn.size()]);
        fundingOutputs = fundingIn.toArray(new UnspentTransactionOutput[fundingIn.size()]);
        signingRequests = new SigningRequest[fundingOutputs.length];
        lockTime = lockTimeIn;
        defaultSequenceNumber = defaultSequenceNumberIn;

        // Create empty input scripts pointing at the right out points
        inputs = new TransactionInput[fundingOutputs.length];
        for (int i = 0; i < fundingOutputs.length; i++) {
            inputs[i] = new TransactionInput(fundingOutputs[i].outPoint, ScriptInput.EMPTY, getDefaultSequenceNumber(),
                    fundingOutputs[i].value);
        }

        // Create transaction with valid outputs and empty inputs
        BitcoinTransaction bitcoinTransaction = new BitcoinTransaction(1, inputs, outputs, lockTime);

        for (int i = 0; i < fundingOutputs.length; i++) {
            if (isSegWitOutput(i)) {
                inputs[i].script = ScriptInput.fromOutputScript(fundingOutputs[i].script);
            }

            UnspentTransactionOutput utxo = fundingOutputs[i];

            // Make sure that we only work on standard output scripts
            if (!(utxo.script instanceof ScriptOutput)) {
                throw new RuntimeException("Unsupported script");
            }

            // Find the address of the funding
            BitcoinAddress address = utxo.script.getAddress(network);

            // Find the key to sign with
            PublicKey publicKey = keyRingIn.findPublicKeyByAddress(address);

            if (publicKey == null) {
                // This should not happen as we only work on outputs that we
                // have keys for
                throw new RuntimeException("Public key not found");
            }

            ScriptOutput scriptOutput = utxo.script;
            if (scriptOutput instanceof ScriptOutputP2SH) {
                getInputScript(publicKey, bitcoinTransaction, i, true);
            } else if (scriptOutput instanceof ScriptOutputP2WPKH) {
                getInputScript(publicKey, bitcoinTransaction, i, false);
            }

            // Set the input script to the funding output script
            List scriptsList = new ArrayList<ScriptInput>();
            if (!isSegWitOutput(i)) {
                for (TransactionInput transactionInput : inputs) {
                    ScriptInput scriptInput = transactionInput.script;
                    scriptsList.add(scriptInput);
                    transactionInput.script = ScriptInput.EMPTY;
                }
                inputs[i].script = ScriptInput.fromOutputScript(fundingOutputs[i].script);
            }

            // Calculate the transaction hash that has to be signed
            Sha256Hash txDigestHash = bitcoinTransaction.getTxDigestHash(i);

            // Set the input to the empty script again
            if (!isSegWitOutput(i)) {
                TransactionInput[] transactionInputArr2 = inputs;
                int length2 = transactionInputArr2.length;
                int i4 = 0;
                int i5 = 0;
                while (i4 < length2) {
                    transactionInputArr2[i4].script = (ScriptInput) scriptsList.get(i5);
                    i4++;
                    i5++;
                }
                inputs[i] = new TransactionInput(fundingOutputs[i].outPoint, ScriptInput.EMPTY, -1,
                        fundingOutputs[i].value);
            }

            signingRequests[i] = new SigningRequest(publicKey, txDigestHash);
        }
    }

    public final int getLockTime() {
        return lockTime;
    }

    public final int getDefaultSequenceNumber() {
        return defaultSequenceNumber;
    }

    public final TransactionOutput[] getOutputs() {
        return outputs;
    }

    public final UnspentTransactionOutput[] getFundingOutputs() {
        return fundingOutputs;
    }

    public final SigningRequest[] getSigningRequests() {
        return signingRequests;
    }

    public final TransactionInput[] getInputs() {
        return inputs;
    }

    private final void getInputScript(PublicKey publicKey, BitcoinTransaction bitcoinTransaction, int i, boolean z) {
        byte[] concatenate = BitUtils.concatenate(new byte[] { (byte) 0,
                (byte) publicKey.getPubKeyHashCompressed().length }, publicKey.getPubKeyHashCompressed());
        ScriptInput fromScriptBytes;
        try {
            fromScriptBytes = ScriptInput.fromScriptBytes(BitUtils.concatenate(
                    new byte[] { (byte) (concatenate.length & 255) }, concatenate));
            if (fromScriptBytes != null) {
                ((ScriptInputP2WPKH) fromScriptBytes).setNested(z);
                bitcoinTransaction.inputs[i].script = fromScriptBytes;
                this.inputs[i].script = fromScriptBytes;
                return;
            }
        } catch (ScriptParsingException e) {
            e.printStackTrace();
        }
    }

    // public final boolean isSegwit() {
    // // for (ScriptOutput isSegwitOutputScript :
    // // SequencesKt.map(ArraysKt.asSequence((T[]) this.fundingOutputs),
    // // UnsignedTransaction$isSegwit$1.INSTANCE)) {
    // // if (isSegwitOutputScript(isSegwitOutputScript)) {
    // // return true;
    // // }
    // // }
    // return false;
    // }

    private final boolean isSegwitOutputScript(ScriptOutput scriptOutput) {
        return (scriptOutput instanceof ScriptOutputP2WPKH) || (scriptOutput instanceof ScriptOutputP2SH);
    }

    public final boolean isSegWitOutput(int i) {
        ScriptOutput scriptOutput = this.fundingOutputs[i].script;
        return isSegwitOutputScript(scriptOutput);
    }

    /**
     * @return fee in satoshis
     */
    public long calculateFee() {
        long in = 0, out = 0;
        for (UnspentTransactionOutput funding : fundingOutputs) {
            in += funding.value;
        }
        for (TransactionOutput output : outputs) {
            out += output.value;
        }
        return in - out;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String fee = CoinUtil.valueString(calculateFee(), false);
        sb.append(String.format("Fee: %s", fee)).append('\n');
        return sb.toString();
    }

    public final SigningRequest[] getSignatureInfo() {
        return this.signingRequests;
    }

    private String getValue(Long value) {
        return String.format("(%s)", CoinUtil.valueString(value, false));
    }

}
