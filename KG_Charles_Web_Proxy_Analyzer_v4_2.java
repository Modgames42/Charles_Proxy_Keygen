// Decompiled with: CFR 0.151
// Class Version: 8
package kg_charles_web_proxy_analyzer_v4_2;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import kg_charles_web_proxy_analyzer_v4_2.RC5;

public class KG_Charles_Web_Proxy_Analyzer_v4_2
extends JFrame
implements ActionListener,
DocumentListener {
    private JTextField tfName;
    private JTextField tfSerial;
    private JButton buGenerate;
    private JButton buQuit;
    private JLabel labelCR;
    private static final String strTitle = "Charles Web Debugging Proxy";

    public static void main(String[] args) {
        KG_Charles_Web_Proxy_Analyzer_v4_2 kg = new KG_Charles_Web_Proxy_Analyzer_v4_2();
        kg.setVisible(true);
    }

    public KG_Charles_Web_Proxy_Analyzer_v4_2() {
        this.setSize(650, 150);
        this.setTitle(strTitle);
        this.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width / 2 - this.getSize().width / 2, dim.height / 2 - this.getSize().height / 2);
        this.tfName = new JTextField("TEAM MESMERiZE");
        this.tfName.setBounds(10, 10, 300, 30);
        this.tfName.getDocument().addDocumentListener(this);
        this.tfSerial = new JTextField();
        this.tfSerial.setSize(300, 40);
        this.tfSerial.setBounds(10, 50, 300, 30);
        this.buGenerate = new JButton("Generate");
        this.buGenerate.setBounds(320, 10, 120, 30);
        this.buGenerate.setActionCommand("generate");
        this.buGenerate.addActionListener(this);
        this.buQuit = new JButton("Quit");
        this.buQuit.setBounds(320, 50, 120, 30);
        this.buQuit.setActionCommand("quit");
        this.buQuit.addActionListener(this);
        this.labelCR = new JLabel("(c) TEAM MESMERiZE 2018");
        this.labelCR.setBounds(470, 0, 180, 100);
        this.setLayout(null);
        this.getContentPane().add(this.tfName);
        this.getContentPane().add(this.tfSerial);
        this.getContentPane().add(this.buGenerate);
        this.getContentPane().add(this.buQuit);
        this.getContentPane().add(this.labelCR);
        this.setDefaultCloseOperation(3);
        this.calculateSerial(this.tfName.getText());
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte)((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public String calculateSerial(String name) {
        int serialCheckSum = 1418211210;
        int nameCheckSum = this.calcNameChecksum(name);
        long serial = serialCheckSum ^= nameCheckSum;
        serial <<= 32;
        serial >>>= 32;
        serial <<= 32;
        int serialLow = (int)((serial |= 0x1CAD6BCL) & 0xFFFFFFFFFFFFFFFFL);
        int serialHigh = (int)(serial >>> 32 & 0xFFFFFFFFFFFFFFFFL);
        int[] serialEnc = new int[]{serialLow, serialHigh};
        int[] serialDec = new int[2];
        RC5 serialDecrypter = new RC5();
        serialDecrypter.RC5_SETUP(-334581843, -1259282228);
        serialDecrypter.RC5_DECRYPT(serialEnc, serialDec);
        long serialDecrypted = ((long)serialDec[1] & 0xFFFFFFFFL) << 32;
        int xorCheckSum = this.calcXorChecksum(serial);
        String strSerial = Integer.toHexString(xorCheckSum) + Long.toHexString(serialDecrypted |= (long)serialDec[0] & 0xFFFFFFFFL);
        strSerial = String.format("%02X", xorCheckSum) + String.format("%016X", serialDecrypted);
        this.tfSerial.setText(strSerial);
        return strSerial;
    }

    private final int calcXorChecksum(long l) {
        long l2 = 0L;
        for (int i = 56; i >= 0; i -= 8) {
            l2 ^= l >>> i & 0xFFL;
        }
        return Math.abs((int)(l2 & 0xFFL));
    }

    public int calcNameChecksum(String strName) {
        byte[] byteArrayName = null;
        try {
            byteArrayName = strName.getBytes("UTF-8");
        }
        catch (UnsupportedEncodingException ex) {
            System.out.println(ex.toString());
        }
        int nameLength = byteArrayName.length;
        int n = nameLength + 4;
        if (n % 8 != 0) {
            n += 8 - n % 8;
        }
        byte[] arrby4 = new byte[n];
        System.arraycopy(byteArrayName, 0, arrby4, 4, nameLength);
        arrby4[0] = (byte)(nameLength >> 24);
        arrby4[1] = (byte)(nameLength >> 16);
        arrby4[2] = (byte)(nameLength >> 8);
        arrby4[3] = (byte)nameLength;
        RC5 r = new RC5();
        r.RC5_SETUP(1763497072, 2049034577);
        byte[] outputArray = r.RC5_EncryptArray(arrby4);
        int n3 = 0;
        for (byte by : outputArray) {
            n3 ^= by;
            n3 = n3 << 3 | n3 >>> 29;
        }
        return n3;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("quit")) {
            System.exit(0);
        } else if (e.getActionCommand().equals("generate")) {
            this.calculateSerial(this.tfName.getText());
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.calculateSerial(this.tfName.getText());
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.calculateSerial(this.tfName.getText());
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        this.calculateSerial(this.tfName.getText());
    }
}