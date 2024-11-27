package com.snmp.generate;

import java.util.List;

import org.web3j.crypto.Credentials;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mrd.bitlib.crypto.Bip39;
import com.mrd.bitlib.crypto.BipDerivationType;
import com.mrd.bitlib.crypto.HdKeyNode;
import com.mrd.bitlib.crypto.InMemoryPrivateKey;
import com.mrd.bitlib.crypto.RandomSource;
import com.mrd.bitlib.model.AddressType;
import com.mrd.bitlib.model.BitcoinAddress;
import com.mrd.bitlib.model.NetworkParameters;
import com.mrd.bitlib.model.hdpath.HdKeyPath;
import com.mrd.bitlib.util.HexUtils;
import com.mycelium.wallet.AndroidRandomSource;
import com.mycelium.wapi.wallet.AesKeyCipher;
import com.mycelium.wapi.wallet.KeyCipher;
import com.snmp.crypto.CryptoFragment;
import com.snmp.crypto.R;

public class GenerateMain extends CryptoFragment {
    private static final String TAG = "generate";
    protected static final int BIP44_PRODNET_COIN_TYPE = 0x80000000;
    protected static final int BIP44_TESTNET_COIN_TYPE = 0x80000001;
    private RandomSource mRandomSource;
    private Bip39.MasterSeed mMasterSeed;
    private String mRawWords = "";
    private String mCryptoWords = "";
    private String mReplace = "long";
    private TextView mTxtBtcAddress;
    private TextView mTxtEthAddress;
    private TextView mTxtGenCount;
    private TextView mTxtMasterWords;

    public GenerateMain(Activity activity) {
        super(activity);
    }

    @Override
    public String getName() {
        return TAG;
    }

    @Override
    public int onGetLayoutId() {
        return R.layout.crypto_generate;
    }

    public void initView(View root) {
        mRandomSource = new AndroidRandomSource();

        mTxtBtcAddress = (TextView) root.findViewById(R.id.txt_btc_address);
        mTxtEthAddress = (TextView) root.findViewById(R.id.txt_eth_address);
        mTxtGenCount = (TextView) root.findViewById(R.id.txt_ges_count);
        mTxtMasterWords = (TextView) root.findViewById(R.id.txt_master_words);

        root.findViewById(R.id.btn_input_words).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                GenerateInputDialog.showInput(GenerateMain.this.getActivity());
            }
        });

        root.findViewById(R.id.gen_words_12).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                genWordList(12);
                getGapAddresses(AesKeyCipher.defaultKeyCipher());
            }
        });

        root.findViewById(R.id.gen_words_24).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                genWordList(24);
                getGapAddresses(AesKeyCipher.defaultKeyCipher());
            }
        });

        root.findViewById(R.id.copy_raw_words).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                copyRawWords();
            }
        });

        root.findViewById(R.id.copy_crypto_words).setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                copyCryptoWords();
            }
        });
    }

    private void copyCryptoWords() {
        CharSequence buildText = mCryptoWords;
        if (!TextUtils.isEmpty(buildText)) {
            ClipboardManager service = GenerateMain.this.getActivity().getSystemService(ClipboardManager.class);
            String label = "aaa";
            service.setPrimaryClip(ClipData.newPlainText(label, buildText));
            Toast.makeText(GenerateMain.this.getActivity(), "suscess copy crypto", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyRawWords() {
        CharSequence buildText = mRawWords;
        if (!TextUtils.isEmpty(buildText)) {
            ClipboardManager service = GenerateMain.this.getActivity().getSystemService(ClipboardManager.class);
            String label = "aaa";
            service.setPrimaryClip(ClipData.newPlainText(label, buildText));
            Toast.makeText(GenerateMain.this.getActivity(), "suscess copy raw", Toast.LENGTH_SHORT).show();
        }
    }

    private BitcoinAddress getGapAddresses(KeyCipher cipher) {
        // Set<Int> gaps = getGapsBug();
        // Get the master seed
        Bip39.MasterSeed masterSeed = mMasterSeed;
        // val tempSecureBacking = InMemoryBtcWalletManagerBacking()

        // val tempSecureKeyValueStore = SecureKeyValueStore(tempSecureBacking,
        // RandomSource {
        // randomness not needed for the temporary keystore
        // })

        // MutableList<BitcoinAddress> addresses = mutableListOf();
        // for (gapIndex in gaps.indices) {
        // for (derivationType: BipDerivationType in BipDerivationType.values())
        // {
        // Generate the root private key
        NetworkParameters network = NetworkParameters.productionNetwork;

        HdKeyNode root = HdKeyNode.fromSeed(masterSeed.getBip32Seed(), null);

        HdKeyNode bip44Root = root.createChildNode(HdKeyPath.valueOf("m/44'/0'/0'/0/0"));
        // HdKeyNode coinTypeRoot = bip44Root
        // .createChildNode(network.isProdnet() ? BIP44_PRODNET_COIN_TYPE
        // : BIP44_TESTNET_COIN_TYPE);

        // int accountIndex = 0;
        // Create the account root.
        // HdKeyNode accountRoot = coinTypeRoot
        // .createChildNode(accountIndex | 0x80000000);
        // HdKeyNode externalChainRoot = accountRoot.createChildNode(0);
        // HdKeyNode changeChainRoot = accountRoot.createChildNode(1);
        BitcoinAddress address = bip44Root.getPublicKey().toAddress(network, AddressType.P2PKH, false);
        // BitcoinAddress address2 = externalChainRoot.getPublicKey().toAddress(
        // network, AddressType.P2PKH, false);
        // BitcoinAddress address3 =
        // accountRoot.getPublicKey().toAddress(network,
        // AddressType.P2PKH, false);

        // HDAccountKeyManager keyManager = HDAccountKeyManager.createNew(root,
        // networkParameters, gapIndex, tempSecureKeyValueStore, cipher,
        // derivationType);
        // addresses.add(keyManager.getAddress(false, 0)); // get first external
        // address for the account in the gap
        // }
        // }
        HdKeyNode rootNode = HdKeyNode.fromSeed(masterSeed.getBip32Seed(), null);
        HdKeyPath path = HdKeyPath.valueOf("m/44'/60'/0'/0/0");
        HdKeyNode createChildNode = rootNode.createChildNode(path);
        InMemoryPrivateKey privateKey = createChildNode.getPrivateKey();
        Credentials ethCreate = Credentials.create(HexUtils.toHex(privateKey.getPrivateKeyBytes()));

        mTxtBtcAddress.setText(address.toString());
        mTxtEthAddress.setText(ethCreate.getAddress());

        android.util.Log.d("WindowManager", "aaaaa112 " + address + " "
        /* + ethCreate.getAddress() */);
        return address;
    }

    private void genWordList(int wordsLength) {
        List<String> wordlist = null;
        int count = 0;
        while (wordlist == null) {
            wordlist = getWordList(GenerateUtils.getPrivateWord(), wordsLength);
            count++;
        }

        android.util.Log.d("WindowManager", "find count=" + count);
        mTxtGenCount.setText("count: " + count);

        mRawWords = "";
        for (String s : wordlist) {
            mRawWords += s + " ";
            // android.util.Log.d("WindowManager", "aaaaa111 " + s + " ");
        }

        mCryptoWords = GenerateUtils
                .getCryptoShowWords(wordlist, GenerateUtils.getPrivateWord(), wordsLength, mReplace);
        mTxtMasterWords.setText(mCryptoWords);
    }

    private List<String> getWordList(String privateWord, int total) {
        int length = 128 * total / 12;
        Bip39.MasterSeed masterSeed = Bip39.createRandomMasterSeed(mRandomSource, length);
        // String words =
        // "whip motion boil maximum valid because receive network legend curtain pact grunt";
        // String[] wordList = words.split(" ");
        // Bip39.MasterSeed masterSeed =
        // Bip39.generateSeedFromWordList(wordList, "");
        List<String> wordlist = masterSeed.getBip39WordList();
        if (wordlist.contains(privateWord)) {
            mMasterSeed = masterSeed;
            return wordlist;
        }
        return null;
    }

}
