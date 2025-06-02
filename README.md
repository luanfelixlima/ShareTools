# ShareTools 🛠️

## Descrição 📝

ShareTools é uma plataforma baseada em Java projetada para facilitar o compartilhamento de ferramentas entre moradores de um condomínio. A plataforma permite que os usuários se cadastrem, listem suas ferramentas disponíveis para empréstimo e gerenciem esses empréstimos de forma eficiente. 🚀

## Funcionalidades ✨

* **Cadastro de Usuários:** Permite que novos moradores se registrem na plataforma. 👤
* **Login e Autenticação:** Garante que apenas usuários registrados acessem o sistema. 🔒
* **CRUD de Ferramentas:** Os usuários podem Adicionar, Visualizar, Atualizar e Deletar (CRUD) as ferramentas que desejam compartilhar. 🛠️
* **Gestão de Empréstimos:** Funcionalidades para solicitar, aprovar, registrar devolução e gerenciar o histórico de empréstimos. 📋
* **Regras de Negócio Definidas:** O sistema implementa regras específicas para o processo de compartilhamento e empréstimo. ⚙️
* **Dashboard Interativo:** Apresenta dados relevantes sobre as ferramentas e empréstimos de forma visual. 📊

## Tecnologias Utilizadas 💻

* **Java:** Linguagem principal de desenvolvimento. ☕
* **Padrão MVC (Model-View-Controller):** Arquitetura utilizada para organizar o código. 🏗️
* **Arquitetura em Camadas:** O projeto é estruturado com camadas distintas para front-end, repositório e banco de dados. 📂

## Estrutura do Projeto 📁

O projeto segue uma arquitetura em camadas, geralmente organizada da seguinte forma:

* **src/main/java:** Contém o código fonte da aplicação.
    * **com.example.sharetools.controller:** Controladores responsáveis por lidar com as requisições da interface do usuário.
    * **com.example.sharetools.model:** Classes de modelo que representam os dados da aplicação (ex: Usuário, Ferramenta, Empréstimo).
    * **com.example.sharetools.repository:** Classes responsáveis pela persistência e acesso aos dados (interação com o banco de dados).
    * **com.example.sharetools.service:** Lógica de negócio da aplicação.
* **src/main/resources:** Arquivos de configuração, templates (se aplicável), e outros recursos.
* **src/test/java:** Testes unitários e de integração.
* **pom.xml (ou build.gradle):** Arquivo de configuração do Maven (ou Gradle) para gerenciamento de dependências e build do projeto.

## Pré-requisitos ✅

* Java Development Kit (JDK) instalado (especificar a versão, por exemplo, JDK 11 ou superior).
* Maven ou Gradle instalado (especificar qual e a versão, se necessário).
* Um Sistema de Gerenciamento de Banco de Dados (SGBD) (especificar qual, por exemplo, PostgreSQL, MySQL, H2).

## Instalação e Execução 🚀

1.  **Clone o repositório:** 📥
    ```bash
    git clone [https://github.com/luanfelixlima/ShareTools.git](https://github.com/luanfelixlima/ShareTools.git)
    cd ShareTools
    ```

2.  **Configuração do Banco de Dados:**
    * Crie um banco de dados com o nome `sharetools_db` (ou o nome configurado no projeto).
    * Configure as credenciais do banco de dados no arquivo de propriedades da aplicação (geralmente `application.properties` ou `application.yml` em `src/main/resources`). Exemplo:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/sharetools_db
        spring.datasource.username=seu_usuario_bd
        spring.datasource.password=sua_senha_bd
        spring.jpa.hibernate.ddl-auto=update # ou create, dependendo da sua necessidade inicial
        ```

3.  **Compile o projeto (usando Maven como exemplo):**
    ```bash
    mvn clean install
    ```
    Ou, se estiver usando Gradle:
    ```bash
    ./gradlew build
    ```

4.  **Execute a aplicação (usando Maven como exemplo):**
    ```bash
    mvn spring-boot:run
    ```
    Ou, se estiver usando Gradle:
    ```bash
    ./gradlew bootRun
    ```
    Alternativamente, se um arquivo `.jar` executável foi gerado na pasta `target` (Maven) ou `build/libs` (Gradle):
    ```bash
    java -jar target/ShareTools-0.0.1-SNAPSHOT.jar
    ```

5.  **Acesse a aplicação:**
    Abra seu navegador e acesse `http://localhost:8080` (ou a porta configurada para sua aplicação).

## Como Contribuir 🤝

Contribuições são bem-vindas! Se você deseja contribuir com o projeto, siga os passos abaixo:

1.  **Faça um Fork** do projeto.
2.  Crie uma nova **Branch** para sua funcionalidade ou correção: `git checkout -b minha-nova-funcionalidade`
3.  Faça suas alterações e **Commits**: `git commit -m 'Adiciona nova funcionalidade'`
4.  Envie suas alterações para o seu fork: `git push origin minha-nova-funcionalidade`
5.  Abra um **Pull Request** para o repositório original.

Por favor, certifique-se de que seus commits estão bem descritos e que o código segue os padrões do projeto.

#

---
English version:
## Description 📝

ShareTools is a Java-based platform designed to facilitate the sharing of tools among condominium residents. 🏢 The platform allows users to register, list their tools available for lending, and manage these loans efficiently. 🚀

## Features ✨

- **User Registration:** Allows new residents to sign up on the platform. 👤
- **Login and Authentication:** Ensures only registered users can access the system. 🔒
- **Tool CRUD:** Users can Add, View, Update, and Delete (CRUD) the tools they wish to share. 🛠️
- **Loan Management:** Features to request, approve, record returns, and manage loan history. 📋
- **Defined Business Rules:** The system implements specific rules for the sharing and lending process. ⚙️
- **Interactive Dashboard:** Displays relevant data about tools and loans visually. 📊

## Technologies Used 💻

- **Java:** Main development language. ☕
- **MVC Pattern (Model-View-Controller):** Architecture used to organize the code. 🏗️
- **Layered Architecture:** The project is structured with distinct layers for front-end, repository, and database. 📂

## Project Structure 📁

The project follows a layered architecture, typically organized as follows:

- **src/main/java:** Contains the application’s source code.
    - **com.example.sharetools.controller:** Controllers responsible for handling user interface requests.
    - **com.example.sharetools.model:** Model classes representing the application’s data (e.g., User, Tool, Loan).
    - **com.example.sharetools.repository:** Classes responsible for data persistence and access (database interaction).
    - **com.example.sharetools.service:** Application business logic.
- **src/main/resources:** Configuration files, templates (if applicable), and other resources.
- **src/test/java:** Unit and integration tests.
- **pom.xml (or build.gradle):** Maven (or Gradle) configuration file for dependency management and project build.

## Prerequisites ✅

- Java Development Kit (JDK) installed (specify the version, e.g., JDK 11 or higher).
- Maven or Gradle installed (specify which and the version, if necessary).
- A Database Management System (DBMS) (specify which, e.g., PostgreSQL, MySQL, H2).

## Installation and Execution 🚀

1. **Clone the repository:** 📥
    ```bash
    git clone https://github.com/luanfelixlima/ShareTools.git
    cd ShareTools
    ```

2. **Database Configuration:** 🗄️
    - Create a database named `sharetools_db` (or the name configured in the project).
    - Configure the database credentials in the application properties file (usually `application.properties` or `application.yml` in `src/main/resources`). Example:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/sharetools_db
        spring.datasource.username=your_db_username
        spring.datasource.password=your_db_password
        spring.jpa.hibernate.ddl-auto=update # or create, depending on your initial needs
        ```

3. **Compile the project (using Maven as an example):** 🛠️
    ```bash
    mvn clean install
    ```
   Or, if using Gradle:
    ```bash
    ./gradlew build
    ```

4. **Run the application (using Maven as an example):** ▶️
    ```bash
    mvn spring-boot:run
    ```
   Or, if using Gradle:
    ```bash
    ./gradlew bootRun
    ```
   Alternatively, if an executable `.jar` file was generated in the `target` (Maven) or `build/libs` (Gradle) folder:
    ```bash
    java -jar target/ShareTools-0.0.1-SNAPSHOT.jar
    ```

5. **Access the application:** 🌐
   Open your browser and go to `http://localhost:8080` (or the port configured for your application).

## How to Contribute 🤝

Contributions are welcome! If you’d like to contribute to the project, follow the steps below:

1. **Fork the project.** 🍴
2. Create a new **Branch** for your feature or fix: `git checkout -b my-new-feature` 🌿
3. Make your changes and **Commits**: `git commit -m 'Add new feature'` 💾
4. Push your changes to your fork: `git push origin my-new-feature` 🚀
5. Open a **Pull Request** to the original repository. 📬

Please ensure your commits are well-described and the code follows the project’s standards.
