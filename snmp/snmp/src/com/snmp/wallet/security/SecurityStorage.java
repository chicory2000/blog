package com.snmp.wallet.security;

import org.web3j.crypto.Credentials;

import com.google.common.base.Optional;
import com.mrd.bitlib.crypto.Bip39;
import com.mrd.bitlib.crypto.BitcoinSigner;
import com.mrd.bitlib.crypto.HdKeyNode;
import com.mrd.bitlib.crypto.HdKeyNode.KeyGenerationException;
import com.mrd.bitlib.crypto.InMemoryPrivateKey;
import com.mrd.bitlib.crypto.PublicKey;
import com.mrd.bitlib.model.AddressType;
import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.NetworkParameters;
import com.mrd.bitlib.model.hdpath.HdKeyPath;
import com.mrd.bitlib.util.HexUtils;
import com.mycelium.wallet.AndroidRandomSource;
import com.mycelium.wapi.wallet.AesKeyCipher;
import com.mycelium.wapi.wallet.KeyCipher;
import com.mycelium.wapi.wallet.KeyCipher.InvalidKeyCipher;
import com.mycelium.wapi.wallet.SecureKeyValueStore;
import com.snmp.utils.Bech32;
import com.snmp.utils.Bech32Prefix;
import com.snmp.utils.LogUtils;
import com.snmp.utils.PreferenceManager;
import com.snmp.utils.Utils;
import com.snmp.wallet.Constant;

public class SecurityStorage {
    private static final String TAG = SecurityStorage.class.getSimpleName();
    private static final byte[] MASTER_SEED_ID = HexUtils.toBytes("D64CA2B680D8C8909A367F28EB47F990");
    private final KeyCipher mKeyCipher = AesKeyCipher.defaultKeyCipher();
    private SecureKeyValueStore mSecureKeyValueStore;
    private HdKeyNode mNodeRoot;
    private HdKeyNode mNodeMain;
    private String mMainPath = "";
    private String mMainAddress = "";
    private String mTestReceiveAddr = "";

    public SecurityStorage() {
        SecurityPreference backing = new SecurityPreference();
        mSecureKeyValueStore = new SecureKeyValueStore(backing, new AndroidRandomSource());
        initStorage();
    }

    public void configureBip32MasterSeed(Bip39.MasterSeed masterSeed) throws InvalidKeyCipher {
        mSecureKeyValueStore.encryptAndStoreValue(MASTER_SEED_ID, masterSeed.toBytes(false), mKeyCipher);
        initStorage();
    }

    public void reinitWallet() {
        initStorage();
    }

    private void initStorage() {
        try {
            byte[] decryptedValue = mSecureKeyValueStore.getDecryptedValue(MASTER_SEED_ID, mKeyCipher);
            if (decryptedValue == null) {
                LogUtils.e(TAG, "initStorage null");
                return;
            }
            Optional<Bip39.MasterSeed> fromBytes = Bip39.MasterSeed.fromBytes(decryptedValue, false);

            if (fromBytes.isPresent()) {
                Bip39.MasterSeed masterSeed = fromBytes.get();
                initAddresses(masterSeed);
            } else {
                LogUtils.e(TAG, "initStorage isPresent");
            }
        } catch (InvalidKeyCipher e) {
            e.printStackTrace();
        }
    }

    private void initAddresses(Bip39.MasterSeed masterSeed) {
        mNodeRoot = HdKeyNode.fromSeed(masterSeed.getBip32Seed(), null);

        initMainAddress(mNodeRoot);

        initSubAddress(mNodeRoot);
        getEthAddress(mNodeRoot);
    }

    private void initMainAddress(HdKeyNode root) {
        mMainPath = PreferenceManager.getString("wallet_path", "m/44'/0'/0'/0/0");
        mNodeMain = root.createChildNode(HdKeyPath.valueOf(mMainPath));

        if (isEthPath(mMainPath)) {
            mMainAddress = getEthAddress(mNodeRoot);
        } else {
            BitcoinAddress address = mNodeMain.getPublicKey().toAddress(getNetwork(), getAddressType(mMainPath), false);
            mMainAddress = address.toString();
        }

        LogUtils.d(TAG, "mMainAddress =" + mMainAddress);
    }

    private String getEthAddress(HdKeyNode root) {
        String path = "m/44'/60'/0'/0/0";
        HdKeyNode createChildNode = root.createChildNode(HdKeyPath.valueOf(path));
        InMemoryPrivateKey privateKey = createChildNode.getPrivateKey();
        Credentials ethCreate = Credentials.create(HexUtils.toHex(privateKey.getPrivateKeyBytes()));
        return ethCreate.getAddress();
    }

    private void initSubAddress(HdKeyNode root) {
        String path = "m/44'/0'/0'/0/1";
        HdKeyNode nodesub = root.createChildNode(HdKeyPath.valueOf(path));
        BitcoinAddress address = nodesub.getPublicKey().toAddress(getNetwork(), getAddressType(path), false);
        mTestReceiveAddr = address.toString();
    }

    private NetworkParameters getNetwork() {
        return Constant.getNetworkParameters();
    }

    private boolean isEthPath(String path) {
        return path.contains("m/44'/60'");
    }

    private AddressType getAddressType(String path) {
        if (path.contains("m/49")) {
            return AddressType.P2SH_P2WPKH;
        } else if (path.contains("m/84")) {
            return AddressType.P2WPKH;
        }
        return AddressType.P2PKH;
    }

    public String getPath() {
        return mMainPath;
    }

    public String getTestRecvAddr() {
        return mTestReceiveAddr;
    }

    public String getAddressByPath(String path) {
        HdKeyNode node = mNodeRoot.createChildNode(HdKeyPath.valueOf(path));
        if (isEthPath(path)) {
            return getEthAddress(mNodeRoot);
        }
        BitcoinAddress address = node.getPublicKey().toAddress(getNetwork(), getAddressType(path), false);
        return address.toString();
    }

    public String getAddress() {
        return mMainAddress;
    }

    public PublicKey getPublicKey() {
        return mNodeMain.getPublicKey();
    }

    // snmp security
    public BitcoinSigner getPrivateKey() {
        return mNodeMain.getPrivateKey();
    }

    public String getPrivateKeyNostrHex() {
        String hex = HexUtils.toHex(mNodeMain.getPrivateKey().getPrivateKeyBytes());
        try {
            hex = Bech32.toBech32(Bech32Prefix.NSEC, mNodeMain.getPrivateKey().getPrivateKeyBytes());
        } catch (Exception e) {
            LogUtils.e(TAG, "getPrivateKeyHex =" + e);
            e.printStackTrace();
        }
        hex = Utils.swapEnd(hex);
        return hex;
    }

    // snmp security
    public String getBase58EncodedPrivateKey() {
        String key = mNodeMain.getPrivateKey().getBase58EncodedPrivateKey(getNetwork());
        key = Utils.swapEnd(key);
        return key;
    }
}
