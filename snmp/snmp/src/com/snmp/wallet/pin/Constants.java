package com.snmp.wallet.pin;

public class Constants {
    public static final String FAILED_PIN_COUNT = "failedPinCount";
    public static final String PIN_SETTING = "PIN";
    public static final String PIN_SETTING_RESETTABLE = "PinResettable";
    public static final String RANDOMIZE_PIN = "randomizePin";
    public static final String PIN_SETTING_REQUIRED_ON_STARTUP = "PinOnStartup";
    public static final int BITCOIN_BLOCKS_PER_DAY = (24 * 60) / 10;
    public static final int BTC_BLOCK_TIME_IN_SECONDS = 600;

    // Minimum age of the PIN in blocks, so that we allow a second wordlist
    // backup
    public static final int MIN_PIN_BLOCKHEIGHT_AGE_ADDITIONAL_BACKUP = 2 * BITCOIN_BLOCKS_PER_DAY;

    // Minimum age of the PIN in blocks, until you can reset the PIN
    public static final int MIN_PIN_BLOCKHEIGHT_AGE_RESET_PIN = 7 * BITCOIN_BLOCKS_PER_DAY;
    public static final int MIN_PIN_AGE_RESET_PIN = 7 * 24 * 60 * 60 * 1000;
}
