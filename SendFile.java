/*
 * SendFile.java
 * Usage: java SendFile [-port <port>] filename address
 */
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;

public class SendFile {
    public static void main(String[] args) {
	int port = 10000;

	int i;
	for (i = 0; i < args.length; i++) {
	    if ("-port".equals(args[i]))
		port = Integer.parseInt(args[++i]);
	    else
		break;
	}
	String filename = args[i++];
	String addr = args[i++];

	try {
	    File file = new File(filename);
	    FileInputStream src = new FileInputStream(file);

	    try {
		FileChannel in = src.getChannel();

		SocketChannel out = SocketChannel.open();
		out.connect(new InetSocketAddress(InetAddress.getByName(addr),
						  port));
		DataOutputStream dos = new DataOutputStream(out.socket().getOutputStream());
		long len = in.size();
		dos.writeLong(len);
		dos.writeUTF(file.getName());

		long start = System.currentTimeMillis();

		in.transferTo(0, len,  out);

		long end = System.currentTimeMillis();
		long elapse = end - start;
		double bw = ((double)len * 1000.0d) / (double)elapse;
		System.out.println(len + " " + bw + " Bytes/sec ("
				   + elapse + " msec)");

	    } catch (IOException e) {
		e.printStackTrace();
	    }
	} catch (FileNotFoundException e) {
	    e.printStackTrace();
	}
    }
}

