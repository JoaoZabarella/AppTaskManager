package com.example.taskmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public enum Prioridade {

    BAIXA("Baixa"),
    MEDIA("Media"),
    ALTA("Alta"),
    URGENTE("Urgente");


    private String descricao;

    Prioridade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static Prioridade fromString(String texto) {
        for (Prioridade p : Prioridade.values()) {
            if(p.descricao.equals(texto)) {
                return p;
            }
        }
        throw new IllegalArgumentException("Prioridade não reconhecida: " + texto);
    }
}
