package com.ecommerce.server;

import com.ecommerce.model.*;
import com.ecommerce.service.CarrinhoService;
import com.ecommerce.service.ClienteService;
import com.ecommerce.service.PedidoService;
import com.ecommerce.service.ProdutoService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ApiHandler implements HttpHandler {
    private final ProdutoService produtoService;
    private final ClienteService clienteService;
    private final CarrinhoService carrinhoService;
    private final PedidoService pedidoService;

    public ApiHandler(ProdutoService produtoService, ClienteService clienteService,
                      CarrinhoService carrinhoService, PedidoService pedidoService) {
        this.produtoService = produtoService;
        this.clienteService = clienteService;
        this.carrinhoService = carrinhoService;
        this.pedidoService = pedidoService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        URI uri = exchange.getRequestURI();
        String path = uri.getPath();

        // Tratamento de Preflight CORS (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            sendCors(exchange);
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        sendCors(exchange);

        try {
            Map<String, String> queryParams = parseQueryParams(uri.getQuery());

            if (path.startsWith("/api/produtos")) {
                tratarProdutos(exchange, method, path, queryParams);
            } else if (path.startsWith("/api/categorias")) {
                tratarCategorias(exchange);
            } else if (path.startsWith("/api/clientes/login")) {
                tratarLogin(exchange, method);
            } else if (path.startsWith("/api/clientes")) {
                tratarClientes(exchange, method);
            } else if (path.startsWith("/api/carrinho/itens")) {
                tratarItensCarrinho(exchange, method, queryParams);
            } else if (path.startsWith("/api/carrinho")) {
                tratarCarrinho(exchange, method, queryParams);
            } else if (path.startsWith("/api/pedidos")) {
                tratarPedidos(exchange, method, queryParams);
            } else {
                sendJsonResponse(exchange, 404, "{\"erro\":\"Endpoint não encontrado\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendJsonResponse(exchange, 500, "{\"erro\":\"" + JsonUtils.escape(e.getMessage()) + "\"}");
        }
    }

    private void tratarProdutos(HttpExchange exchange, String method, String path, Map<String, String> queryParams) throws IOException {
        if (!"GET".equalsIgnoreCase(method)) {
            sendJsonResponse(exchange, 405, "{\"erro\":\"Método não permitido\"}");
            return;
        }

        String subPath = path.substring("/api/produtos".length());
        if (subPath.startsWith("/") && subPath.length() > 1) {
            String idStr = subPath.substring(1);
            try {
                Long id = Long.parseLong(idStr);
                Optional<Produto> opt = produtoService.buscarPorId(id);
                if (opt.isPresent()) {
                    sendJsonResponse(exchange, 200, JsonUtils.produtoToJson(opt.get()));
                } else {
                    sendJsonResponse(exchange, 404, "{\"erro\":\"Produto não encontrado\"}");
                }
            } catch (NumberFormatException e) {
                sendJsonResponse(exchange, 400, "{\"erro\":\"ID de produto inválido\"}");
            }
            return;
        }

        String termo = queryParams.get("q");
        String categoria = queryParams.get("categoria");
        List<Produto> lista = produtoService.buscar(termo, categoria);
        sendJsonResponse(exchange, 200, JsonUtils.produtosToJson(lista));
    }

    private void tratarCategorias(HttpExchange exchange) throws IOException {
        StringBuilder sb = new StringBuilder("[");
        Categoria[] cats = Categoria.values();
        for (int i = 0; i < cats.length; i++) {
            if (i > 0) sb.append(",");
            sb.append(String.format("{\"id\":\"%s\",\"nome\":\"%s\"}", cats[i].name(), JsonUtils.escape(cats[i].getDescricao())));
        }
        sb.append("]");
        sendJsonResponse(exchange, 200, sb.toString());
    }

    private void tratarClientes(HttpExchange exchange, String method) throws IOException {
        if ("GET".equalsIgnoreCase(method)) {
            List<Cliente> clientes = clienteService.listarTodos();
            sendJsonResponse(exchange, 200, JsonUtils.clientesToJson(clientes));
        } else if ("POST".equalsIgnoreCase(method)) {
            String body = readRequestBody(exchange);
            Map<String, String> dados = JsonUtils.parseSimpleJson(body);
            String nome = dados.get("nome");
            String email = dados.get("email");
            String telefone = dados.get("telefone");
            String endereco = dados.get("endereco");

            if (nome == null || email == null) {
                sendJsonResponse(exchange, 400, "{\"erro\":\"Nome e e-mail são obrigatórios\"}");
                return;
            }
            Cliente cliente = clienteService.cadastrar(nome, email, telefone, endereco);
            sendJsonResponse(exchange, 201, JsonUtils.clienteToJson(cliente));
        } else {
            sendJsonResponse(exchange, 405, "{\"erro\":\"Método não permitido\"}");
        }
    }

    private void tratarLogin(HttpExchange exchange, String method) throws IOException {
        if (!"POST".equalsIgnoreCase(method)) {
            sendJsonResponse(exchange, 405, "{\"erro\":\"Método não permitido\"}");
            return;
        }
        String body = readRequestBody(exchange);
        Map<String, String> dados = JsonUtils.parseSimpleJson(body);
        String email = dados.get("email");
        if (email == null) {
            sendJsonResponse(exchange, 400, "{\"erro\":\"E-mail obrigatório para login\"}");
            return;
        }

        Optional<Cliente> opt = clienteService.login(email);
        if (opt.isPresent()) {
            sendJsonResponse(exchange, 200, JsonUtils.clienteToJson(opt.get()));
        } else {
            sendJsonResponse(exchange, 404, "{\"erro\":\"Cliente não encontrado com este e-mail\"}");
        }
    }

    private void tratarCarrinho(HttpExchange exchange, String method, Map<String, String> queryParams) throws IOException {
        if (!"GET".equalsIgnoreCase(method)) {
            sendJsonResponse(exchange, 405, "{\"erro\":\"Método não permitido\"}");
            return;
        }

        Long clienteId = parseLong(queryParams.get("clienteId"), 1L);
        List<ItemCarrinho> itens = carrinhoService.obterCarrinho(clienteId);
        double total = carrinhoService.calcularTotal(clienteId);
        int quantidadeTotal = carrinhoService.obterQuantidadeTotalItens(clienteId);

        sendJsonResponse(exchange, 200, JsonUtils.carrinhoToJson(itens, total, quantidadeTotal));
    }

    private void tratarItensCarrinho(HttpExchange exchange, String method, Map<String, String> queryParams) throws IOException {
        if ("POST".equalsIgnoreCase(method)) {
            String body = readRequestBody(exchange);
            Map<String, String> dados = JsonUtils.parseSimpleJson(body);
            Long clienteId = parseLong(dados.get("clienteId"), 1L);
            Long produtoId = parseLong(dados.get("produtoId"), null);
            int quantidade = parseInt(dados.get("quantidade"), 1);

            if (produtoId == null) {
                sendJsonResponse(exchange, 400, "{\"erro\":\"produtoId é obrigatório\"}");
                return;
            }

            boolean sucesso = carrinhoService.adicionarProduto(clienteId, produtoId, quantidade);
            if (sucesso) {
                tratarCarrinho(exchange, "GET", Map.of("clienteId", String.valueOf(clienteId)));
            } else {
                sendJsonResponse(exchange, 400, "{\"erro\":\"Estoque insuficiente para adicionar esta quantidade\"}");
            }
        } else if ("PUT".equalsIgnoreCase(method)) {
            String body = readRequestBody(exchange);
            Map<String, String> dados = JsonUtils.parseSimpleJson(body);
            Long clienteId = parseLong(dados.get("clienteId"), 1L);
            Long produtoId = parseLong(dados.get("produtoId"), null);
            int quantidade = parseInt(dados.get("quantidade"), 0);

            if (produtoId == null) {
                sendJsonResponse(exchange, 400, "{\"erro\":\"produtoId é obrigatório\"}");
                return;
            }

            boolean sucesso = carrinhoService.atualizarQuantidade(clienteId, produtoId, quantidade);
            if (sucesso) {
                tratarCarrinho(exchange, "GET", Map.of("clienteId", String.valueOf(clienteId)));
            } else {
                sendJsonResponse(exchange, 400, "{\"erro\":\"Estoque insuficiente ou item inválido\"}");
            }
        } else if ("DELETE".equalsIgnoreCase(method)) {
            String body = readRequestBody(exchange);
            Map<String, String> dados = JsonUtils.parseSimpleJson(body);
            Long clienteId = parseLong(dados.get("clienteId"), parseLong(queryParams.get("clienteId"), 1L));
            Long produtoId = parseLong(dados.get("produtoId"), parseLong(queryParams.get("produtoId"), null));

            if (produtoId == null) {
                sendJsonResponse(exchange, 400, "{\"erro\":\"produtoId é obrigatório para remoção\"}");
                return;
            }

            carrinhoService.removerProduto(clienteId, produtoId);
            tratarCarrinho(exchange, "GET", Map.of("clienteId", String.valueOf(clienteId)));
        } else {
            sendJsonResponse(exchange, 405, "{\"erro\":\"Método não permitido\"}");
        }
    }

    private void tratarPedidos(HttpExchange exchange, String method, Map<String, String> queryParams) throws IOException {
        if ("POST".equalsIgnoreCase(method)) {
            String body = readRequestBody(exchange);
            Map<String, String> dados = JsonUtils.parseSimpleJson(body);
            Long clienteId = parseLong(dados.get("clienteId"), 1L);
            String endereco = dados.get("enderecoEntrega");
            String pagamento = dados.get("metodoPagamento");

            Optional<Cliente> optCliente = clienteService.buscarPorId(clienteId);
            if (optCliente.isEmpty()) {
                sendJsonResponse(exchange, 404, "{\"erro\":\"Cliente não encontrado\"}");
                return;
            }

            try {
                Pedido pedido = pedidoService.criarPedido(optCliente.get(), endereco, pagamento);
                sendJsonResponse(exchange, 201, JsonUtils.pedidoToJson(pedido));
            } catch (IllegalStateException e) {
                sendJsonResponse(exchange, 400, "{\"erro\":\"" + JsonUtils.escape(e.getMessage()) + "\"}");
            }
        } else if ("GET".equalsIgnoreCase(method)) {
            String clienteIdStr = queryParams.get("clienteId");
            if (clienteIdStr != null) {
                Long clienteId = parseLong(clienteIdStr, 1L);
                List<Pedido> pedidos = pedidoService.listarPedidosPorCliente(clienteId);
                sendJsonResponse(exchange, 200, JsonUtils.pedidosToJson(pedidos));
            } else {
                List<Pedido> todos = pedidoService.listarTodos();
                sendJsonResponse(exchange, 200, JsonUtils.pedidosToJson(todos));
            }
        } else {
            sendJsonResponse(exchange, 405, "{\"erro\":\"Método não permitido\"}");
        }
    }

    private void sendCors(HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type, Authorization");
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, String jsonResponse) throws IOException {
        byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody()) {
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private Map<String, String> parseQueryParams(String query) {
        Map<String, String> params = new HashMap<>();
        if (query == null || query.trim().isEmpty()) return params;
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8),
                           URLDecoder.decode(kv[1], StandardCharsets.UTF_8));
            } else if (kv.length == 1) {
                params.put(URLDecoder.decode(kv[0], StandardCharsets.UTF_8), "");
            }
        }
        return params;
    }

    private Long parseLong(String val, Long defaultVal) {
        if (val == null) return defaultVal;
        try {
            return Long.parseLong(val.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }

    private int parseInt(String val, int defaultVal) {
        if (val == null) return defaultVal;
        try {
            return Integer.parseInt(val.trim());
        } catch (NumberFormatException e) {
            return defaultVal;
        }
    }
}
