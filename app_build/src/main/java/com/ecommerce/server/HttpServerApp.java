package com.ecommerce.server;

import com.ecommerce.service.CarrinhoService;
import com.ecommerce.service.ClienteService;
import com.ecommerce.service.PedidoService;
import com.ecommerce.service.ProdutoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Executors;

public class HttpServerApp {
    private HttpServer server;
    private final int port;
    private final ProdutoService produtoService;
    private final ClienteService clienteService;
    private final CarrinhoService carrinhoService;
    private final PedidoService pedidoService;
    private Path webDir;

    public HttpServerApp(int port, ProdutoService produtoService, ClienteService clienteService,
                         CarrinhoService carrinhoService, PedidoService pedidoService) {
        this.port = port;
        this.produtoService = produtoService;
        this.clienteService = clienteService;
        this.carrinhoService = carrinhoService;
        this.pedidoService = pedidoService;

        // Localização da pasta web
        Path directPath = Paths.get("src/main/resources/web");
        Path buildPath = Paths.get("app_build/src/main/resources/web");
        if (Files.exists(directPath)) {
            this.webDir = directPath.toAbsolutePath();
        } else if (Files.exists(buildPath)) {
            this.webDir = buildPath.toAbsolutePath();
        } else {
            this.webDir = Paths.get("web").toAbsolutePath();
        }
    }

    public void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newVirtualThreadPerTaskExecutor() != null ?
                Executors.newVirtualThreadPerTaskExecutor() : Executors.newFixedThreadPool(10));

        // Contexto de API
        ApiHandler apiHandler = new ApiHandler(produtoService, clienteService, carrinhoService, pedidoService);
        server.createContext("/api", apiHandler);

        // Contexto de arquivos estáticos Web
        server.createContext("/", new StaticFileHandler());

        server.start();
        System.out.printf("🌐 Servidor HTTP ativo em: http://localhost:%d%n", port);
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    public int getPort() {
        return port;
    }

    private class StaticFileHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path.equals("/") || path.isEmpty()) {
                path = "/index.html";
            }

            // Normaliza para evitar directory traversal
            path = path.replace("..", "");

            Path filePath = webDir.resolve(path.substring(1));
            byte[] content = null;
            String contentType = "text/html; charset=UTF-8";

            if (Files.exists(filePath) && !Files.isDirectory(filePath)) {
                content = Files.readAllBytes(filePath);
            } else {
                // Tenta carregar do classpath
                try (InputStream is = getClass().getResourceAsStream("/web" + path)) {
                    if (is != null) {
                        content = is.readAllBytes();
                    }
                }
            }

            if (content == null) {
                // Fallback para index.html (SPA routing)
                Path indexPath = webDir.resolve("index.html");
                if (Files.exists(indexPath)) {
                    content = Files.readAllBytes(indexPath);
                } else {
                    try (InputStream is = getClass().getResourceAsStream("/web/index.html")) {
                        if (is != null) {
                            content = is.readAllBytes();
                        }
                    }
                }
            }

            if (content == null) {
                String notFound = "<h1>404 - Arquivo não encontrado</h1>";
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, notFound.length());
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes());
                }
                return;
            }

            if (path.endsWith(".html")) {
                contentType = "text/html; charset=UTF-8";
            } else if (path.endsWith(".js")) {
                contentType = "application/javascript; charset=UTF-8";
            } else if (path.endsWith(".css")) {
                contentType = "text/css; charset=UTF-8";
            } else if (path.endsWith(".json")) {
                contentType = "application/json; charset=UTF-8";
            } else if (path.endsWith(".svg")) {
                contentType = "image/svg+xml";
            } else if (path.endsWith(".png")) {
                contentType = "image/png";
            } else if (path.endsWith(".jpg") || path.endsWith(".jpeg")) {
                contentType = "image/jpeg";
            }

            exchange.getResponseHeaders().set("Content-Type", contentType);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.sendResponseHeaders(200, content.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(content);
            }
        }
    }
}
