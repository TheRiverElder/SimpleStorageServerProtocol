package io.theriverelder.sssp.desktop;

import com.sun.net.httpserver.HttpServer;
import io.theriverelder.sssp.common.SimpleStorageServer;
import io.theriverelder.sssp.desktop.restful.InformationQueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;

public class DesktopSimpleStorageServer implements SimpleStorageServer {

    private Logger logger = LoggerFactory.getLogger(DesktopSimpleStorageServer.class);
    private HttpServer httpServer;
    private boolean disposed = true;


    @Override
    public void initialize(int port) throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(port), 0);
        this.httpServer.createContext("/", new InformationQueryHandler(this));
        this.httpServer.setExecutor(null);
    }

    @Override
    public void start() {
        httpServer.start();
        getLogger().info("Server started at port {}", getPort());
        disposed = true;
    }

    @Override
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

    @Override
    public int getPort() {
        return httpServer.getAddress().getPort();
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
