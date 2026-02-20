package task4;

public class Divide{
    public int divide(int a, int b) {
        return a/b;
    }

    public double improvedDivide(double a, double b){
        if (b == 0) {
            throw new ArithmeticException("Cannot divide by zero");
        } else {
            return a/b;
        }
    }
}