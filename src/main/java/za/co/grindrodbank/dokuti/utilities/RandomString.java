package za.co.grindrodbank.dokuti.utilities;

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

public class RandomString {

    /**
     * Generate a random string.
     */
    public String nextString() {
        for (int idx = 0; idx < buf.length; ++idx)
            buf[idx] = symbols[random.nextInt(symbols.length)];
        return new String(buf);
    }


    public static final String alphabet = "abcdefghijklmnopqrstuvwxyz";
    public static final String abc = "abcdefghijklmnopqrstuvwxyz" + "abcdefghijklmnopqrstuvwxyz".toUpperCase() +"0123456789";

    private final Random random;

    private final char[] symbols;

    private final char[] buf;
    
    public RandomString(int length, Random random) {
        if (length < 1) throw new IllegalArgumentException();
        if (abc.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = abc.toCharArray();
        this.buf = new char[length];
    }


    /**
     * Create an alphanumeric strings from a secure generator.
     */
    public RandomString(int length) {
        this(length, new SecureRandom());
    }

    /**
     * Create session identifiers.
     */
    public RandomString() {
        this(21);
    }

}