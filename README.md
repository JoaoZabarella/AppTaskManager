﻿ # AppTaskManager


# Gerenciador de Tarefas (Notion-like)

Este é um gerenciador de tarefas inspirado no Notion, que permite aos usuários criar, organizar e gerenciar suas tarefas de forma eficiente. O sistema inclui funcionalidades como categorias de tarefas, status, comentários, e até mesmo anexos de arquivos.

## Funcionalidades

- **Gerenciamento de Tarefas**: Criar, editar e excluir tarefas.
- **Categorias**: Organize suas tarefas por categorias (ex.: "Trabalho", "Pessoal").
- **Comentários**: Adicione comentários nas tarefas.
- **Arquivos**: Anexe arquivos às suas tarefas.
- **Validações**: Garantia de integridade dos dados com validações de entrada.

## Tecnologias Utilizadas

- **Java**: Linguagem principal.
- **Spring Boot**: Framework para construir a API REST.
- **JPA / Hibernate**: Para persistência de dados.
- **Banco de Dados**: MySQL (ou outro banco de dados relacional de sua preferência).
- **Bean Validation**: Validações dos dados do usuário.
- **Thymeleaf / JSP**: Para templates de interface, caso haja necessidade de um frontend simples.

## Como Rodar o Projeto

### Pré-requisitos

- Java 11 ou superior.
- Maven.
- MySQL ou outro banco de dados relacional.

### Passos para rodar

1. Clone o repositório:
   ```bash
   git clone https://github.com/seuusuario/gerenciador-de-tarefas.git
