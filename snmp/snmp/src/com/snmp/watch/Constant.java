package com.snmp.watch;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class Constant {
	public static final long SATOSHIS_PER_BITCOIN = 100000000L;
	public static final int SECOND_1 = 1000;

	private static DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols() {
		private static final long serialVersionUID = 1L;
		{
			setDecimalSeparator('.');
		}
	};

	public static DecimalFormat decimalFormat = new DecimalFormat("#.########",
			otherSymbols);

}
