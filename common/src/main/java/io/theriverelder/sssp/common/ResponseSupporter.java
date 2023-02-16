package io.theriverelder.sssp.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface ResponseSupporter {

    URI getRequestUri();

    // 这个InputStream不再自动关闭，请手动关闭
    InputStream getRequestBody();

    // 以下方法都将被依次调用

    // 此处的header不包括Content-Length
    void setResponseHeader(String name, String value);

    void setResponseStatus(int status);

    void setResponseBodyLength(long bodyLength);

    // 这个最后调用
    // 返回true表示可用由HttpResponseHelper.process()关闭inputStream
    // 返回false表示不要关闭，但是请ResponseSupporter务必在后续过程中自己关闭inputStream！
    boolean sendResponseBody(InputStream inputStream) throws IOException;
}
