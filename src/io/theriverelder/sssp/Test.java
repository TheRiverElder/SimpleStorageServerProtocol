package io.theriverelder.sssp;

import java.io.IOException;

public class Test {
    public static void main(String[] args) throws IOException {
//        String queryPath = URLEncoder.encode("C:/User/Desktop", StandardCharsets.UTF_8);
////        String queryPath = "C:/User/Desktop";
//        System.out.println("queryPath: " + queryPath);
////        URI uri = new URI("http", null, "localhost", 8888, "/information", "relative&path=" + queryPath, null);
//        URI uri = new URI("http://localhost:8888/C:/User/Desktop?action=remove");
//        System.out.println("uri: " + uri);
//        System.out.println("uri.path: " + uri.getPath());
//        System.out.println("uri.query: " + InformationQueryHandler.parseUriQuery(uri.getRawQuery()));

        SimpleStorageServer server = new SimpleStorageServer(8888);
        server.start();
    }
}
