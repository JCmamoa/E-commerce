package com.ecommerce.server;

import com.ecommerce.model.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class JsonUtils {

    public static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public static String produtoToJson(Produto p) {
        if (p == null) return "null";
        return String.format(java.util.Locale.US,
                "{\"id\":%d,\"nome\":\"%s\",\"descricao\":\"%s\",\"preco\":%.2f,\"estoque\":%d,\"categoria\":\"%s\",\"categoriaNome\":\"%s\",\"imagemUrl\":\"%s\",\"avaliacao\":%.1f,\"totalAvaliacoes\":%d}",
                p.getId(),
                escape(p.getNome()),
                escape(p.getDescricao()),
                p.getPreco(),
                p.getEstoque(),
                p.getCategoria() != null ? p.getCategoria().name() : "",
                p.getCategoria() != null ? escape(p.getCategoria().getDescricao()) : "Geral",
                escape(p.getImagemUrl()),
                p.getAvaliacao(),
                p.getTotalAvaliacoes()
        );
    }

    public static String produtosToJson(List<Produto> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(produtoToJson(lista.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String clienteToJson(Cliente c) {
        if (c == null) return "null";
        return String.format(
                "{\"id\":%d,\"nome\":\"%s\",\"email\":\"%s\",\"telefone\":\"%s\",\"endereco\":\"%s\"}",
                c.getId(),
                escape(c.getNome()),
                escape(c.getEmail()),
                escape(c.getTelefone()),
                escape(c.getEndereco())
        );
    }

    public static String clientesToJson(List<Cliente> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(clienteToJson(lista.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    public static String itemCarrinhoToJson(ItemCarrinho item) {
        if (item == null) return "null";
        return String.format(java.util.Locale.US,
                "{\"produto\":%s,\"quantidade\":%d,\"subtotal\":%.2f}",
                produtoToJson(item.getProduto()),
                item.getQuantidade(),
                item.getSubtotal()
        );
    }

    public static String carrinhoToJson(List<ItemCarrinho> itens, double total, int quantidadeTotal) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(java.util.Locale.US, "{\"total\":%.2f,\"quantidadeTotal\":%d,\"itens\":[", total, quantidadeTotal));
        for (int i = 0; i < itens.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(itemCarrinhoToJson(itens.get(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

    public static String pedidoToJson(Pedido p) {
        if (p == null) return "null";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(java.util.Locale.US,
                "{\"id\":\"%s\",\"cliente\":%s,\"valorTotal\":%.2f,\"status\":\"%s\",\"statusDescricao\":\"%s\",\"statusIcone\":\"%s\",\"dataHora\":\"%s\",\"enderecoEntrega\":\"%s\",\"metodoPagamento\":\"%s\",\"itens\":[",
                escape(p.getId()),
                clienteToJson(p.getCliente()),
                p.getValorTotal(),
                p.getStatus() != null ? p.getStatus().name() : "",
                p.getStatus() != null ? escape(p.getStatus().getDescricao()) : "",
                p.getStatus() != null ? escape(p.getStatus().getIcone()) : "",
                escape(p.getDataHoraFormatada()),
                escape(p.getEnderecoEntrega()),
                escape(p.getMetodoPagamento())
        ));
        List<ItemCarrinho> itens = p.getItens();
        for (int i = 0; i < itens.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(itemCarrinhoToJson(itens.get(i)));
        }
        sb.append("]}");
        return sb.toString();
    }

    public static String pedidosToJson(List<Pedido> lista) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < lista.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(pedidoToJson(lista.get(i)));
        }
        sb.append("]");
        return sb.toString();
    }

    public static Map<String, String> parseSimpleJson(String json) {
        Map<String, String> map = new HashMap<>();
        if (json == null) return map;
        String clean = json.trim();
        if (clean.startsWith("{")) clean = clean.substring(1);
        if (clean.endsWith("}")) clean = clean.substring(0, clean.length() - 1);

        String[] pairs = clean.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        for (String pair : pairs) {
            String[] kv = pair.split(":(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", 2);
            if (kv.length == 2) {
                String k = kv[0].trim().replace("\"", "");
                String v = kv[1].trim();
                if (v.startsWith("\"") && v.endsWith("\"") && v.length() >= 2) {
                    v = v.substring(1, v.length() - 1);
                }
                map.put(k, v);
            }
        }
        return map;
    }
}
