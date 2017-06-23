package cn.easyhbase;

import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.util.Arrays;

public class RangeHashTest {

    private static final byte[][] PREFIXES;

    static {
        PREFIXES = new byte[256][];
        for (int i = 0; i < 256; i++) {
            PREFIXES[i] = new byte[]{(byte) i};
        }
    }

    /**
     * Compute hash for binary data.
     */
    @Test
    public void computeHashBytesTest() {

        int start = 0;
        int end = 33;
        int mod = 64;
        byte[] bytes = new byte[]{0x01, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        int min = Math.min(bytes.length, end);
        int hash = 1;
        for (int i = start; i < min; i++) {
            hash = (31 * hash) + (int) bytes[i];
            System.out.println("hash: " + hash);
        }
        System.out.println("hash: " + hash % mod);
        System.out.println("hash % mod: " + hash % mod);

    }

    @Test
    public void getAllPossiblePrefixesTest() {
        int mod = 64;
        Arrays.copyOfRange(PREFIXES, 0, mod);
        for (int i = 0; i < 256; i++) {
            System.out.println(Bytes.toStringBinary(PREFIXES[i]));
        }
    }
}
