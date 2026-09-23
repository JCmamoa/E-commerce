package com.ecommerce.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Pedido {
    private String id;
    private Cliente cliente;
    private List<ItemCarrinho> itens = new ArrayList<>();
    private double valorTotal;
    private StatusPedido status;
    private LocalDateTime dataHora;
    private String enderecoEntrega;
    private String metodoPagamento;

    public Pedido() {
        this.dataHora = LocalDateTime.now();
        this.status = StatusPedido.PAGO;
    }

    public Pedido(String id, Cliente cliente, List<ItemCarrinho> itens, double valorTotal, StatusPedido status, String enderecoEntrega, String metodoPagamento) {
        this.id = id;
        this.cliente = cliente;
        this.itens = itens != null ? new ArrayList<>(itens) : new ArrayList<>();
        this.valorTotal = valorTotal;
        this.status = status;
        this.dataHora = LocalDateTime.now();
        this.enderecoEntrega = enderecoEntrega;
        this.metodoPagamento = metodoPagamento;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<ItemCarrinho> getItens() {
        return itens;
    }

    public void setItens(List<ItemCarrinho> itens) {
        this.itens = itens;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public void setStatus(StatusPedido status) {
        this.status = status;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public String getDataHoraFormatada() {
        if (dataHora == null) return "";
        return dataHora.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
    }

    @Override
    public String toString() {
        return String.format("Pedido %s | Total: R$ %.2f | Status: %s | Data: %s",
                id, valorTotal, status != null ? status.getDescricaoFormatada() : "N/A", getDataHoraFormatada());
    }
}
