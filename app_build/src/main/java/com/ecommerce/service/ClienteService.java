package com.ecommerce.service;

import com.ecommerce.model.Cliente;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ClienteService {
    private final Map<Long, Cliente> clientes = new ConcurrentHashMap<>();
    private final Map<String, Cliente> clientesPorEmail = new ConcurrentHashMap<>();
    private final AtomicLong idCounter = new AtomicLong(1);

    public ClienteService() {
        carregarClientesIniciais();
    }

    private void carregarClientesIniciais() {
        cadastrar("João Silva", "joao.silva@email.com", "(11) 98765-4321", "Av. Paulista, 1000, Apto 82 - Bela Vista, São Paulo - SP");
        cadastrar("Maria Oliveira", "maria.oliveira@email.com", "(21) 97654-3210", "Rua das Laranjeiras, 450 - Laranjeiras, Rio de Janeiro - RJ");
        cadastrar("Carlos Eduardo Santos", "carlos.santos@email.com", "(31) 99812-7744", "Av. Afonso Pena, 1500 - Funcionários, Belo Horizonte - MG");
    }

    public Cliente cadastrar(String nome, String email, String telefone, String endereco) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("O e-mail é obrigatório.");
        }
        String emailPadrao = email.trim().toLowerCase();
        if (clientesPorEmail.containsKey(emailPadrao)) {
            return clientesPorEmail.get(emailPadrao);
        }

        Long id = idCounter.getAndIncrement();
        Cliente cliente = new Cliente(id, nome.trim(), emailPadrao, telefone, endereco);
        clientes.put(id, cliente);
        clientesPorEmail.put(emailPadrao, cliente);
        return cliente;
    }

    public Optional<Cliente> login(String email) {
        if (email == null) return Optional.empty();
        return Optional.ofNullable(clientesPorEmail.get(email.trim().toLowerCase()));
    }

    public Optional<Cliente> buscarPorId(Long id) {
        if (id == null) return Optional.empty();
        return Optional.ofNullable(clientes.get(id));
    }

    public List<Cliente> listarTodos() {
        return new ArrayList<>(clientes.values());
    }
}
