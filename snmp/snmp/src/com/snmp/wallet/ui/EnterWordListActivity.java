package com.snmp.wallet.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.mrd.bitlib.crypto.Bip39;
import com.snmp.crypto.R;
import com.snmp.utils.PreferenceManager;
import com.snmp.wallet.BtcAccount;

public final class EnterWordListActivity extends Activity {
    private static final String TAG = EnterWordListActivity.class.getSimpleName();
    private static final String ONLY_SEED = "onlySeed";
    public static final String MASTERSEED = "masterseed";
    public static final String PASSWORD = "password";
    private TextView enterWordInfo;
    private List<String> enteredWords = new ArrayList<String>();;
    private boolean usesPassphrase;
    private int numberOfWords = 12;
    private int currentWordNum;

    private String _currentWord = "";
    private List<Button> _completionButtons;
    private String[] _completions;
    private int _minimumCharacters = 2;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView((int) R.layout.crypto_enter_word_list_activity);
        initView();
        currentWordNum = 1;
        setCompletions(Bip39.ENGLISH_WORD_LIST);
        if (bundle == null) {
            // only ask if we are not recreating the activity, because of
            // rotation for example
            askForWordNumber();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("usepass", usesPassphrase);
        savedInstanceState.putInt("index", currentWordNum);
        savedInstanceState.putInt("total", numberOfWords);
        savedInstanceState.putStringArray("entered", enteredWords.toArray(new String[enteredWords.size()]));
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        enteredWords = new ArrayList<String>(Arrays.asList(savedInstanceState.getStringArray("entered")));
        enterWordInfo.setText(enteredWords.toString());
        usesPassphrase = savedInstanceState.getBoolean("usepass");
        numberOfWords = savedInstanceState.getInt("total");
        currentWordNum = savedInstanceState.getInt("index");
        findViewById(R.id.btDeleteLastWord).setEnabled(currentWordNum > 1);
        if (currentWordNum < numberOfWords) {
            setHint();
        } else if (!checksumMatches()) {
            findViewById(R.id.tvChecksumWarning).setVisibility(View.VISIBLE);
            setHint(false);
        }
    }

    private void initView() {
        enterWordInfo = (TextView) findViewById(R.id.tvEnterWord);
        findViewById(R.id.btDeleteLastWord).setOnClickListener(deleteListener);

        View root = findViewById(R.id.llKeyBoard);
        // Hook up top row
        HookUpLetterKey(root, R.id.btQ);
        HookUpLetterKey(root, R.id.btW);
        HookUpLetterKey(root, R.id.btE);
        HookUpLetterKey(root, R.id.btR);
        HookUpLetterKey(root, R.id.btT);
        HookUpLetterKey(root, R.id.btY);
        HookUpLetterKey(root, R.id.btU);
        HookUpLetterKey(root, R.id.btI);
        HookUpLetterKey(root, R.id.btO);
        HookUpLetterKey(root, R.id.btP);

        // Hook up middle row
        HookUpLetterKey(root, R.id.btA);
        HookUpLetterKey(root, R.id.btS);
        HookUpLetterKey(root, R.id.btD);
        HookUpLetterKey(root, R.id.btF);
        HookUpLetterKey(root, R.id.btG);
        HookUpLetterKey(root, R.id.btH);
        HookUpLetterKey(root, R.id.btJ);
        HookUpLetterKey(root, R.id.btK);
        HookUpLetterKey(root, R.id.btL);

        // Hook up bottom row
        HookUpLetterKey(root, R.id.btZ);
        HookUpLetterKey(root, R.id.btX);
        HookUpLetterKey(root, R.id.btC);
        HookUpLetterKey(root, R.id.btV);
        HookUpLetterKey(root, R.id.btB);
        HookUpLetterKey(root, R.id.btN);
        HookUpLetterKey(root, R.id.btM);
        HookUpDelKey(root, R.id.btDel);

        _completionButtons = new ArrayList<Button>();
        _completionButtons.add((Button) root.findViewById(R.id.btSuggestion1));
        _completionButtons.add((Button) root.findViewById(R.id.btSuggestion2));
        _completionButtons.add((Button) root.findViewById(R.id.btSuggestion3));

        for (Button b : _completionButtons) {
            b.setOnClickListener(completionClickListener);
        }

    }

    private void HookUpLetterKey(View root, int buttonId) {
        Button b = (Button) root.findViewById(buttonId);
        b.setOnClickListener(aToZClickListener);
    }

    private void HookUpDelKey(View root, int buttonId) {
        Button b = (Button) root.findViewById(buttonId);
        b.setOnClickListener(delClickListener);
    }

    View.OnClickListener completionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            acceptWord(((Button) view).getText().toString());
        }
    };

    OnClickListener aToZClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            Button b = (Button) view;
            onCharacterKeyClicked(b.getText().toString().toLowerCase(Locale.US).charAt(0));
        }
    };

    OnClickListener delClickListener = new OnClickListener() {
        @Override
        public void onClick(View view) {
            onDelClicked();
        }
    };

    public void onCharacterKeyClicked(char character) {
        setCurrentWord(_currentWord + character);
        showCompletionButtons();
    }

    public void onDelClicked() {
        if (_currentWord.length() > 0) {
            setCurrentWord(_currentWord.substring(0, _currentWord.length() - 1));
            showCompletionButtons();
        }
    }

    private void acceptWord(String word) {
        onWordSelected(word);

        // prepare for next word
        _currentWord = "";
        setCurrentWord(_currentWord);
        // hide buttons
        showCompletionButtons();
    }

    private void setCurrentWord(String word) {
        _currentWord = word;
        onCurrentWordChanged(word);

        if (exactMatch(word)) {
            // exact match
            acceptWord(_currentWord);

        }
    }

    private boolean exactMatch(String entered) {
        // check if the word matches one entry in the wordlist exactly
        if (Arrays.asList(_completions).contains(entered)) {
            // check if there is no other word starting with the same letters
            // (eg. "sea" / "seat")
            for (String w : _completions) {
                if (!w.equals(entered) && w.startsWith(entered)) {
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }

    }

    public void setCompletions(String[] completions) {
        _completions = completions.clone();
        showCompletionButtons();
    }

    public void setMinimumCompletionCharacters(int minimumCharacters) {
        _minimumCharacters = minimumCharacters;
    }

    private void showCompletionButtons() {
        // Make the first button invisible, the rest disappear
        boolean first = true;
        for (Button b : _completionButtons) {
            if (first) {
                first = false;
                b.setVisibility(View.INVISIBLE);
            } else {
                b.setVisibility(View.GONE);
            }
            b.setText("");
        }

        List<String> completions = determineCompletions(_currentWord, _completionButtons.size());

        for (int i = 0; i < completions.size(); i++) {
            Button b = _completionButtons.get(i);
            String word = completions.get(i);
            b.setVisibility(View.VISIBLE);
            b.setText(word);
        }

    }

    private List<String> determineCompletions(String partialWord, int maxCompletions) {
        List<String> completions = new ArrayList<String>(maxCompletions);
        if (_completions == null || partialWord.length() < _minimumCharacters) {
            return completions;
        }
        for (String s : _completions) {
            if (s.startsWith(partialWord)) {
                completions.add(s);
                if (completions.size() == maxCompletions) {
                    break;
                }
            }
        }
        return completions;
    }

    private View.OnClickListener deleteListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            enteredWords.remove(enteredWords.size() - 1);
            --currentWordNum;
            setHint();
            enterWordInfo.setText(enteredWords.toString());
            findViewById(R.id.tvChecksumWarning).setVisibility(View.GONE);
            if (currentWordNum == 1) {
                findViewById(R.id.btDeleteLastWord).setEnabled(false);
            }
        }
    };

    void setHint() {
        ((TextView) findViewById(R.id.tvHint)).setText(getString(R.string.importing_wordlist_enter_next_word,
                Integer.toString(currentWordNum), Integer.toString(numberOfWords)));
        findViewById(R.id.tvHint).setVisibility(View.VISIBLE);
    }

    void setHint(boolean show) {
        if (show) {
            setHint();
        } else {
            findViewById(R.id.tvHint).setVisibility(View.INVISIBLE);
        }
    }

    private void askForWordNumber() {
        final View checkBoxView = View.inflate(this, R.layout.crypto_wordlist_checkboxes, null);
        final CheckBox checkBox = (CheckBox) checkBoxView.findViewById(R.id.checkboxWordlistPassphrase);
        final RadioButton words12 = (RadioButton) checkBoxView.findViewById(R.id.wordlist12);
        final RadioButton words18 = (RadioButton) checkBoxView.findViewById(R.id.wordlist18);
        final RadioButton words24 = (RadioButton) checkBoxView.findViewById(R.id.wordlist24);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                checkBoxView.findViewById(R.id.tvPassphraseInfo).setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
        builder.setTitle(R.string.import_words_title);
        builder.setMessage(R.string.import_wordlist_questions).setView(checkBoxView).setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        usesPassphrase = checkBox.isChecked();
                        if (words12.isChecked()) {
                            numberOfWords = 12;
                        } else if (words18.isChecked()) {
                            numberOfWords = 18;
                        } else if (words24.isChecked()) {
                            numberOfWords = 24;
                        } else {
                            throw new IllegalStateException("No radiobutton selected in word list import");
                        }
                        setHint();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // use test wordlist
                        usesPassphrase = checkBox.isChecked();
                        askForPassphrase();
                    }
                }).show();
    }

    private boolean checkIfDone() {
        if (currentWordNum < numberOfWords) {
            currentWordNum++;
            setHint();
            return false;
        }
        if (checksumMatches()) {
            return true;
        } else {
            findViewById(R.id.tvChecksumWarning).setVisibility(View.VISIBLE);
            setHint(false);
            currentWordNum++; // needed for the delete button to function
                              // correctly
            return false;
        }
    }

    private boolean checksumMatches() {
        return Bip39.isValidWordList(enteredWords.toArray(new String[enteredWords.size()]));
    }

    public void onCurrentWordChanged(String currentWord) {
        ((TextView) findViewById(R.id.tvWord)).setText(currentWord);
    }

    public void onWordSelected(String word) {
        addWordToList(word);
    }

    private void askForPassphrase() {
        if (usesPassphrase) {
            View view = LayoutInflater.from(this).inflate(R.layout.crypto_wallet_password, null);
            final EditText pass = (EditText) view.findViewById(R.id.et_password);

            AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.Dialog);
            builder.setTitle(R.string.type_password_title);
            builder.setView(view).setCancelable(false)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            String passphrase = pass.getText().toString();
                            String prepass = PreferenceManager.getString("wallet_passphrase_pre", "");
                            done(prepass + passphrase);
                            PreferenceManager.putString("wallet_passphrase", passphrase);
                        }
                    }).show();
        } else {
            done("");
        }
    }

    private void addWordToList(String selection) {
        enteredWords.add(selection);
        enterWordInfo.setText(enteredWords.toString());
        if (checkIfDone()) {
            askForPassphrase();
        } else {
            findViewById(R.id.btDeleteLastWord).setEnabled(true);
        }
    }

    private void done(String passphrase) {
        BtcAccount.getInstance().generateNewWallet((enteredWords.toArray(new String[enteredWords.size()])), passphrase);
        // snmp security
        enteredWords.clear();
        finish();
    }
}
