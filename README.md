# 🛠️ ETC — Plataforma de Conexão de Mão de Obra Autônoma

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)
![Maven](https://img.shields.io/badge/Apache%20Maven-C71A36?style=for-the-badge&logo=Apache%20Maven&logoColor=white)
![Lombok](https://img.shields.io/badge/Project%20Lombok-bc002d?style=for-the-badge&logo=lombok&logoColor=white)
![Log4j2](https://img.shields.io/badge/Apache%20Log4j2-2C2255?style=for-the-badge&logo=apache&logoColor=white)

> **Rework em Java** do projeto de TCC (Etec), desenvolvido com foco em arquitetura backend nativa, JDBC puro, boas práticas de Orientação a Objetos e regras de negócio complexas.

---

## 📌 Sobre o Projeto

A ideia do **ETC** nasceu de um problema real: a grande dependência do formato "Quem Indica" (Q.I.) no mercado de trabalho informal e autônomo (como pedreiros, eletricistas e pintores), o que dificulta o acesso contínuo a novas oportunidades de trabalho mesmo para profissionais com décadas de experiência.

A plataforma atua como um centralizador inteligente entre prestadores de serviço e clientes, automatizando a recomendação, o envio de solicitações e a gestão de propostas de trabalho.

---

## ⚙️ Principais Funcionalidades & Regras de Negócio

* **Perfil Flexível:**
  * **Profissionais:** Cadastro de perfis com definição de especialidades técnicas (*tags* de profissão), localização e dados cadastrais.
  * **Clientes:** Publicação de ordens de serviço/projetos com requisitos específicos e localização.
* **Match Making Inteligente:**
  * Algoritmo de recomendação de profissionais baseado em **distância geográfica**, **compatibilidade de tags/profissões**, **histórico de serviços** e **avaliações**.
* **Gestão de Soluções e Propostas (*Match Requests*):**
  * Um cliente pode enviar solicitações de serviço (*Match Requests*) para múltiplos profissionais simultaneamente para um mesmo projeto.
  * **Concorrência e Exclusividade:** O primeiro profissional a aceitar a solicitação assume o serviço. Automaticamente, as demais solicitações associadas àquele projeto são trancadas.
  * **Bloqueio de Agenda:** Ao aceitar uma demanda, o profissional fica com o status ocupado até a conclusão e entrega do serviço, prevenindo *overbooking*.
* **Módulo de Avaliações:**
  * Sistema de *feedback* e notas (*Avaliation/Review*) para gerar histórico de reputação e métricas de confiança na plataforma.

---

## 🛠️ Tecnologias & Conceitos Aplicados

* **Linguagem:** Java 22 (Java SE / Nativo sem frameworks Web ou ORMs, focando nos fundamentos da linguagem)
* **Persistência de Dados:** JDBC Nativo (`PreparedStatement`, `ResultSet`, gerenciamento de transações e suporte ao PostgreSQL)
* **Design Patterns & Arquitetura:**
  * **DAO / Repository Pattern:** Isola totalmente a lógica de acesso a dados.
  * **DTO Pattern:** Transporte limpo e encapsulado de dados entre camadas (`PersonWithUserDTO`, `EmployeePersonDTO`, `AddressDTO`, etc.).
  * **Factory Pattern:** Gerenciamento centralizado de conexões em `ConnectionFactory` com *try-with-resources*.
* **Interface & Interatividade:** Console / CLI interativo via `Controllers` com fluxos de navegação e filtros.
* **Infraestrutura & Produtividade:**
  * **Docker & Docker Compose:** Containerização do banco de dados PostgreSQL 
  * **Lombok & Log4j2:** Redução de código *boilerplate* e registro estruturado de logs.
  * **Maven:** Gerenciamento de dependências (`pom.xml`).

---

## 🚀 Como Executar o Projeto

### Pré-requisitos
* **Java SDK 22** ou superior instalado
* **Docker** e **Docker Compose** configurados

### Passo a Passo

1. **Clonar o repositório:**
   ```bash
   git clone [https://github.com/UmVitorAleatorio/Projeto-ETC-Java.git](https://github.com/UmVitorAleatorio/Projeto-ETC-Java.git)
   cd Projeto-ETC-Java
