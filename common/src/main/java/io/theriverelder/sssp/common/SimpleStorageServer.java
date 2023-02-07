package io.theriverelder.sssp.common;

public interface SimpleStorageServer {
    void initialize(int port);
    void start();
    void stop();
    int getPort();
}
