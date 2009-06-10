/*
 * Bulk.java
 * Usage: java Bulk [-port <port>] [-len <len1,len2,...>] [-iter <iter>] address
 */
import java.io.*;
import java.net.*;

public class Bulk {
    String addr;
    int port;

    public Bulk(String addr, int port) {
	this.addr = addr;
	this.port = port;
    }

    public long run(int len, int iter) throws IOException {
	byte[] buf = new byte[len];

	for (int i = 0; i < len; i++) {
	    buf[i] = (byte)i;
	}

	try {
	    Socket sock = new Socket(InetAddress.getByName(addr), port);
	    DataInputStream in = new DataInputStream(sock.getInputStream());
	    DataOutputStream out = new DataOutputStream(sock.getOutputStream());
	    try {
		out.writeInt(len);
		out.writeInt(iter);

		long start = System.currentTimeMillis();

		for (int i = 0; i < iter; i++)
		    out.write(buf, 0, buf.length);

		in.readInt(); /* sync */
		out.flush();

		long end = System.currentTimeMillis();

		return end - start;
	    } finally {
		out.close();
		in.close();
		sock.close();
	    }
	} catch (IOException e) {
	    throw e;
	}
    }

    public static void main(String[] args) {
	int port = 10000;
	String[] lens = {};
	int iter = 0;
	long elapse;
	String addr = "localhost";
	boolean isest = false;

	for (int i = 0; i < args.length; i++) {
	    if ("-port".equals(args[i])) {
		port = Integer.parseInt(args[++i]);
	    } else if ("-len".equals(args[i])) {
		lens = args[++i].split(",", -1);
	    } else if ("-iter".equals(args[i])) {
		iter = Integer.parseInt(args[++i]);
	    } else {
		addr = args[i];
	    }
	}

	try {
	    Bulk bulk = new Bulk(addr, port);

	    if (iter == 0)
		isest = true;

	    for (String l : lens) {
		int len = Integer.parseInt(l);

		if (isest) {
		    /* iter is roughly estimated at 10 seconds. */
		    elapse = bulk.run(len, 100);
		    iter = (int)Math.ceil(1000000d / (double)elapse);
		}

		elapse = bulk.run(len, iter);
		double bw = ((double)len * (double)iter * 1000.0d) / (double)elapse;
		System.out.println(len + " " + iter + " " + bw + " Bytes/sec ("
				   + elapse + " msec)");
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}