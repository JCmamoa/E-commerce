package com.ecommerce.model;

public enum StatusPedido {
    AGUARDANDO_PAGAMENTO("Aguardando Pagamento", "🟡"),
    PAGO("Pago", "🟢"),
    EM_SEPARACAO("Em Separação", "📦"),
    ENVIADO("Enviado para Transporte", "🚚"),
    ENTREGUE("Entregue", "✅"),
    CANCELADO("Cancelado", "❌");

    private final String descricao;
    private final String icone;

    StatusPedido(String descricao, String icone) {
        this.descricao = descricao;
        this.icone = icone;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getIcone() {
        return icone;
    }

    public String getDescricaoFormatada() {
        return icone + " " + descricao;
    }
}
