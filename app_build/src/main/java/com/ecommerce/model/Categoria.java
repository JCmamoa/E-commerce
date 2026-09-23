package com.ecommerce.model;

public enum Categoria {
    ELETRONICOS("Eletrônicos"),
    MODA("Moda & Vestuário"),
    CASA("Casa & Decoração"),
    ESPORTES("Esportes & Lazer"),
    LIVROS("Livros & Papelaria"),
    BELEZA("Beleza & Perfumaria");

    private final String descricao;

    Categoria(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Categoria fromString(String texto) {
        if (texto == null) return null;
        for (Categoria c : Categoria.values()) {
            if (c.name().equalsIgnoreCase(texto) || c.descricao.equalsIgnoreCase(texto)) {
                return c;
            }
        }
        return null;
    }
}
