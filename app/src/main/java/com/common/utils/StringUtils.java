package com.common.utils;

/*数据处理*/
public class StringUtils {

    public static String getStringDouble(double distance) {
        if (distance > 1000) {
            distance = distance / 1000;
        }
//        new java.text.DecimalFormat("0.00");
//        String .format("%.2f");
//        double f = 111231.5585;
//    BigDecimal b = new BigDecimal(f);
//    //保留2位小数
//    double f1 = b.setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue();
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
        return df.format(distance);

    }
}
