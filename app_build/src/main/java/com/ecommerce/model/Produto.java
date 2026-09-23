package com.ecommerce.model;

import java.util.Objects;

public class Produto {
    private Long id;
    private String nome;
    private String descricao;
    private double preco;
    private int estoque;
    private Categoria categoria;
    private String imagemUrl;
    private double avaliacao;
    private int totalAvaliacoes;

    public Produto() {
    }

    public Produto(Long id, String nome, String descricao, double preco, int estoque, Categoria categoria, String imagemUrl, double avaliacao, int totalAvaliacoes) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
        this.categoria = categoria;
        this.imagemUrl = imagemUrl;
        this.avaliacao = avaliacao;
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public int getEstoque() {
        return estoque;
    }

    public void setEstoque(int estoque) {
        this.estoque = estoque;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public int getTotalAvaliacoes() {
        return totalAvaliacoes;
    }

    public void setTotalAvaliacoes(int totalAvaliacoes) {
        this.totalAvaliacoes = totalAvaliacoes;
    }

    public synchronized boolean temEstoque(int quantidade) {
        return this.estoque >= quantidade && quantidade > 0;
    }

    public synchronized boolean debitarEstoque(int quantidade) {
        if (temEstoque(quantidade)) {
            this.estoque -= quantidade;
            return true;
        }
        return false;
    }

    public synchronized void reporEstoque(int quantidade) {
        if (quantidade > 0) {
            this.estoque += quantidade;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Produto produto = (Produto) o;
        return Objects.equals(id, produto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - R$ %.2f | Estoque: %d | Categoria: %s",
                id, nome, preco, estoque, categoria != null ? categoria.getDescricao() : "Geral");
    }
}
