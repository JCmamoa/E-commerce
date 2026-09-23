package com.ecommerce;

import com.ecommerce.cli.EcommerceCLI;
import com.ecommerce.server.HttpServerApp;
import com.ecommerce.service.CarrinhoService;
import com.ecommerce.service.ClienteService;
import com.ecommerce.service.PedidoService;
import com.ecommerce.service.ProdutoService;

public class Main {
    public static void main(String[] args) {
        System.out.println("Inicializando Sistema de E-Commerce...");

        int porta = 8080;
        if (args.length > 0) {
            try {
                porta = Integer.parseInt(args[0]);
            } catch (NumberFormatException ignored) {}
        }

        // 1. Inicialização dos Serviços em Memória
        ProdutoService produtoService = new ProdutoService();
        ClienteService clienteService = new ClienteService();
        CarrinhoService carrinhoService = new CarrinhoService(produtoService);
        PedidoService pedidoService = new PedidoService(produtoService, carrinhoService, clienteService);

        // 2. Inicialização do Servidor HTTP / Web SPA
        HttpServerApp httpServer = new HttpServerApp(porta, produtoService, clienteService, carrinhoService, pedidoService);
        try {
            httpServer.start();
        } catch (Exception e) {
            System.err.println("Aviso: Não foi possível iniciar o servidor HTTP na porta " + porta + ": " + e.getMessage());
            try {
                porta = 8081;
                httpServer = new HttpServerApp(porta, produtoService, clienteService, carrinhoService, pedidoService);
                httpServer.start();
            } catch (Exception ex) {
                System.err.println("Erro ao tentar porta alternativa: " + ex.getMessage());
            }
        }

        // 3. Inicialização da Interface CLI Interativa
        EcommerceCLI cli = new EcommerceCLI(produtoService, clienteService, carrinhoService, pedidoService, porta);
        cli.iniciar();

        // Finalização graciosa
        httpServer.stop();
        System.exit(0);
    }
}
