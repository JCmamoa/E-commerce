package com.ecommerce.service;

import com.ecommerce.model.Cliente;
import com.ecommerce.model.ItemCarrinho;
import com.ecommerce.model.Pedido;
import com.ecommerce.model.StatusPedido;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class PedidoService {
    private final List<Pedido> pedidos = new CopyOnWriteArrayList<>();
    private final ProdutoService produtoService;
    private final CarrinhoService carrinhoService;
    private int contadorPedido = 1000;

    public PedidoService(ProdutoService produtoService, CarrinhoService carrinhoService, ClienteService clienteService) {
        this.produtoService = Objects.requireNonNull(produtoService);
        this.carrinhoService = Objects.requireNonNull(carrinhoService);
        carregarPedidosIniciais(clienteService);
    }

    private void carregarPedidosIniciais(ClienteService clienteService) {
        Optional<Cliente> optJoao = clienteService.login("joao.silva@email.com");
        if (optJoao.isPresent()) {
            Cliente joao = optJoao.get();
            carrinhoService.adicionarProduto(joao.getId(), 3L, 1); // Fone Bluetooth
            carrinhoService.adicionarProduto(joao.getId(), 9L, 1); // Garrafa térmica
            Pedido pedido1 = criarPedido(joao, joao.getEndereco(), "PIX");
            if (pedido1 != null) {
                pedido1.setStatus(StatusPedido.ENVIADO);
            }
        }
    }

    public synchronized Pedido criarPedido(Cliente cliente, String enderecoEntrega, String metodoPagamento) {
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente não informado para o pedido.");
        }

        List<ItemCarrinho> itensCarrinho = carrinhoService.obterCarrinho(cliente.getId());
        if (itensCarrinho.isEmpty()) {
            throw new IllegalStateException("O carrinho está vazio. Adicione itens antes de finalizar a compra.");
        }

        // 1. Validar estoque de todos os itens antes de prosseguir
        for (ItemCarrinho item : itensCarrinho) {
            if (!produtoService.verificarEstoque(item.getProduto().getId(), item.getQuantidade())) {
                throw new IllegalStateException("Estoque insuficiente para o produto: " + item.getProduto().getNome());
            }
        }

        // 2. Debitar estoque de todos os itens
        for (ItemCarrinho item : itensCarrinho) {
            boolean debitado = produtoService.debitarEstoque(item.getProduto().getId(), item.getQuantidade());
            if (!debitado) {
                throw new IllegalStateException("Falha ao debitar estoque de: " + item.getProduto().getNome());
            }
        }

        // 3. Montar cópia imutável dos itens para o pedido
        List<ItemCarrinho> itensPedido = new ArrayList<>();
        for (ItemCarrinho item : itensCarrinho) {
            itensPedido.add(new ItemCarrinho(item.getProduto(), item.getQuantidade()));
        }

        double total = carrinhoService.calcularTotal(cliente.getId());
        String idPedido = "PED-" + (++contadorPedido);

        String enderecoFinal = (enderecoEntrega != null && !enderecoEntrega.trim().isEmpty())
                ? enderecoEntrega.trim()
                : cliente.getEndereco();

        String pagamentoFinal = (metodoPagamento != null && !metodoPagamento.trim().isEmpty())
                ? metodoPagamento.trim()
                : "Cartão de Crédito";

        Pedido pedido = new Pedido(idPedido, cliente, itensPedido, total, StatusPedido.PAGO, enderecoFinal, pagamentoFinal);
        pedidos.add(0, pedido); // insere no início da lista

        // 4. Limpar o carrinho após a compra com sucesso
        carrinhoService.limparCarrinho(cliente.getId());

        return pedido;
    }

    public List<Pedido> listarPedidosPorCliente(Long clienteId) {
        if (clienteId == null) return Collections.emptyList();
        return pedidos.stream()
                .filter(p -> p.getCliente() != null && Objects.equals(p.getCliente().getId(), clienteId))
                .collect(Collectors.toList());
    }

    public List<Pedido> listarTodos() {
        return new ArrayList<>(pedidos);
    }

    public Optional<Pedido> buscarPorId(String id) {
        if (id == null) return Optional.empty();
        return pedidos.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id.trim()))
                .findFirst();
    }

    public boolean atualizarStatus(String id, StatusPedido novoStatus) {
        Optional<Pedido> optPedido = buscarPorId(id);
        if (optPedido.isPresent()) {
            optPedido.get().setStatus(novoStatus);
            return true;
        }
        return false;
    }
}
