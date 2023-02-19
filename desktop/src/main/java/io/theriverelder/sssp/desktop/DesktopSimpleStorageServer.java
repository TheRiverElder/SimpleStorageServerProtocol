package io.theriverelder.sssp.desktop;

import com.sun.net.httpserver.HttpServer;
import io.theriverelder.sssp.common.HttpResponseHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.slf4j.helpers.NOPLogger.NOP_LOGGER;

public class DesktopSimpleStorageServer {

    @Nullable
    private Logger logger = LoggerFactory.getLogger(this.getClass().getSimpleName());
    private HttpServer httpServer;
    private boolean disposed = true;

//    private ExecutorService executorService;


    public void initialize(int port) throws IOException {
//        this.executorService = Executors.newFixedThreadPool(16);

        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        this.httpServer.createContext("/", exchange -> {
            getLogger().info("+++++++ Connection IN: {}", exchange.getRemoteAddress().toString());
            HttpResponseHelper.process(new HttpExchangeResponseSupporter(this, exchange));
            exchange.close();
            getLogger().info("------- Connection OUT: {}", exchange.getRemoteAddress().toString());
        });
        this.httpServer.setExecutor(null);
    }
//
//    public ExecutorService getExecutorService() {
//        return executorService;
//    }

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
