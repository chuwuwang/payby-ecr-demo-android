package com.payby.pos.common.helper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class AmountHelper {

    public static long string2LongCent(String amount) {
        BigDecimal decimal = new BigDecimal(amount);
        BigDecimal bigDecimal = new BigDecimal("100");
        return decimal.multiply(bigDecimal).longValue();
    }

    public static String longCent2String(long amount) {
        BigDecimal decimal = new BigDecimal(amount);
        BigDecimal bigDecimal = new BigDecimal("100");
        double doubleValue = decimal.divide(bigDecimal).doubleValue();
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(Locale.US);
        DecimalFormat decimalFormat = new DecimalFormat("#0.00", symbols);
        return decimalFormat.format(doubleValue);
    }

    public static BigDecimal longCent2BigDecimal(long amount) {
        BigDecimal bigDecimal = new BigDecimal(100);
        return new BigDecimal(amount).divide(bigDecimal);
    }

    public static long bigDecimal2LongCent(BigDecimal amount) {
        BigDecimal bigDecimal = new BigDecimal("100");
        return amount.multiply(bigDecimal).longValue();
    }

}