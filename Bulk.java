import java.io.*;
import java.net.*;

public class Bulk {
    public static void main(String[] args) {

	try {
	    InetAddress sink = InetAddress.getByName(args[0]);
	    int len = Integer.parseInt(args[1]);
	    int iter = Integer.parseInt(args[2]);
	    byte[] buf = new byte[len];

	    for (int i = 0; i < len; i++) {
		buf[i] = (byte)i;
	    }

	    Socket sock = new Socket(sink, 10000);
	    DataInputStream in = new DataInputStream(sock.getInputStream());
	    DataOutputStream out = new DataOutputStream(sock.getOutputStream());

	    out.writeInt(len);
	    out.writeInt(iter);

	    long start = System.currentTimeMillis();

	    for (int i = 0; i < iter; i++) {
		out.write(buf, 0, buf.length);
		out.flush();
	    }
	    in.readInt(); /* sync */
	    sock.close();

	    long end = System.currentTimeMillis();

	    double bw = ((double)len * (double)iter * 1000.0) / (double)(end - start);
	    System.out.println(len + " " + bw + " Bytes/sec (" + (end - start) + " msec)");
	} catch (IOException e) {
	    System.out.println(e);
	    System.exit(-1);
	}
    }
}