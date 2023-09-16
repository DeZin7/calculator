package com.dezin.calculator;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class CalculatorTest {
    
    
    @Test 
    public void testSum() {
        assertEquals(5, calculator.sum(2, 3));
    }
}