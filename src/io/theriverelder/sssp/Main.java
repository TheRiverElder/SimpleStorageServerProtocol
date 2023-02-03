package io.theriverelder.sssp;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 8888;
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if ("--port".equals(arg) || "-p".equals(arg) && i + 1 < args.length) {
                String portString = args[i + 1];
                port = Integer.parseInt(portString);
            }
        }
        SimpleStorageServer server = new SimpleStorageServer(port);
        server.start();
    }
}
