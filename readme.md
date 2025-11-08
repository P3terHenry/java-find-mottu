<a id="readme-top"></a>

# 📱 Challange - Mottu - Java API com Thymeleaf - Find Mottu

![Static Badge](https://img.shields.io/badge/build-passing-brightgreen) ![Static Badge](https://img.shields.io/badge/Version-1.0.3-black) ![License](https://img.shields.io/badge/license-MIT-lightgrey)

## 🧑‍🤝‍🧑 Informações dos Contribuintes

| Nome | Matricula | Turma |
| :------------: | :------------: | :------------: |
| Felipe Nogueira Ramon | 555335 | 2TDSPH |
 | Gustavo | 123456 | 2TDSPH |
| Pedro Herique Vasco Antonieti | 556253 | 2TDSPH |
[Voltar ao topo](#readme-top)

## 🚩 Características

**Find Mottu** é uma solução completa de gestão de frota de motocicletas desenvolvida com tecnologias modernas e práticas de desenvolvimento. O sistema oferece uma **API RESTful robusta** construída em Java com Spring Boot, complementada por uma **interface web responsiva** para administração da plataforma.

## 🏛️ Padrões e Boas Práticas

- **Arquitetura MVC** com separação clara de responsabilidades
- **Design Patterns** aplicados (Repository, Service Layer, DTO)
- **Validação de Dados** com Bean Validation
- **Tratamento de Exceções** globalmente
- **Migrations de Banco** com Flyway para controle de versão
- **Clean Code** seguindo convenções Java e Spring Boot

[Voltar ao topo](#readme-top)

## 🎥 Youtube

Apresentação do projeto no Youtube: https://www.youtube.com/watch?v=qkSbIB2PRp8

[Voltar ao topo](#readme-top)


## 🛠️ Tecnologias Utilizadas

- Spring Boot
- Spring Web
- Spring Security
- Java 17+
- Maven
- SQL Server

[Voltar ao topo](#readme-top)

## 💻 Inicializar projeto

Para iniciar o projeto faz se necessário seguir algumas etapas abaixo:

### 📝 Pré-requisitos

- Java 17+
- Maven 3.1.0+
- IDE (como IntelliJ, Eclipse ou VS Code)
- Acesso a uma instância do SQL Server

### 🗃️ Instalação
1. Clone o repositório para a sua pasta:
    ```cmd
    git clone https://github.com/P3terHenry/java-find-mottu.git
    cd java-find-mottu
    ```
2. Copie o arquivo de exemplo de variáveis de ambiente e atualize-o com suas credenciais e URL do SQL Server:
    ```cmd
    copy .env.example .env
    ```
   - Abra o arquivo ` .env` e ajuste as variáveis de conexão (ver seção "Configuração do Banco de Dados" abaixo).

3. Build e execução (opções):
   - Usando o wrapper no Windows (cmd):
     ```cmd
     .\mvnw.cmd clean package
     java -jar target\find-mottu-1.0.0.jar
     ```
     Observação: substitua `find-mottu-1.0.0.jar` pelo nome do JAR gerado em `target`.

   - Ou executar diretamente pela IDE: execute a classe anotada com `@SpringBootApplication`.

4. Acesse o Swagger para testar os endpoints da API:
   ```text
   http://localhost:8080/swagger-ui/index.html
   ```
5. Acesse a interface web para administração:
   ```link
   http://localhost:8080/
   ```

### 🗄️ Acesso ao Banco de Dados
O projeto foi adaptado para uso com SQL Server. Não utiliza mais o console H2 por padrão.

[Voltar ao topo](#readme-top)

## ⚙️ Configuração do Banco de Dados (SQL Server)
Edite o arquivo ` .env` criado a partir de ` .env.example` e preencha as variáveis abaixo:

- `SPRING_DATASOURCE_URL` — exemplo:
  ```text
  jdbc:sqlserver://<HOST>:1433;databaseName=<NOME_DO_BANCO>
  ```
- `SPRING_DATASOURCE_USERNAME` — usuário do banco
- `SPRING_DATASOURCE_PASSWORD` — senha do usuário
- `SPRING_DATASOURCE_DRIVER` — use:
  ```text
  com.microsoft.sqlserver.jdbc.SQLServerDriver
  ```
- `SPRING_JPA_DATABASE_PLATFORM` — exemplo:
  ```text
  org.hibernate.dialect.SQLServerDialect
  ```
- `SPRING_JPA_HIBERNATE_DDL_AUTO` — valores comuns: `validate`, `update`, `none` (evitar `create` em produção)

Observação: entradas relacionadas ao H2 (se presentes) podem permanecer comentadas no exemplo, mas o comportamento do projeto pressupõe SQL Server.

[Voltar ao topo](#readme-top)

## 🔒 Segurança
Autenticação e controle de acesso implementados com Spring Security.

## 🧪 Testes
- Executar testes unitários:
  ```cmd
  .\mvnw.cmd test
  ```

## 📦 Dependências importantes
- Driver JDBC do SQL Server (ver `pom.xml`):
  ```xml
  <dependency>
    <groupId>com.microsoft.sqlserver</groupId>
    <artifactId>mssql-jdbc</artifactId>
    <version>11.2.1.jre17</version>
  </dependency>
  ```
