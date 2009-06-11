/*
 * SendFile.java
 */
import java.io.*;
import java.nio.*;
import java.nio.channels.*;
import java.net.*;

public class RecvFile {
    public static void main(String[] args) {
	int port = 10000;

	for (int i = 0; i < args.length; i++) {
	    if ("-port".equals(args[i]))
		port = Integer.parseInt(args[++i]);
	}

	try {
	    ServerSocketChannel serverChan = ServerSocketChannel.open();
	    serverChan.socket().bind(new InetSocketAddress(port));

	    while (true) {
		SocketChannel chan = serverChan.accept();
		DataInputStream dis = new DataInputStream(chan.socket().getInputStream());
		long len = dis.readLong();
		String fname = dis.readUTF();

		FileOutputStream dst = new FileOutputStream(new File(fname));
		FileChannel out = dst.getChannel();

		out.transferFrom(chan, 0, len);
	    }

	} catch (IOException e) {
	    e.printStackTrace();
	}
    }
}
