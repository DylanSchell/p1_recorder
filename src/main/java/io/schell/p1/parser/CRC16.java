package io.schell.p1.parser;

/**
 * Created by dylan on 7/5/2016.
 */
public class CRC16 {
    // Generator polynom codes:
    public static final int stdPoly    = 0xA001; // standard CRC-16 x16+x15+x2+1 (CRC-16-IBM)
    public static final int stdRPoly   = 0xC002; // standard reverse x16+x14+x+1 (CRC-16-IBM)
    public static final int ccittPoly  = 0x8408; // CCITT/SDLC/HDLC X16+X12+X5+1 (CRC-16-CCITT)
    // The initial CRC value is usually 0xFFFF and the result is complemented.
    public static final int ccittRPoly = 0x8810; // CCITT reverse X16+X11+X4+1   (CRC-16-CCITT)
    public static final int lrcPoly    = 0x8000; // LRCC-16 X16+1

    public static int slowCrc16(byte[] data, int initialValue, int polynom) {
        int crc = initialValue;
        for (byte aData : data) {
            crc ^= (aData & 0xFF);
            for (int i = 0; i < 8; i++) {
                if ((crc & 1) != 0) {
                    crc = (crc >> 1) ^ polynom;
                } else {
                    crc = crc >> 1;
                }
            }
        }
        return crc;
    }

}
