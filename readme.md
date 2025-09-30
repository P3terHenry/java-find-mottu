<a id="readme-top"></a>

# 📱 Challange - Mottu - Java API com Thymeleaf - Find Mottu

![Static Badge](https://img.shields.io/badge/build-passing-brightgreen) ![Static Badge](https://img.shields.io/badge/Version-1.0.3-black) ![License](https://img.shields.io/badge/license-MIT-lightgrey)

## 🧑‍🤝‍🧑 Informações dos Contribuintes

| Nome | Matricula | Turma |
| :------------: | :------------: | :------------: |
| Felipe Nogueira Ramon | 555335 | 2TDSPH |
| Pedro Herique Vasco Antonieti | 556253 | 2TDSPH |
<p align="right"><a href="#readme-top">Voltar ao topo</a></p>

## 🚩 Características

**Find Mottu** é uma solução completa de gestão de frota de motocicletas desenvolvida com tecnologias modernas e práticas de desenvolvimento. O sistema oferece uma **API RESTful robusta** construída em Java com Spring Boot, complementada por uma **interface web responsiva** para administração da plataforma.

### 🏛️ Padrões e Boas Práticas

- **Arquitetura MVC** com separação clara de responsabilidades
- **Design Patterns** aplicados (Repository, Service Layer, DTO)
- **Validação de Dados** com Bean Validation
- **Tratamento de Exceções** globalmente
- **Migrations de Banco** com Flyway para controle de versão
- **Clean Code** seguindo convenções Java e Spring Boot

<p align="right"><a href="#readme-top">Voltar ao topo</a></p>

## 🛠️ Tecnologias Utilizadas

![Apache Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![Swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)

<p align="right"><a href="#readme-top">Voltar ao topo</a></p>

## 💻 Inicializar projeto

Para iniciar o projeto faz se necessário seguir algumas etapas abaixo:

### 📝 Pré-requisitos

- Java 17+
- Maven 3.1.0
- IDE (como IntelliJ, Eclipse ou VS Code)

### 🗃️ Instalação
1. Clone o repositório para a sua pasta:
    ```sh
    git clone https://github.com/P3terHenry/java-find-mottu.git
    ```
2. Acesse a pasta onde você colocou seu projeto.
3. Copile e execute o projeto:
   ```sh
   ./mvnw spring-boot:run
   ```
4. Acesse o Swagger para testar os endpoints da API:
   ```link
   http://localhost:8080/swagger-ui/index.html
   ```
5. Acesse a interface web para administração:
   ```link
    http://localhost:8080/
    ```
### 🗄️ Acesso ao Banco de Dados
Projeto utiliza Banco H2, acesse via http://localhost:8080/h2-console.
<p align="right"><a href="#readme-top">Voltar ao topo</a></p>
