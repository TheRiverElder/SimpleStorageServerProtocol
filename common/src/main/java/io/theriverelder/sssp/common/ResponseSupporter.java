import java.io.InputStream;
import java.net.URI;

public interface ResponseSupporter {

    URI getUri();
    
    // 以下方法都将被依次调用

    // 此处的header不包括Content-Length
    void setResponseHeader(String name, String value);

    void setResponseStatus(int status);

    void setResponseBodyLength(long bodyLength);

    // 这个最后调用
    void sendResponseBody(InputStream inputStream);
}
