package io.theriverelder.sssp.desktop;

import com.sun.net.httpserver.HttpExchange;
import io.theriverelder.sssp.common.ResponseSupporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class HttpExchangeResponseSupporter implements ResponseSupporter {


    private final DesktopSimpleStorageServer server;
    private final HttpExchange exchange;
    private int responseStatusCode;
    private long responseContentLength;

    public HttpExchangeResponseSupporter(DesktopSimpleStorageServer server, HttpExchange exchange) {
        this.server = server;
        this.exchange = exchange;
    }


    @Override
    public void schedule(Runnable runnable) throws Exception {
//        server.getExecutorService().submit(runnable);
        runnable.run(); // 救不了，用线程池直接寄，不如直接运行，至少还能处理一个请求
    }

    @Override
    public URI getRequestUri() {
        return exchange.getRequestURI();
    }

    @Override
    public InputStream getRequestBody() {
        return exchange.getRequestBody();
    }

    @Override
    public long getRequestBodyLength() {
        return -1L;
    }

    @Override
    public void setResponseHeader(String name, String value) {
        exchange.getResponseHeaders().set(name, value);
    }

    @Override
    public void setResponseStatus(int status) {
        responseStatusCode = status;
    }

    @Override
    public void setResponseBodyLength(long bodyLength) {
        responseContentLength = bodyLength;
    }

    @Override
    public boolean sendResponseBody(InputStream inputStream) throws IOException {
        exchange.sendResponseHeaders(responseStatusCode, responseContentLength);
        try (OutputStream outputStream = exchange.getResponseBody()) {
            byte[] buffer = new byte[8192];
            int read;
            while ((read = inputStream.read(buffer, 0, 8192)) >= 0) {
                outputStream.write(buffer, 0, read);
            }
        }
        return true;
    }
}
