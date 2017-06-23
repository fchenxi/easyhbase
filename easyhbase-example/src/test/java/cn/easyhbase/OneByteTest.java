package cn.easyhbase;

import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

public class OneByteTest {

    private static final byte[][] PREFIXES;

    static {
        PREFIXES = new byte[Byte.MAX_VALUE][];
        for (byte i = 0; i < Byte.MAX_VALUE; i++) {
            PREFIXES[i] = new byte[] {i};
        }
    }
    @Test
    public void computeOneByteTest() {
        byte nextPrefix = 0;
        byte maxPrefix = 64;
        byte[] originalKey = new byte[]{0x09, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00,
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00};
        byte[] key = Bytes.add(PREFIXES[nextPrefix++], originalKey);
        nextPrefix = (byte) (nextPrefix % maxPrefix);

        System.out.println(Bytes.toStringBinary(key));
    }
}
