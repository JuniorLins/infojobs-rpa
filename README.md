# 🤖 InfoJobs RPA - Candidatura Automática

> Um robô automatizado (RPA) desenvolvido em Java com Spring Boot para buscar e se candidatar automaticamente a vagas no InfoJobs. O sistema gerencia usuários, credenciais criptografadas, logs de execução e expõe uma API REST para monitoramento e controle.

[![Java](https://img.shields.io/badge/Java-17-blue)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.3-green)](https://spring.io/projects/spring-boot)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue)](https://www.docker.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)](https://www.postgresql.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow)](LICENSE)

---

## 📑 Índice

- [✨ Funcionalidades](#-funcionalidades)
- [🛠️ Tecnologias Utilizadas](#️-tecnologias-utilizadas)
- [📂 Estrutura do Projeto](#-estrutura-do-projeto)
- [🚀 Como Executar](#-como-executar)
    - [Pré-requisitos](#pré-requisitos)
    - [Executando com Docker Compose](#executando-com-docker-compose)
    - [Executando localmente (IntelliJ)](#executando-localmente-intellij)
- [🔐 Variáveis de Ambiente](#-variáveis-de-ambiente)
- [📡 API REST](#-api-rest)
- [🧩 Arquitetura e Segurança](#-arquitetura-e-segurança)
- [📈 Monitoramento (Futuro)](#-monitoramento-futuro)
- [🤝 Contribuição](#-contribuição)
- [📄 Licença](#-licença)

---

## ✨ Funcionalidades

- ✅ **Login automático** no InfoJobs com credenciais seguras.
- ✅ **Busca de vagas** com múltiplos termos de fallback.
- ✅ **Candidatura automática** a vagas encontradas.
- ✅ **Persistência** das vagas processadas no PostgreSQL.
- ✅ **Gerenciamento de usuários** (ADMIN / CANDIDATE) com autenticação JWT.
- ✅ **Segurança:** Criptografia AES para senhas do InfoJobs e BCrypt para senhas do sistema.
- ✅ **Execução agendada** via Spring Scheduler (08:00, 12:00 e 18:00).
- ✅ **API REST** para iniciar o robô e gerenciar usuários.
- ✅ **Containerizado** com Docker para fácil deploy.

---

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17**
- **Spring Boot 4.0.3**
- **Spring Security + JWT**
- **Spring Data JPA / Hibernate**
- **PostgreSQL 15**
- **Selenium + WebDriverManager**
- **Lombok**
- **JUnit 5**

### DevOps & Infraestrutura
- **Maven**
- **Docker / Docker Compose**
- **Git**

---

## 📂 Estrutura do Projeto
infojobs/
├── src/
│ └── main/
│ ├── java/com/rpa/infojobs/
│ │ ├── config/ # Configurações (Security, WebDriver, ThreadPool)
│ │ ├── controller/ # Endpoints REST (Auth, User, Robot)
│ │ ├── dto/ # Dados de transferência (Request/Response)
│ │ ├── model/ # Entidades JPA (User, Vaga)
│ │ ├── repository/ # Interfaces JPA
│ │ ├── robot/ # Lógica do RPA (InfojobsRobot, páginas Selenium)
│ │ ├── scheduler/ # Tarefas agendadas (RobotScheduler)
│ │ ├── security/ # JWT, AES Encryption
│ │ ├── service/ # Regras de negócio
│ │ └── tools/ # Utilitários de manutenção (AtualizarSenhaAES)
│ └── resources/
│ └── application.properties
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md

text

---

## 🚀 Como Executar

### Pré-requisitos

- **Docker** e **Docker Compose** instalados.
- **Java 17** (para execução local).
- **Maven** (para build local).

### Executando com Docker Compose (Recomendado)

1. Clone o repositório e acesse a pasta:

```bash
git clone https://github.com/seu-usuario/infojobs-rpa.git
cd infojobs-rpa

2. Compile o projeto e gere o JAR:

bash
mvn clean package -DskipTests

3. Construa a imagem da aplicação:

bash
docker build -t infojobs-app .
4. Suba todos os serviços:

bash
docker-compose up -d

5. Acesse a aplicação:

API: http://localhost:8080

Adminer (Gerenciador do Banco): http://localhost:8081

Para acompanhar os logs do robô:

bash
docker logs -f infojobs-app

Executando localmente (IntelliJ)
Configure o PostgreSQL e atualize o application.properties com o IP e credenciais corretas.

Execute a classe InfojobsApplication ou RobotMain.

A API estará disponível em http://localhost:8080.

🔐 Variáveis de Ambiente
Variável	Descrição	Valor Padrão
JWT_SECRET	Chave secreta para assinatura do JWT	404E6352...
INFOJOBS_AES_KEY	Chave AES para criptografar senhas do InfoJobs	0123456789...
SPRING_DATASOURCE_URL	URL de conexão com o PostgreSQL	jdbc:postgresql://postgres:5432/infojobs_rpa
SPRING_DATASOURCE_USERNAME	Usuário do banco	infojobs_user
SPRING_DATASOURCE_PASSWORD	Senha do banco	infojobs_pass123
📡 API REST
🔐 Autenticação
Login (gerar token JWT)

text
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@sistema.com",
  "password": "admin123"
}
🧑‍💻 Usuários
Criar usuário (Admin only)

text
POST /api/users
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "email": "user@exemplo.com",
  "password": "senha123",
  "name": "Usuario",
  "infojobsEmail": "info@email.com",
  "infojobsPassword": "senhaInfojobs",
  "role": "CANDIDATE"
}
Listar usuários

text
GET /api/users
Authorization: Bearer <JWT_TOKEN>
🤖 Robô
Executar robô para um usuário específico

text
POST /api/robot/executar?userId=UUID_DO_USUARIO
Authorization: Bearer <JWT_TOKEN>
🧩 Arquitetura e Segurança
AES-256 para senhas do InfoJobs (reversível) – necessário para o Selenium logar.

BCrypt para senhas do sistema (irreversível) – usado na autenticação JWT.

Spring Security + JWT Stateless – protege todos os endpoints, exceto /api/auth/**.

WebDriver com @Scope("prototype") – evita conflitos entre execuções simultâneas.

Semaphore – garante que apenas uma execução do robô ocorra por vez.

📈 Monitoramento (Futuro)
Está prevista a integração com:

Prometheus para coleta de métricas.

Grafana para dashboards de monitoramento.

Métricas personalizadas: vagas processadas, candidaturas realizadas, tempo de execução.

🤝 Contribuição
Contribuições são bem-vindas! Siga os passos:

Fork o projeto.

Crie uma branch para sua feature (git checkout -b feature/nova-feature).

Commit suas mudanças (git commit -m 'Adiciona nova feature').

Push para a branch (git push origin feature/nova-feature).

Abra um Pull Request.

📄 Licença
Este projeto está sob a licença MIT. Veja o arquivo LICENSE para mais detalhes.

👨‍💻 Autor
Desenvolvido com ❤️ por juniorjblins e orientado por um Engenheiro de Software Sênior com 15+ anos de experiência.

📞 Contato
Junior Lins - https://www.linkedin.com/in/junior-lins-linsjunior/

