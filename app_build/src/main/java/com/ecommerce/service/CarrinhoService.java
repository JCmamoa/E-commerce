package com.ecommerce.service;

import com.ecommerce.model.ItemCarrinho;
import com.ecommerce.model.Produto;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CarrinhoService {
    private final Map<Long, List<ItemCarrinho>> carrinhos = new ConcurrentHashMap<>();
    private final ProdutoService produtoService;

    public CarrinhoService(ProdutoService produtoService) {
        this.produtoService = Objects.requireNonNull(produtoService, "produtoService não pode ser nulo");
    }

    public synchronized List<ItemCarrinho> obterCarrinho(Long clienteId) {
        return carrinhos.computeIfAbsent(clienteId, k -> new ArrayList<>());
    }

    public synchronized boolean adicionarProduto(Long clienteId, Long produtoId, int quantidade) {
        if (quantidade <= 0) return false;

        Optional<Produto> optProduto = produtoService.buscarPorId(produtoId);
        if (optProduto.isEmpty()) return false;

        Produto produto = optProduto.get();
        List<ItemCarrinho> itens = obterCarrinho(clienteId);

        Optional<ItemCarrinho> optItem = itens.stream()
                .filter(item -> Objects.equals(item.getProduto().getId(), produtoId))
                .findFirst();

        int quantidadeFinal = quantidade;
        if (optItem.isPresent()) {
            quantidadeFinal += optItem.get().getQuantidade();
        }

        if (!produto.temEstoque(quantidadeFinal)) {
            return false;
        }

        if (optItem.isPresent()) {
            optItem.get().setQuantidade(quantidadeFinal);
        } else {
            itens.add(new ItemCarrinho(produto, quantidade));
        }

        return true;
    }

    public synchronized boolean atualizarQuantidade(Long clienteId, Long produtoId, int novaQuantidade) {
        List<ItemCarrinho> itens = obterCarrinho(clienteId);
        Optional<ItemCarrinho> optItem = itens.stream()
                .filter(item -> Objects.equals(item.getProduto().getId(), produtoId))
                .findFirst();

        if (optItem.isEmpty()) return false;

        if (novaQuantidade <= 0) {
            itens.remove(optItem.get());
            return true;
        }

        Optional<Produto> optProduto = produtoService.buscarPorId(produtoId);
        if (optProduto.isEmpty()) return false;

        Produto produto = optProduto.get();
        if (!produto.temEstoque(novaQuantidade)) {
            return false;
        }

        optItem.get().setQuantidade(novaQuantidade);
        return true;
    }

    public synchronized boolean removerProduto(Long clienteId, Long produtoId) {
        List<ItemCarrinho> itens = obterCarrinho(clienteId);
        return itens.removeIf(item -> Objects.equals(item.getProduto().getId(), produtoId));
    }

    public synchronized double calcularTotal(Long clienteId) {
        List<ItemCarrinho> itens = obterCarrinho(clienteId);
        return itens.stream().mapToDouble(ItemCarrinho::getSubtotal).sum();
    }

    public synchronized int obterQuantidadeTotalItens(Long clienteId) {
        List<ItemCarrinho> itens = obterCarrinho(clienteId);
        return itens.stream().mapToInt(ItemCarrinho::getQuantidade).sum();
    }

    public synchronized void limparCarrinho(Long clienteId) {
        carrinhos.remove(clienteId);
    }
}
