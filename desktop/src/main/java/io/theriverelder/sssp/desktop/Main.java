package io.theriverelder.sssp.desktop;

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
        DesktopSimpleStorageServer server = new DesktopSimpleStorageServer();
        server.initialize(port);
        server.start();
    }
}
