// Decompiled with: CFR 0.151
// Class Version: 8
package kg_charles_web_proxy_analyzer_v4_2;

class RC5 {
    public static final int w = 32;
    public static final int r = 12;
    public static final int b = 8;
    public static final int c = 2;
    public static final int t = 26;
    public int[] S = new int[26];
    public int P = -1209970333;
    public int Q = -1640531527;

    RC5() {
    }

    public byte[] RC5_DecryptArray(byte[] arrby) {
        byte[] arrby2 = new byte[arrby.length];
        int n = arrby.length;
        int n2 = 0;
        long l = 0L;
        int l1 = 0;
        int l2 = 0;
        for (int i = 0; i < n; ++i) {
            if (l < 4L) {
                l1 <<= 8;
                l1 |= arrby[i] & 0xFF;
            } else {
                l2 <<= 8;
                l2 |= arrby[i] & 0xFF;
            }
            ++l;
            if (++n2 != 8) continue;
            int[] ct = new int[]{l2, l1};
            int[] pt = new int[]{0, 0};
            this.RC5_DECRYPT(ct, pt);
            arrby2[i - 7] = (byte)(pt[1] >>> 24);
            arrby2[i - 6] = (byte)(pt[1] >>> 16);
            arrby2[i - 5] = (byte)(pt[1] >>> 8);
            arrby2[i - 4] = (byte)pt[1];
            arrby2[i - 3] = (byte)(pt[0] >>> 24);
            arrby2[i - 2] = (byte)(pt[0] >>> 16);
            arrby2[i - 1] = (byte)(pt[0] >>> 8);
            arrby2[i] = (byte)pt[0];
            n2 = 0;
            l = 0L;
            l1 = 0;
            l2 = 0;
        }
        return arrby2;
    }

    public byte[] RC5_EncryptArray(byte[] arrby) {
        byte[] arrby2 = new byte[arrby.length];
        int n = arrby.length;
        int n2 = 0;
        long l = 0L;
        int l1 = 0;
        int l2 = 0;
        for (int i = 0; i < n; ++i) {
            if (l < 4L) {
                l1 <<= 8;
                l1 |= arrby[i] & 0xFF;
            } else {
                l2 <<= 8;
                l2 |= arrby[i] & 0xFF;
            }
            ++l;
            if (++n2 != 8) continue;
            int[] pt = new int[]{l2, l1};
            int[] ct = new int[]{0, 0};
            this.RC5_ENCRYPT(pt, ct);
            arrby2[i - 7] = (byte)(ct[1] >>> 24);
            arrby2[i - 6] = (byte)(ct[1] >>> 16);
            arrby2[i - 5] = (byte)(ct[1] >>> 8);
            arrby2[i - 4] = (byte)ct[1];
            arrby2[i - 3] = (byte)(ct[0] >>> 24);
            arrby2[i - 2] = (byte)(ct[0] >>> 16);
            arrby2[i - 1] = (byte)(ct[0] >>> 8);
            arrby2[i] = (byte)ct[0];
            n2 = 0;
            l = 0L;
            l1 = 0;
            l2 = 0;
        }
        return arrby2;
    }

    void RC5_ENCRYPT(int[] pt, int[] ct) {
        int A = pt[0] + this.S[0];
        int B = pt[1] + this.S[1];
        for (int i = 1; i <= 12; ++i) {
            A = ((A ^ B) << (B & 0x1F) | (A ^ B) >>> 32 - (B & 0x1F)) + this.S[2 * i];
            B = ((B ^ A) << (A & 0x1F) | (B ^ A) >>> 32 - (A & 0x1F)) + this.S[2 * i + 1];
        }
        ct[0] = A;
        ct[1] = B;
    }

    void RC5_DECRYPT(int[] ct, int[] pt) {
        int B = ct[1];
        int A = ct[0];
        for (int i = 12; i > 0; --i) {
            B = (B - this.S[2 * i + 1] >>> (A & 0x1F) | B - this.S[2 * i + 1] << 32 - (A & 0x1F)) ^ A;
            A = (A - this.S[2 * i] >>> (B & 0x1F) | A - this.S[2 * i] << 32 - (B & 0x1F)) ^ B;
        }
        pt[1] = B - this.S[1];
        pt[0] = A - this.S[0];
    }

    void RC5_SETUP(int l, int h) {
        int i;
        int u = 4;
        int[] L = new int[]{l, h};
        this.S[0] = this.P;
        for (i = 1; i < 26; ++i) {
            this.S[i] = this.S[i - 1] + this.Q;
        }
        int j = 0;
        i = 0;
        int B = 0;
        int A = 0;
        for (int k = 0; k < 78; ++k) {
            A = this.S[i] = this.S[i] + (A + B) << 3 | this.S[i] + (A + B) >>> 29;
            B = L[j] = L[j] + (A + B) << (A + B & 0x1F) | L[j] + (A + B) >>> 32 - (A + B & 0x1F);
            i = (i + 1) % 26;
            j = (j + 1) % 2;
        }
        System.out.println();
    }

    public static String hex(int n) {
        return String.format("0x%s", Integer.toHexString(n)).replace(' ', '0');
    }
}