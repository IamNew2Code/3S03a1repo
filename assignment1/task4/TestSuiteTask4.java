package task4;
import static org.junit.Assert.*;
import org.junit.Test;

public class TestSuiteTask4 {

    @Test
    public void dividePositiveNumbers(){
        Divide divide = new Divide();
        assertEquals(23, divide.improvedDivide(69,3),0.001);
    }

    @Test
    public void divideNegativeNumerator(){
        Divide divide = new Divide();
        assertEquals(-7, divide.improvedDivide(-42,6),0.001);
    }

    @Test
    public void divideNegativeDenominator(){
        Divide divide = new Divide();
        assertEquals(-5, divide.improvedDivide(25,-5),0.001);
    }

    @Test
    public void divideBothNegative(){
        Divide divide = new Divide();
        assertEquals(4, divide.improvedDivide(-16,-4),0.001);
    }

    // Added units test to improve test suite (task 4d)
    @Test
    public void divideByZero(){
        Divide divide = new Divide();
        assertThrows(ArithmeticException.class, () -> divide.improvedDivide(5,0));
    }

    //The third argument in asserEquals is the delta value to account for precision issues when dealing with double values.
    @Test
    public void divideNumeratorDecimal(){
        Divide divide = new Divide();
        assertEquals(1.1, divide.improvedDivide(7.7,7),0.0001);
    }

    @Test
    public void divideDenominatorDecimal(){
        Divide divide = new Divide();
        assertEquals(20, divide.improvedDivide(2,0.1),0.0001);
    }

    @Test
    public void divideBothDecimal(){
        Divide divide = new Divide();
        assertEquals(14, divide.improvedDivide(4.2,0.3),0.0001);
    }

    @Test
    public void divideResultInDecimal(){
        Divide divide = new Divide();
        assertEquals(0.3333, divide.improvedDivide(1,3),0.0001);
    }

}
