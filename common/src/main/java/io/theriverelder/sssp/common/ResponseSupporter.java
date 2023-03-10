package io.theriverelder.sssp.common;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

public interface ResponseSupporter {

    // 让实现自己选择处理任务的方式
    // 例如有的实现使用的框架无法处理多并发，需要给每个请求分配独立的线程，或使用线程池
    // 而有的实现使用的框架自带并发，无序自己设置线程池，直接运行即可
    void schedule(Runnable runnable) throws Exception;

    URI getRequestUri();

    // 这个InputStream不再自动关闭，请手动关闭
    InputStream getRequestBody();

    // 返回-1代表RequestBody能读多少，算多少
    long getRequestBodyLength();

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
