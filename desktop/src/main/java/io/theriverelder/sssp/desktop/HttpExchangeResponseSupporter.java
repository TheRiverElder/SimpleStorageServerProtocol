package io.theriverelder.sssp.desktop;

import com.sun.net.httpserver.HttpExchange;
import io.theriverelder.sssp.common.ResponseSupporter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;

public class HttpExchangeResponseSupporter implements ResponseSupporter {

    private final HttpExchange exchange;
    private int responseStatusCode;
    private long responseContentLength;

    public HttpExchangeResponseSupporter(HttpExchange exchange) {
        this.exchange = exchange;
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
        try (inputStream; OutputStream outputStream = exchange.getResponseBody()) {
            inputStream.transferTo(outputStream);
        }
        return true;
    }
}
