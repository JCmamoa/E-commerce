package com.ecommerce.cli;

import com.ecommerce.model.*;
import com.ecommerce.service.CarrinhoService;
import com.ecommerce.service.ClienteService;
import com.ecommerce.service.PedidoService;
import com.ecommerce.service.ProdutoService;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

import static com.ecommerce.cli.ConsoleColors.*;

public class EcommerceCLI {
    private final ProdutoService produtoService;
    private final ClienteService clienteService;
    private final CarrinhoService carrinhoService;
    private final PedidoService pedidoService;
    private final Scanner scanner;
    private Cliente clienteLogado;
    private final int webPort;

    public EcommerceCLI(ProdutoService produtoService, ClienteService clienteService,
                        CarrinhoService carrinhoService, PedidoService pedidoService, int webPort) {
        this.produtoService = produtoService;
        this.clienteService = clienteService;
        this.carrinhoService = carrinhoService;
        this.pedidoService = pedidoService;
        this.webPort = webPort;
        this.scanner = new Scanner(System.in);

        // Cliente padrão inicial para demonstração
        List<Cliente> clientes = clienteService.listarTodos();
        if (!clientes.isEmpty()) {
            this.clienteLogado = clientes.get(0);
        }
    }

    public void iniciar() {
        boolean executando = true;
        while (executando) {
            exibirCabecalho();
            exibirMenuPrincipal();
            System.out.print(colorir("👉 Escolha uma opção: ", BOLD, CYAN));
            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1" -> listarCatalogo();
                case "2" -> buscarProdutos();
                case "3" -> verDetalhesProduto();
                case "4" -> gerenciarCarrinho();
                case "5" -> finalizarCompra();
                case "6" -> listarPedidos();
                case "7" -> gerenciarClientes();
                case "8" -> abrirInterfaceWeb();
                case "0" -> {
                    System.out.println("\n" + colorir("👋 Obrigado por utilizar o E-Commerce Java! Até logo.", BOLD, GREEN));
                    executando = false;
                }
                default -> {
                    System.out.println(colorir("\n❌ Opção inválida! Pressione ENTER para continuar...", RED, BOLD));
                    pausar();
                }
            }
        }
    }

    private void exibirCabecalho() {
        System.out.println("\n" + colorir("╔══════════════════════════════════════════════════════════════════════╗", CYAN));
        System.out.println(colorir("║                   🛒 E-COMMERCE JAVA ENTERPRISE                      ║", BOLD, CYAN));
        System.out.println(colorir("║         Catálogo • Carrinho • Checkout • Pedidos • Web SPA           ║", CYAN));
        System.out.println(colorir("╚══════════════════════════════════════════════════════════════════════╝", CYAN));

        int itensNoCarrinho = clienteLogado != null ? carrinhoService.obterQuantidadeTotalItens(clienteLogado.getId()) : 0;
        double totalCarrinho = clienteLogado != null ? carrinhoService.calcularTotal(clienteLogado.getId()) : 0.0;

        System.out.printf(colorir("👤 Cliente Atual: %s | 📧 %s%n", BOLD, WHITE),
                clienteLogado != null ? clienteLogado.getNome() : "Nenhum",
                clienteLogado != null ? clienteLogado.getEmail() : "N/A");
        System.out.printf(colorir("🛍️  Carrinho: %d item(ns) | Total: R$ %.2f | 🌐 Web Server: http://localhost:%d%n", YELLOW),
                itensNoCarrinho, totalCarrinho, webPort);
        System.out.println(colorir("──────────────────────────────────────────────────────────────────────", CYAN));
    }

    private void exibirMenuPrincipal() {
        System.out.println(colorir("1. 📦 Listar Catálogo Completo de Produtos", WHITE));
        System.out.println(colorir("2. 🔍 Buscar Produtos (por Nome ou Categoria)", WHITE));
        System.out.println(colorir("3. 🔎 Ver Detalhes de um Produto & Adicionar ao Carrinho", WHITE));
        System.out.println(colorir("4. 🛒 Gerenciar Meu Carrinho de Compras", WHITE));
        System.out.println(colorir("5. 💳 Finalizar Compra (Checkout)", GREEN, BOLD));
        System.out.println(colorir("6. 📜 Meus Pedidos & Histórico", WHITE));
        System.out.println(colorir("7. 👥 Trocar de Cliente ou Cadastrar Novo", WHITE));
        System.out.println(colorir("8. 🌐 Abrir / Visualizar Interface Web (React)", MAGENTA, BOLD));
        System.out.println(colorir("0. 🚪 Sair do Sistema", RED));
        System.out.println(colorir("──────────────────────────────────────────────────────────────────────", CYAN));
    }

    private void listarCatalogo() {
        System.out.println("\n" + colorir("--- 📦 CATÁLOGO DE PRODUTOS ---", BOLD, YELLOW));
        imprimirTabelaProdutos(produtoService.listarTodos());
        pausar();
    }

    private void buscarProdutos() {
        System.out.println("\n" + colorir("--- 🔍 BUSCA DE PRODUTOS ---", BOLD, YELLOW));
        System.out.print("Digite o termo de busca (ou ENTER para todos): ");
        String termo = scanner.nextLine().trim();

        System.out.println("\nCategorias disponíveis:");
        System.out.println("0. Todas");
        Categoria[] cats = Categoria.values();
        for (int i = 0; i < cats.length; i++) {
            System.out.printf("%d. %s%n", (i + 1), cats[i].getDescricao());
        }
        System.out.print("Escolha a categoria (número ou ENTER para todas): ");
        String catOpt = scanner.nextLine().trim();
        String categoriaFiltro = null;
        try {
            int catIdx = Integer.parseInt(catOpt);
            if (catIdx >= 1 && catIdx <= cats.length) {
                categoriaFiltro = cats[catIdx - 1].name();
            }
        } catch (NumberFormatException ignored) {}

        List<Produto> resultados = produtoService.buscar(termo, categoriaFiltro);
        System.out.println("\n" + colorir("Resultados encontrados: " + resultados.size(), GREEN, BOLD));
        imprimirTabelaProdutos(resultados);
        pausar();
    }

    private void imprimirTabelaProdutos(List<Produto> lista) {
        if (lista.isEmpty()) {
            System.out.println(colorir("Nenhum produto encontrado.", YELLOW));
            return;
        }

        System.out.println("┌──────┬──────────────────────────────────────────┬──────────────────────┬─────────────┬─────────┬────────┐");
        System.out.printf("│ %-4s │ %-40s │ %-20s │ %-11s │ %-7s │ %-6s │%n",
                "ID", "Nome do Produto", "Categoria", "Preço (R$)", "Estoque", "Rating");
        System.out.println("├──────┼──────────────────────────────────────────┼──────────────────────┼─────────────┼─────────┼────────┤");

        for (Produto p : lista) {
            String nome = p.getNome().length() > 40 ? p.getNome().substring(0, 37) + "..." : p.getNome();
            String cat = p.getCategoria() != null ? p.getCategoria().getDescricao() : "Geral";
            if (cat.length() > 20) cat = cat.substring(0, 17) + "...";

            String estoqueStr = p.getEstoque() <= 5 ? colorir(String.format("%-7d", p.getEstoque()), RED, BOLD) : String.format("%-7d", p.getEstoque());

            System.out.printf("│ %-4d │ %-40s │ %-20s │ R$ %8.2f │ %s │ ⭐ %.1f │%n",
                    p.getId(), nome, cat, p.getPreco(), estoqueStr, p.getAvaliacao());
        }
        System.out.println("└──────┴──────────────────────────────────────────┴──────────────────────┴─────────────┴─────────┴────────┘");
    }

    private void verDetalhesProduto() {
        System.out.println("\n" + colorir("--- 🔎 DETALHES DO PRODUTO ---", BOLD, YELLOW));
        System.out.print("Informe o ID do produto: ");
        Long id = lerLong();
        if (id == null) return;

        Optional<Produto> opt = produtoService.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println(colorir("❌ Produto não encontrado com o ID informado.", RED));
            pausar();
            return;
        }

        Produto p = opt.get();
        System.out.println("\n" + colorir("══════════════════════════════════════════════════════════════════════", CYAN));
        System.out.println(colorir(p.getNome(), BOLD, WHITE));
        System.out.printf("Categoria: %s | Avaliação: ⭐ %.1f (%d avaliações)%n",
                p.getCategoria() != null ? p.getCategoria().getDescricao() : "Geral",
                p.getAvaliacao(), p.getTotalAvaliacoes());
        System.out.println("Descrição: " + p.getDescricao());
        System.out.printf("Preço: %s | Estoque Disponível: %s unidades%n",
                colorir(String.format("R$ %.2f", p.getPreco()), BOLD, GREEN),
                colorir(String.valueOf(p.getEstoque()), BOLD, p.getEstoque() > 5 ? WHITE : RED));
        System.out.println(colorir("══════════════════════════════════════════════════════════════════════", CYAN));

        System.out.print("\nDeseja adicionar este produto ao carrinho? (S/N): ");
        String resp = scanner.nextLine().trim();
        if (resp.equalsIgnoreCase("s")) {
            System.out.print("Quantidade desejada: ");
            Integer qtd = lerInt();
            if (qtd != null && qtd > 0) {
                boolean sucesso = carrinhoService.adicionarProduto(clienteLogado.getId(), p.getId(), qtd);
                if (sucesso) {
                    System.out.println(colorir("✅ Produto adicionado ao carrinho com sucesso!", GREEN, BOLD));
                } else {
                    System.out.println(colorir("❌ Quantidade solicitada excede o estoque disponível (" + p.getEstoque() + " unidades)!", RED, BOLD));
                }
            }
        }
        pausar();
    }

    private void gerenciarCarrinho() {
        boolean noCarrinho = true;
        while (noCarrinho) {
            System.out.println("\n" + colorir("--- 🛒 MEU CARRINHO DE COMPRAS ---", BOLD, YELLOW));
            List<ItemCarrinho> itens = carrinhoService.obterCarrinho(clienteLogado.getId());

            if (itens.isEmpty()) {
                System.out.println(colorir("Seu carrinho está vazio no momento.", YELLOW));
                System.out.println("\n1. Adicionar produto informando o ID");
                System.out.println("0. Voltar ao menu principal");
                System.out.print("👉 Opção: ");
                String op = scanner.nextLine().trim();
                if (op.equals("1")) {
                    adicionarAoCarrinhoDireto();
                } else {
                    noCarrinho = false;
                }
                continue;
            }

            System.out.println("┌──────┬──────────────────────────────────────────┬──────────┬─────────────┬─────────────┐");
            System.out.printf("│ %-4s │ %-40s │ %-8s │ %-11s │ %-11s │%n",
                    "Item", "Produto", "Qtd", "Unitário", "Subtotal");
            System.out.println("├──────┼──────────────────────────────────────────┼──────────┼─────────────┼─────────────┤");

            int idx = 1;
            for (ItemCarrinho item : itens) {
                Produto p = item.getProduto();
                String nome = p.getNome().length() > 40 ? p.getNome().substring(0, 37) + "..." : p.getNome();
                System.out.printf("│ %-4d │ %-40s │ %-8d │ R$ %8.2f │ R$ %8.2f │%n",
                        idx++, nome, item.getQuantidade(), p.getPreco(), item.getSubtotal());
            }
            System.out.println("└──────┴──────────────────────────────────────────┴──────────┴─────────────┴─────────────┘");

            double total = carrinhoService.calcularTotal(clienteLogado.getId());
            System.out.println(colorir(String.format("VALOR TOTAL DO CARRINHO: R$ %.2f", total), BOLD, GREEN));
            System.out.println("\nAções do Carrinho:");
            System.out.println("1. ➕ Adicionar mais um produto");
            System.out.println("2. ✏️ Alterar quantidade de um item");
            System.out.println("3. 🗑️ Remover produto do carrinho");
            System.out.println("4. 🧹 Esvaziar carrinho");
            System.out.println("5. 💳 Ir para Finalização de Compra (Checkout)");
            System.out.println("0. ↩️ Voltar ao Menu Principal");
            System.out.print("👉 Escolha: ");
            String opcao = scanner.nextLine().trim();

            switch (opcao) {
                case "1" -> adicionarAoCarrinhoDireto();
                case "2" -> alterarQuantidadeItem();
                case "3" -> removerItemCarrinho();
                case "4" -> {
                    carrinhoService.limparCarrinho(clienteLogado.getId());
                    System.out.println(colorir("✅ Carrinho esvaziado com sucesso!", GREEN));
                }
                case "5" -> {
                    finalizarCompra();
                    noCarrinho = false;
                }
                case "0" -> noCarrinho = false;
                default -> System.out.println(colorir("Opção inválida!", RED));
            }
        }
    }

    private void adicionarAoCarrinhoDireto() {
        System.out.print("Informe o ID do produto para adicionar: ");
        Long id = lerLong();
        if (id == null) return;
        System.out.print("Quantidade: ");
        Integer qtd = lerInt();
        if (qtd == null || qtd <= 0) return;

        boolean ok = carrinhoService.adicionarProduto(clienteLogado.getId(), id, qtd);
        if (ok) {
            System.out.println(colorir("✅ Item adicionado ao carrinho!", GREEN, BOLD));
        } else {
            System.out.println(colorir("❌ Não foi possível adicionar. Verifique se o ID existe e se há estoque!", RED, BOLD));
        }
    }

    private void alterarQuantidadeItem() {
        System.out.print("Informe o ID do produto a alterar: ");
        Long id = lerLong();
        if (id == null) return;
        System.out.print("Nova quantidade (0 para remover): ");
        Integer qtd = lerInt();
        if (qtd == null) return;

        boolean ok = carrinhoService.atualizarQuantidade(clienteLogado.getId(), id, qtd);
        if (ok) {
            System.out.println(colorir("✅ Quantidade atualizada com sucesso!", GREEN));
        } else {
            System.out.println(colorir("❌ Erro ao atualizar quantidade. Verifique o estoque!", RED));
        }
    }

    private void removerItemCarrinho() {
        System.out.print("Informe o ID do produto a remover: ");
        Long id = lerLong();
        if (id == null) return;
        boolean ok = carrinhoService.removerProduto(clienteLogado.getId(), id);
        if (ok) {
            System.out.println(colorir("✅ Item removido do carrinho!", GREEN));
        } else {
            System.out.println(colorir("❌ Item não encontrado no carrinho.", RED));
        }
    }

    private void finalizarCompra() {
        System.out.println("\n" + colorir("--- 💳 FINALIZAÇÃO DE COMPRA (CHECKOUT) ---", BOLD, GREEN));
        List<ItemCarrinho> itens = carrinhoService.obterCarrinho(clienteLogado.getId());
        if (itens.isEmpty()) {
            System.out.println(colorir("❌ Seu carrinho está vazio! Adicione itens antes de finalizar a compra.", RED));
            pausar();
            return;
        }

        double total = carrinhoService.calcularTotal(clienteLogado.getId());
        System.out.printf(colorir("Total a Pagar: R$ %.2f (%d itens)%n", BOLD, GREEN), total, itens.size());

        System.out.printf("\nEndereço de entrega atual:%n%s%n", colorir(clienteLogado.getEndereco(), WHITE, BOLD));
        System.out.print("Deseja usar este endereço de entrega? (S/N): ");
        String respEnd = scanner.nextLine().trim();
        String enderecoFinal = clienteLogado.getEndereco();
        if (respEnd.equalsIgnoreCase("n")) {
            System.out.print("Digite o novo endereço de entrega completo: ");
            enderecoFinal = scanner.nextLine().trim();
        }

        System.out.println("\nForma de Pagamento:");
        System.out.println("1. ⚡ PIX (Aprovação Instantânea)");
        System.out.println("2. 💳 Cartão de Crédito");
        System.out.println("3. 📄 Boleto Bancário");
        System.out.print("Escolha a forma de pagamento (1-3): ");
        String opPag = scanner.nextLine().trim();
        String metodoPagamento = switch (opPag) {
            case "1" -> "PIX";
            case "2" -> "Cartão de Crédito";
            case "3" -> "Boleto Bancário";
            default -> "PIX";
        };

        System.out.println("\nProcessando pedido e reservando estoque...");
        try {
            Pedido pedido = pedidoService.criarPedido(clienteLogado, enderecoFinal, metodoPagamento);
            System.out.println("\n" + colorir("🎉 PARABÉNS! SEU PEDIDO FOI FINALIZADO COM SUCESSO!", BOLD, GREEN));
            System.out.println(colorir("══════════════════════════════════════════════════════════════════════", GREEN));
            System.out.printf("Número do Pedido: %s%n", colorir(pedido.getId(), BOLD, YELLOW));
            System.out.printf("Status: %s%n", colorir(pedido.getStatus().getDescricaoFormatada(), BOLD, GREEN));
            System.out.printf("Data/Hora: %s%n", pedido.getDataHoraFormatada());
            System.out.printf("Forma de Pagamento: %s%n", pedido.getMetodoPagamento());
            System.out.printf("Endereço de Entrega: %s%n", pedido.getEnderecoEntrega());
            System.out.printf("Valor Total Pago: %s%n", colorir(String.format("R$ %.2f", pedido.getValorTotal()), BOLD, GREEN));
            System.out.println(colorir("══════════════════════════════════════════════════════════════════════", GREEN));
            System.out.println(colorir("O estoque dos produtos foi atualizado automaticamente.", WHITE));
        } catch (Exception e) {
            System.out.println(colorir("❌ Erro ao finalizar pedido: " + e.getMessage(), RED, BOLD));
        }
        pausar();
    }

    private void listarPedidos() {
        System.out.println("\n" + colorir("--- 📜 MEUS PEDIDOS & HISTÓRICO ---", BOLD, YELLOW));
        List<Pedido> pedidos = pedidoService.listarPedidosPorCliente(clienteLogado.getId());
        if (pedidos.isEmpty()) {
            System.out.println(colorir("Você ainda não realizou nenhum pedido.", YELLOW));
            pausar();
            return;
        }

        for (Pedido p : pedidos) {
            System.out.println(colorir("──────────────────────────────────────────────────────────────────────", CYAN));
            System.out.printf("Pedido: %s | Data: %s | Status: %s%n",
                    colorir(p.getId(), BOLD, YELLOW),
                    p.getDataHoraFormatada(),
                    colorir(p.getStatus().getDescricaoFormatada(), BOLD, GREEN));
            System.out.printf("Pagamento: %s | Entrega: %s%n", p.getMetodoPagamento(), p.getEnderecoEntrega());
            System.out.println("Itens Comprados:");
            for (ItemCarrinho item : p.getItens()) {
                System.out.printf("  • %-35s x%d = R$ %.2f%n",
                        item.getProduto().getNome(), item.getQuantidade(), item.getSubtotal());
            }
            System.out.printf(colorir("VALOR TOTAL: R$ %.2f%n", BOLD, GREEN), p.getValorTotal());
        }
        System.out.println(colorir("──────────────────────────────────────────────────────────────────────", CYAN));
        pausar();
    }

    private void gerenciarClientes() {
        System.out.println("\n" + colorir("--- 👥 GERENCIAMENTO DE CLIENTES ---", BOLD, YELLOW));
        List<Cliente> clientes = clienteService.listarTodos();
        System.out.println("Clientes cadastrados:");
        for (int i = 0; i < clientes.size(); i++) {
            Cliente c = clientes.get(i);
            boolean ativo = c.equals(clienteLogado);
            System.out.printf("%d. %s (%s) %s%n",
                    (i + 1), c.getNome(), c.getEmail(),
                    ativo ? colorir("[ATIVO ATUALMENTE]", GREEN, BOLD) : "");
        }
        System.out.println("\nOpções:");
        System.out.println("1. Selecionar cliente existente");
        System.out.println("2. Cadastrar novo cliente");
        System.out.println("0. Voltar");
        System.out.print("👉 Opção: ");
        String op = scanner.nextLine().trim();

        if (op.equals("1")) {
            System.out.print("Número do cliente desejado: ");
            Integer idx = lerInt();
            if (idx != null && idx >= 1 && idx <= clientes.size()) {
                this.clienteLogado = clientes.get(idx - 1);
                System.out.println(colorir("✅ Cliente ativo alterado para: " + clienteLogado.getNome(), GREEN, BOLD));
            }
        } else if (op.equals("2")) {
            System.out.print("Nome completo: ");
            String nome = scanner.nextLine().trim();
            System.out.print("E-mail: ");
            String email = scanner.nextLine().trim();
            System.out.print("Telefone: ");
            String tel = scanner.nextLine().trim();
            System.out.print("Endereço completo: ");
            String end = scanner.nextLine().trim();

            if (!nome.isEmpty() && !email.isEmpty()) {
                Cliente novo = clienteService.cadastrar(nome, email, tel, end);
                this.clienteLogado = novo;
                System.out.println(colorir("✅ Cliente cadastrado e ativado com sucesso!", GREEN, BOLD));
            } else {
                System.out.println(colorir("❌ Nome e e-mail são obrigatórios.", RED));
            }
        }
        pausar();
    }

    private void abrirInterfaceWeb() {
        System.out.println("\n" + colorir("--- 🌐 INTERFACE WEB MODERNA (SPA) ---", BOLD, MAGENTA));
        System.out.println(colorir("O servidor Web embutido está ATIVO e pronto para uso!", GREEN, BOLD));
        System.out.printf("Acesse a aplicação no navegador em:%n👉 %s%n",
                colorir("http://localhost:" + webPort, BOLD, UNDERLINE, CYAN));
        System.out.println("\nRecursos disponíveis na Web:");
        System.out.println("• Grid moderno e responsivo com imagens em alta resolução Unsplash");
        System.out.println("• Busca em tempo real e filtros inteligentes por categoria");
        System.out.println("• Carrinho de compras dinâmico com contador no header");
        System.out.println("• Modal de detalhes do produto com avaliação por estrelas");
        System.out.println("• Checkout completo multi-etapas com simulação de pagamento");
        pausar();
    }

    private Long lerLong() {
        try {
            return Long.parseLong(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(colorir("Número inválido!", RED));
            return null;
        }
    }

    private Integer lerInt() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println(colorir("Número inválido!", RED));
            return null;
        }
    }

    private void pausar() {
        System.out.print(colorir("\nPressione ENTER para continuar...", WHITE));
        scanner.nextLine();
    }
}
