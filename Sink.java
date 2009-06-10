import java.io.*;
import java.net.*;

public class Sink {
    int port;

    public Sink(int port) {
	this.port = port;
    }

    public void run() {
	try {
	    ServerSocket serverSock = new ServerSocket(port);

	    while (true) {
		Socket sock = serverSock.accept();
		new SinkThread(sock).start();
	    }
	} catch (IOException e) {
	    System.out.println(e);
	    System.exit(-1);
	}
    }

    public static void main(String[] args) {
	int port = 10000;

	for (int i = 0; i < args.length; i++) {
	    if ("-port".equals(args[i]))
		port = Integer.parseInt(args[++i]);
	}

	Sink sink = new Sink(port);
	sink.run();
    }
}

class SinkThread extends Thread {
    private Socket sock;

    public SinkThread(Socket sock) {
	this.sock = sock;
    }

    public void start() {
	try {
	    DataInputStream in = new DataInputStream(sock.getInputStream());
	    DataOutputStream out = new DataOutputStream(sock.getOutputStream());

	    int len = in.readInt();
	    int iter = in.readInt();
	    byte[] buf = new byte[len];

	    for (int i = 0; i < iter; i++)
		in.readFully(buf);

	    out.writeInt(0); /* sync */
	    buf = null;
	} catch (IOException e) {
	    System.out.println(e);
	    return;
	}
    }
}
