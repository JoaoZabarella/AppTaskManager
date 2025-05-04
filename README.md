# 📋 Gerenciador de Tarefas (Notion-like)

Um gerenciador de tarefas inspirado no Notion, que permite aos usuários criar, organizar e gerenciar suas tarefas de forma eficiente. O sistema inclui funcionalidades como categorias de tarefas, status, comentários, e até mesmo anexos de arquivos.

## ✨ Funcionalidades

- **📝 Gerenciamento de Tarefas**: Criar, editar e excluir tarefas.
- **🏷️ Categorias**: Organize suas tarefas por categorias (ex.: "Trabalho", "Pessoal").
- **📊 Acompanhamento de Status**: Monitore o progresso de suas tarefas (Novo, Em Andamento, Concluído, Bloqueado, Cancelado).
- **🔥 Níveis de Prioridade**: Defina prioridades para suas tarefas (Baixa, Média, Alta, Urgente).
- **💬 Comentários**: Adicione comentários nas tarefas para contexto adicional ou notas.
- **✅ Validação de Dados**: Garantia de integridade dos dados com validações de entrada.
- **🔐 Autenticação de Usuários**: Login seguro com tokens JWT.
- **👑 Acesso baseado em Funções**: Papéis de administrador e usuário comum.

## 🛠️ Tecnologias Utilizadas

- **☕ Java 17**: Linguagem principal.
- **🍃 Spring Boot 3.2.5**: Framework para construir a API REST.
- **🔒 Spring Security**: Para autenticação e autorização.
- **🗄️ JPA / Hibernate**: Para persistência de dados.
- **🐬 MySQL**: Banco de dados relacional.
- **🔄 Flyway**: Ferramenta de migração de banco de dados.
- **🔑 JWT (JSON Web Tokens)**: Para autenticação.
- **✓ Bean Validation**: Para validar entrada do usuário.
- **🧰 Lombok**: Para reduzir código boilerplate.
- **🧪 JUnit & Mockito**: Para testes unitários.
- **📚 Swagger/OpenAPI**: Documentação da API.

## 🏗️ Estrutura do Projeto

O projeto segue uma arquitetura padrão do Spring Boot:

- **🎮 Controller**: Lidar com requisições HTTP e respostas
- **⚙️ Service**: Implementar lógica de negócios
- **💾 Repository**: Interface com o banco de dados
- **📦 Model**: Representar entidades de domínio
- **📤 DTO**: Transferir dados entre as camadas
- **🔍 Validator**: Validar regras de negócio
- **⚙️ Config**: Configurar segurança e tratamento de exceções

## 🚀 Começando

### 📋 Pré-requisitos

- Java 17 ou superior
- Maven
- Banco de dados MySQL
- Docker (opcional, para contêinerização)
- Conta AWS (opcional, para implantação em nuvem)

### 🔧 Configuração e Instalação

1. Clone o repositório:
   ```bash
   git clone https://github.com/seuusuario/taskmanager.git
   ```

2. Navegue até o diretório do projeto:
   ```bash
   cd taskmanager
   ```

3. Crie um banco de dados MySQL:
   ```sql
   CREATE DATABASE taskmanager;
   ```

4. Configure a conexão com o banco de dados em `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   
   # JWT Secret
   api.security.token.secret=seu_segredo_jwt
   ```

5. Construa e execute a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```

6. Acesse a API em `http://localhost:8080`

7. Veja a documentação Swagger em `http://localhost:8080/swagger-ui.html`

## 🔌 Endpoints da API

### 🔑 Autenticação
- `POST /auth/login`: Autenticar e obter token JWT

### 👤 Usuários
- `POST /usuario`: Registrar um novo usuário
- `GET /usuario/me`: Obter detalhes do usuário autenticado
- `PUT /usuario/me`: Atualizar usuário autenticado
- `DELETE /usuario/me`: Desativar usuário autenticado
- `PUT /usuario/me/alterar-senha`: Alterar senha

### 🏷️ Categorias
- `POST /categorias`: Criar uma nova categoria
- `GET /categorias`: Listar todas as categorias
- `PUT /categorias/{id}`: Atualizar uma categoria
- `DELETE /categorias/{id}`: Excluir uma categoria

### 📝 Tarefas
- `POST /tarefas`: Criar uma nova tarefa
- `GET /tarefas/paginado`: Listar tarefas com paginação
- `PUT /tarefas/{id}`: Atualizar uma tarefa
- `GET /tarefas/filtrar`: Filtrar tarefas por status, prioridade e categoria
- `GET /tarefas/filtrar/palavra`: Buscar tarefas por palavra-chave
- `DELETE /tarefas/arquivar/{id}`: Arquivar uma tarefa
- `PATCH /tarefas/arquivar`: Arquivar múltiplas tarefas
- `DELETE /tarefas/deletar/{id}`: Excluir uma tarefa
- `DELETE /tarefas/deletar/multiplas`: Excluir múltiplas tarefas
- `PATCH /tarefas/concluir/{id}`: Marcar uma tarefa como concluída
- `PATCH /tarefas/concluir`: Marcar múltiplas tarefas como concluídas
- `PATCH /tarefas/reabrir/{id}`: Reabrir uma tarefa concluída
- `PATCH /tarefas/reabrir`: Reabrir múltiplas tarefas concluídas

### 👑 Endpoints de Administrador
- `POST /admin/usuarios/promover/{id}`: Promover usuário para administrador
- `PUT /admin/usuarios/remover/{id}`: Remover papel de administrador do usuário
- `DELETE /admin/usuarios/{id}`: Desativar um usuário
- `GET /admin/usuarios`: Buscar usuários por nome ou email
- `PUT /admin/usuarios/ativar/{id}`: Reativar um usuário

## 🧪 Testes

Execute testes com Maven:
```bash
./mvnw test
```

## 🔄 Migrações do Banco de Dados

A aplicação usa Flyway para migrações de banco de dados. Os scripts de migração estão localizados em `src/main/resources/db/migration`.

## 👤 Usuários Iniciais

O sistema cria um usuário administrador na primeira execução:
- Email: admin@taskmanager.com
- Senha: admin123


## 🐳 Docker e Implantação

Este projeto suporta contêinerização com Docker e implantação na AWS.

### Docker
Um Dockerfile e configuração docker-compose estão incluídos para facilitar a contêinerização.

### AWS
O projeto inclui workflows GitHub Actions para implantação contínua na AWS ECS.

## 🔮 Frontend

O frontend para este projeto será desenvolvido separadamente. Esta API REST foi projetada com endpoints bem documentados para facilitar a integração com qualquer frontend (React, Angular, Vue, etc).

---

# 📋 Task Manager (Notion-like)

A task manager inspired by Notion that allows users to create, organize and manage their tasks efficiently. The system includes features such as task categories, status, comments, and even file attachments.

## ✨ Features

- **📝 Task Management**: Create, edit and delete tasks.
- **🏷️ Categories**: Organize your tasks by categories (e.g., "Work", "Personal").
- **📊 Status Tracking**: Monitor the progress of your tasks (New, In Progress, Completed, Blocked, Canceled).
- **🔥 Priority Levels**: Set priorities for your tasks (Low, Medium, High, Urgent).
- **💬 Comments**: Add comments to tasks for additional context or notes.
- **✅ Data Validation**: Ensure data integrity with input validations.
- **🔐 User Authentication**: Secure login with JWT tokens.
- **👑 Role-based Access**: Admin and regular user roles.

## 🛠️ Technologies Used

- **☕ Java 17**: Primary programming language.
- **🍃 Spring Boot 3.2.5**: Framework for building the REST API.
- **🔒 Spring Security**: For authentication and authorization.
- **🗄️ JPA / Hibernate**: For data persistence.
- **🐬 MySQL**: Relational database.
- **🔄 Flyway**: Database migration tool.
- **🔑 JWT (JSON Web Tokens)**: For authentication.
- **✓ Bean Validation**: For validating user input.
- **🧰 Lombok**: To reduce boilerplate code.
- **🧪 JUnit & Mockito**: For unit testing.
- **📚 Swagger/OpenAPI**: API documentation.

## 🏗️ Project Structure

The project follows a standard Spring Boot architecture:

- **🎮 Controller**: Handle HTTP requests and responses
- **⚙️ Service**: Implement business logic
- **💾 Repository**: Interface with the database
- **📦 Model**: Represent domain entities
- **📤 DTO**: Transfer data between layers
- **🔍 Validator**: Validate business rules
- **⚙️ Config**: Configure security and exception handling

## 🚀 Getting Started

### 📋 Prerequisites

- Java 17 or higher
- Maven
- MySQL database
- Docker (optional, for containerization)
- AWS Account (optional, for cloud deployment)

### 🔧 Setup and Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/taskmanager.git
   ```

2. Navigate to the project directory:
   ```bash
   cd taskmanager
   ```

3. Create a MySQL database:
   ```sql
   CREATE DATABASE taskmanager;
   ```

4. Configure the database connection in `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/taskmanager
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # JWT Secret
   api.security.token.secret=your_jwt_secret
   ```

5. Build and run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

6. Access the API at `http://localhost:8080`

7. View the Swagger documentation at `http://localhost:8080/swagger-ui.html`

## 🔌 API Endpoints

### 🔑 Authentication
- `POST /auth/login`: Authenticate and get JWT token

### 👤 Users
- `POST /usuario`: Register a new user
- `GET /usuario/me`: Get authenticated user details
- `PUT /usuario/me`: Update authenticated user
- `DELETE /usuario/me`: Deactivate authenticated user
- `PUT /usuario/me/alterar-senha`: Change password

### 🏷️ Categories
- `POST /categorias`: Create a new category
- `GET /categorias`: List all categories
- `PUT /categorias/{id}`: Update a category
- `DELETE /categorias/{id}`: Delete a category

### 📝 Tasks
- `POST /tarefas`: Create a new task
- `GET /tarefas/paginado`: List tasks with pagination
- `PUT /tarefas/{id}`: Update a task
- `GET /tarefas/filtrar`: Filter tasks by status, priority, and category
- `GET /tarefas/filtrar/palavra`: Search tasks by keyword
- `DELETE /tarefas/arquivar/{id}`: Archive a task
- `PATCH /tarefas/arquivar`: Archive multiple tasks
- `DELETE /tarefas/deletar/{id}`: Delete a task
- `DELETE /tarefas/deletar/multiplas`: Delete multiple tasks
- `PATCH /tarefas/concluir/{id}`: Mark a task as completed
- `PATCH /tarefas/concluir`: Mark multiple tasks as completed
- `PATCH /tarefas/reabrir/{id}`: Reopen a completed task
- `PATCH /tarefas/reabrir`: Reopen multiple completed tasks

### 👑 Admin Endpoints
- `POST /admin/usuarios/promover/{id}`: Promote user to admin
- `PUT /admin/usuarios/remover/{id}`: Remove admin role from user
- `DELETE /admin/usuarios/{id}`: Deactivate a user
- `GET /admin/usuarios`: Search users by name or email
- `PUT /admin/usuarios/ativar/{id}`: Reactivate a user

## 🧪 Testing

Run tests with Maven:
```bash
./mvnw test
```

## 🔄 Database Migrations

The application uses Flyway for database migrations. Migration scripts are located in `src/main/resources/db/migration`.

## 👤 Initial Users

The system creates an admin user on the first run:
- Email: admin@taskmanager.com
- Password: admin123

## 🐳 Docker and Deployment

This project supports containerization with Docker and deployment to AWS.

### Docker
A Dockerfile and docker-compose configuration are included to facilitate containerization.

### AWS
The project includes GitHub Actions workflows for continuous deployment to AWS ECS.

## 🔮 Frontend

The frontend for this project will be developed separately. This REST API was designed with well-documented endpoints to facilitate integration with any frontend (React, Angular, Vue, etc).