package net.wzero.wewallet.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {
	
	public static BigDecimal toBigDecimal(double value) {
		return new BigDecimal(Double.toString(value));
	}
	
	public static BigDecimal toBigDecimal(String value) {
		return new BigDecimal(value);
	}
	
	public static double add(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));  
        BigDecimal b2 = new BigDecimal(Double.toString(value2));  
        return b1.add(b2).doubleValue();
    }
      
    public static double sub(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));  
        BigDecimal b2 = new BigDecimal(Double.toString(value2));  
        return b1.subtract(b2).doubleValue();
    }
      
    public static double mul(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(Double.toString(value1));  
        BigDecimal b2 = new BigDecimal(Double.toString(value2));  
        return b1.multiply(b2).doubleValue();
    }
      
    public static double div(double value1, double value2, int scale) throws IllegalAccessException {  
        if(scale < 0) throw new IllegalArgumentException("精确度不能小于0");  
        else if(value2 == 0) throw new IllegalArgumentException("除数不能为0");  
        BigDecimal b1 = new BigDecimal(Double.toString(value1));  
        BigDecimal b2 = new BigDecimal(Double.toString(value2));  
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();      
    }
    
    public static double add(double value1, double value2, int scale) {  
        return round(add(value1, value2), scale);
    }
      
    public static double sub(double value1, double value2, int scale) {  
        return round(sub(value1, value2), scale);
    }
      
    public static double mul(double value1, double value2, int scale) {  
        return round(mul(value1, value2), scale);
    }
    
    public static double round(double v, int scale) {    
        if (scale < 0) throw new IllegalArgumentException("精确度不能小于0");    
        BigDecimal b = new BigDecimal(Double.toString(v));    
        BigDecimal one = new BigDecimal("1");    
        return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
    }
    
}
