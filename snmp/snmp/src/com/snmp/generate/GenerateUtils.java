package com.snmp.generate;

import java.util.List;

import android.text.TextUtils;

import com.snmp.utils.PreferenceManager;

public class GenerateUtils {

	public static String getCryptoShowWords(List<String> wordlist,
			String privateWord, int listLength, String replace) {
		String wordspre = "";
		for (int i = 0; i < listLength / 2; i++) {
			if (i % 6 == 0) {
				wordspre += "\n";
			}

			String s = wordlist.get(i);
			if (privateWord.equals(s)) {
				wordspre += replace + " ";
			} else {
				wordspre += s + " ";
			}
		}

		String wordslast = "";
		for (int i = listLength / 2; i < listLength; i++) {
			if (i % 6 == 0) {
				wordslast += "\n";
			}

			String s = wordlist.get(i);
			if (privateWord.equals(s)) {
				wordslast += replace + " ";
			} else {
				wordslast += s + " ";
			}
		}

		return wordslast + wordspre;
	}

	public static String getPrivateWord() {
		String privateWord = PreferenceManager
				.getString("gen_private_word", "");
		if (TextUtils.isEmpty(privateWord)) {
			return "this";
		}

		return privateWord;
	}

}
