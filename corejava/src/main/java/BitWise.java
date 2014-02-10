/**
 * Illustrates bitwise operations
 *
 * @author Aditya Bhardwaj
 */
public class BitWise {

    public static void main(String[] args) {
        int i = 100;
        System.out.println( i << 1);

        int bitmask = 0x000F;
        int val = 0x2222;
        // prints "2"
        System.out.println(val & bitmask);
    }
}
