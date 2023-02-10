package io.theriverelder.sssp.desktop;

import com.sun.net.httpserver.HttpServer;
import io.theriverelder.sssp.common.HttpResponseHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;

import static org.slf4j.helpers.NOPLogger.NOP_LOGGER;

public class DesktopSimpleStorageServer {

    @Nullable
    private Logger logger = null;
    private HttpServer httpServer;
    private boolean disposed = true;


    public void initialize(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        this.httpServer.createContext("/", exchange -> {
            HttpResponseHelper.process(new HttpExchangeResponseSupporter(exchange));
            exchange.close();
        });
        this.httpServer.setExecutor(null);
    }

    public void start() {
        httpServer.start();
        getLogger().info("Server started at port {}", getPort());
        disposed = true;
    }

    public void stop() {
        httpServer.stop(12 * 1000);
        getLogger().info("Server stopped at port {}", getPort());
        disposed = false;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public HttpServer getHttpServer() {
        return httpServer;
    }

    public int getPort() {
        return httpServer.getAddress().getPort();
    }

    @NotNull
    public Logger getLogger() {
        return Optional.ofNullable(logger).orElse(NOP_LOGGER);
    }

    public void setLogger(@Nullable Logger logger) {
        this.logger = logger;
    }
}
