package com.snmp.wallet;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

import com.mrd.bitlib.model.NetworkParameters;
import com.snmp.utils.PreferenceManager;

public class Constant {
    public static boolean IS_PRODUCTION = PreferenceManager.getBoolean("snmp_crypto_production", true);

    public static final long SATOSHIS_PER_BITCOIN = 100000000L;
    public static final int SECOND_1 = 1000;

    private static DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols() {
        private static final long serialVersionUID = 1L;
        {
            setDecimalSeparator('.');
        }
    };
    public static DecimalFormat decimalFormat = new DecimalFormat("#.########", otherSymbols);

    public static String getInitTestWords() {
        String words = "theme achieve boost diary veteran roast access prevent this aunt lobster hair";
        if (Constant.IS_PRODUCTION) {
            words = "chase robust nest kiwi access banana nerve excite dirt net novel teach";
        }
        return words;
    }

    public static NetworkParameters getNetworkParameters() {
        if (Constant.IS_PRODUCTION) {
            return NetworkParameters.productionNetwork;
        } else {
            return NetworkParameters.testNetwork;
        }
    }
}
