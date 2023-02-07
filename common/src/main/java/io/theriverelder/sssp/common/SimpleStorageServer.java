package io.theriverelder.sssp.common;

import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;

public interface SimpleStorageServer {

    void initialize(int port) throws IOException;

    void start();

    void stop();

    int getPort();

    @Nullable
    Logger getLogger();

    void setLogger(@Nullable Logger logger);
}
