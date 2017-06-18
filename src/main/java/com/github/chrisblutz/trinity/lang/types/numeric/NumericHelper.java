package com.github.chrisblutz.trinity.lang.types.numeric;

import com.github.chrisblutz.trinity.lang.errors.Errors;


/**
 * @author Christopher Lutz
 */
public class NumericHelper {
    
    public static void checkDivision(int numerator, int denominator) {
        
        if (denominator == 0) {
            
            throwDivideByZeroError();
        }
    }
    
    public static void checkDivision(long numerator, long denominator) {
        
        if (denominator == 0) {
            
            throwDivideByZeroError();
        }
    }
    
    public static void checkDivision(double numerator, double denominator) {
        
        if (denominator == 0) {
            
            throwDivideByZeroError();
        }
    }
    
    public static void throwDivideByZeroError() {
        
        Errors.throwError("Trinity.Errors.ArithmeticError", "/ by 0.");
    }
}
