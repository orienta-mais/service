# Orienta+ - Plataforma de Mentoria

<p align="center">
  <img src="src/main/resources/static/Logo.webp" alt="Orienta+ Logo" width="200"/>
</p>

<p align="center">
  <strong>Conectando mentores e mentorados para um aprendizado transformador</strong>
</p>

<p align="center">
  <a href="#sobre-o-projeto">Sobre</a> |
  <a href="#arquitetura">Arquitetura</a> |
  <a href="#tecnologias">Tecnologias</a> |
  <a href="#instalação">Instalação</a> |
  <a href="#endpoints-da-api">Endpoints</a> |
  <a href="#estrutura-do-projeto">Estrutura</a>
</p>

---

## Sobre o Projeto

### O que é o Orienta+?

O **Orienta+** é uma plataforma completa de mentoria que conecta mentores experientes com pessoas que buscam orientação profissional e acadêmica. O sistema oferece um ecossistema robusto para agendamento de aulas, gestão de certificados, avaliações de mentores e muito mais.

### Para quem é destinado?

| Perfil | Descrição |
|--------|-----------|
| **Mentores** | Profissionais experientes que desejam compartilhar conhecimento e orientar novos profissionais |
| **Mentorados** | Estudantes e profissionais em busca de orientação, networking e desenvolvimento de carreira |
| **Administradores** | Gestores da plataforma responsáveis por estatísticas, termos de uso e moderação |

### Problema que Resolve

O Orienta+ resolve a lacuna de conexão entre profissionais experientes e pessoas em busca de orientação, oferecendo:

- Agendamento facilitado de sessões de mentoria
- Integração com Zoom para reuniões virtuais automáticas
- Integração com calendário para convites automáticos (ICS)
- Geração de certificados de participação com validação de presença
- Sistema de avaliações para mentores
- Gestão de termos de uso e política de privacidade versionados

---

## Arquitetura do Sistema

### Visão Geral

O Orienta+ foi desenvolvido seguindo os princípios da **Arquitetura Hexagonal (Ports and Adapters)**, garantindo alta testabilidade, manutenibilidade e desacoplamento entre camadas.

### Diagrama de Arquitetura

<p align="center">
  <img src="src/main/resources/static/Diagrama de arquitetura - OrientaMais.png" alt="Diagrama de Arquitetura" width="800"/>
</p>

---

### Fluxo Principal de Dados

1. **Request HTTP** → Controlador REST recebe a requisição
2. **Controlador** → Valida DTOs e chama o Use Case (Input Port)
3. **Service** → Executa regras de negócio e usa Output Ports
4. **Adapters** → Persistem dados, enviam emails, criam reuniões
5. **Response** → Dados são mapeados e retornados ao cliente

---

## Tecnologias Utilizadas

### Linguagens e Frameworks

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **Java** | 21 | Linguagem principal |
| **Spring Boot** | 3.4.1 | Framework principal |
| **Spring Security** | 6.x | Autenticação e autorização |
| **Spring Data JPA** | 3.x | Persistência de dados |
| **Spring Mail** | 3.x | Envio de emails |

### Banco de Dados e Migração

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **PostgreSQL** | Latest | Banco de dados relacional |
| **Flyway** | 11.10.5 | Versionamento de banco de dados |
| **Hibernate** | 6.x | ORM |

### Segurança e Autenticação

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **JWT (Auth0)** | 4.4.0 | Tokens de autenticação |
| **BCrypt** | - | Hash de senhas |
| **OWASP HTML Sanitizer** | 20240325.1 | Sanitização contra XSS |

### Integrações Externas

| Serviço | Descrição |
|---------|-----------|
| **Zoom API** | Criação automática de reuniões |
| **SMTP (Gmail)** | Envio de emails transacionais |
| **ICS Calendar** | Convites de calendário |

### Utilitários

| Tecnologia | Versão | Descrição |
|------------|--------|-----------|
| **Lombok** | - | Redução de boilerplate |
| **ModelMapper** | 3.2.0 | Mapeamento de objetos |
| **iTextPDF** | 5.5.13.4 | Geração de certificados PDF |
| **Google ZXing** | 3.5.3 | Geração de QR Codes |
| **SpringDoc OpenAPI** | 2.8.8 | Documentação da API |

### Qualidade de Código

| Ferramenta | Descrição |
|------------|-----------|
| **Spotless** | Formatação automática (Google Java Format) |
| **Checkstyle** | Validação de estilo de código |
| **JaCoCo** | Cobertura de testes |
| **SonarQube** | Análise estática de código |

---

## Pré-requisitos

Antes de começar, certifique-se de ter instalado:

| Requisito | Versão Mínima | Comando para Verificar |
|-----------|---------------|------------------------|
| **Java JDK** | 21 | `java --version` |
| **Maven** | 3.8+ | `mvn --version` |
| **Docker** | 20.10+ | `docker --version` |
| **Docker Compose** | 2.0+ | `docker compose version` |
| **PostgreSQL** | 14+ | Ou via Docker |

### Variáveis de Ambiente Necessárias

Para produção, configure as seguintes variáveis:

```properties
# Banco de Dados
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/orienta
SPRING_DATASOURCE_USERNAME=orienta_user
SPRING_DATASOURCE_PASSWORD=orienta_pass

# JWT
JWT_SECRET=sua-chave-secreta-base64
JWT_EXPIRATION_MS=900000
JWT_REFRESH_EXPIRATION_MS=604800000

# Email
SPRING_MAIL_USERNAME=seu-email@gmail.com
SPRING_MAIL_PASSWORD=sua-app-password

# Zoom API
ZOOM_ACCOUNT_ID=seu-account-id
ZOOM_CLIENT_ID=seu-client-id
ZOOM_CLIENT_SECRET=seu-client-secret

# URLs da Aplicação
APP_REGISTRATION_URL=https://seu-dominio.com/register/
APP_RESET_PASSWORD_URL=https://seu-dominio.com/reset-password
ORIGINS_WEBAPP=https://seu-frontend.com
```

---

## Instalação e Configuração

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/orientamais.git
cd orientamais
```

### 2. Configurar o Banco de Dados

#### Opção A: Via Docker (Recomendado)

```bash
docker compose up -d postgres
```

Isso criará o banco `orienta` com:
- **Usuário**: `orienta_user`
- **Senha**: `orienta_pass`
- **Porta**: `5432`

#### Opção B: PostgreSQL Local

```sql
CREATE DATABASE orienta;
CREATE USER orienta_user WITH ENCRYPTED PASSWORD 'orienta_pass';
GRANT ALL PRIVILEGES ON DATABASE orienta TO orienta_user;
```

### 3. Configurar Aplicação

O projeto utiliza perfis Spring. Para desenvolvimento, use `application-dev.properties`:

```bash
# O perfil dev é ativado por padrão em application.properties
spring.profiles.active=dev
```

### 4. Executar Migrações

As migrações Flyway são executadas automaticamente na inicialização.

Para executar manualmente:

```bash
./mvnw flyway:migrate
```

---

## Como Rodar o Projeto

### Via Maven (Desenvolvimento)

```bash
# Compilar e rodar
./mvnw spring-boot:run

# Ou em dois passos
./mvnw clean package -DskipTests
java -jar target/orientamais-0.0.1-SNAPSHOT.jar
```

### Via IDE (IntelliJ IDEA / VS Code)

1. Importe o projeto como Maven Project
2. Aguarde a indexação das dependências
3. Execute a classe `OrientamaisApplication.java`

### Via Docker

```bash
# Subir apenas o banco
docker compose up -d postgres

# Construir e rodar a aplicação
./mvnw spring-boot:run
```

### Verificar se Está Rodando

```bash
curl http://localhost:8080/swagger-ui.html
# Ou acesse no navegador: http://localhost:8080/swagger-ui.html
```

---

## Como Rodar os Testes

### Testes Unitários

```bash
./mvnw test
```

### Testes de Integração

```bash
./mvnw verify
```

### Relatório de Cobertura (JaCoCo)

```bash
./mvnw test jacoco:report
# Relatório gerado em: target/site/jacoco/index.html
```

### Verificação de Estilo

```bash
# Verificar formatação
./mvnw spotless:check

# Aplicar formatação automática
./mvnw spotless:apply

# Verificar Checkstyle
./mvnw checkstyle:check
```

---

## Estrutura do Projeto

```
src/main/java/umc/pfc/orientamais/
|
+-- adapters/                             # Camada de Adaptadores (Hexagonal)
|   +-- input/rest/                       # Adaptadores de Entrada
|   |   +-- controller/                   # Controladores REST
|   |   |   +-- Auth.java                 # Autenticação (login, refresh, reset)
|   |   |   +-- Lesson.java               # CRUD de Aulas
|   |   |   +-- Mentor.java               # CRUD de Mentores
|   |   |   +-- Mentored.java             # CRUD de Mentorados
|   |   |   +-- Stats.java                # Estatísticas (admin)
|   |   |   +-- TermsController.java      # Termos e Privacidade
|   |   |   +-- GlobalExceptionHandler.java
|   |   +-- converter/                    # Conversores de tipos
|   |   +-- dto/                          # Data Transfer Objects
|   |       +-- request/                  # DTOs de entrada
|   |       +-- response/                 # DTOs de saída
|   |
|   +-- output/                           # Adaptadores de Saída
|       +-- calendar/                     # Integração com Calendário (ICS)
|       +-- email/                        # Envio de Emails (SMTP)
|       +-- persistence/                  # Repositórios JPA
|       |   +-- repository/               # Interfaces de Repository
|       +-- sanitization/                 # Sanitização HTML (OWASP)
|       +-- zoom/                         # Integração com Zoom API
|
+-- application/                          # Camada de Aplicação
|   +-- mapper/                           # Mapeadores Entity <-> DTO
|   |   +-- LessonMapper.java
|   |   +-- MentorMapper.java
|   |   +-- MentoredMapper.java
|   |   +-- MentorReviewMapper.java
|   +-- port/                             # Portas (Interfaces)
|   |   +-- input/                        # Portas de Entrada (Use Cases)
|   |   |   +-- LessonUseCase.java
|   |   |   +-- MentorUseCase.java
|   |   |   +-- LoginUseCase.java
|   |   |   +-- ...
|   |   +-- output/                       # Portas de Saída
|   |       +-- calendar/                 # CalendarPort
|   |       +-- email/                    # EmailPort
|   |       +-- sanitization/             # SanitizationPort
|   |       +-- zoom/                     # ZoomPort
|   +-- service/                          # Implementações dos Use Cases
|       +-- LessonService.java
|       +-- MentorService.java
|       +-- LoginService.java
|       +-- CertificateService.java
|       +-- email/                        # Serviço de Email
|       +-- scheduler/                    # Jobs Agendados
|       +-- utils/                        # Utilitários
|
+-- config/                               # Configurações
|   +-- infraestructure/                  # Beans de infraestrutura
|   +-- security/                         # Segurança
|       +-- SecurityConfig.java           # Configuração Spring Security
|       +-- CorsConfig.java               # Configuração CORS
|       +-- jwt/                          # JWT Filter e Provider
|
+-- domain/                               # Camada de Domínio (Core)
    +-- exceptions/                       # Exceções de Negócio
    |   +-- NotFoundException.java
    |   +-- BadRequestException.java
    |   +-- ...
    +-- model/                            # Entidades JPA
    |   +-- auth/                         # AuthUser, RefreshToken, etc.
    |   +-- clazz/                        # Lesson, Certificate, etc.
    |   +-- mentor/                       # Mentor, MentorReview, etc.
    |   +-- mentored/                     # Mentored, MentoredInterest
    |   +-- terms/                        # TermsAndPrivacy
    +-- utils/                            # Utilitários de domínio
    +-- validation/                       # Validações customizadas
        +-- SafeInput.java                # Validação anti-injection
        +-- ValidAge.java                 # Validação de idade
```

### Estrutura de Recursos

```
src/main/resources/
|
+-- application.properties                # Configurações gerais
+-- application-dev.properties            # Configurações de desenvolvimento
|
+-- db/migration/                         # Migrações Flyway
|   +-- V1__init_schema.sql               # Schema inicial
|   +-- V2__create_registration_token.sql
|   +-- V3__create_refresh_token.sql
|   +-- ...
|   +-- V16__change_mentor_user_fk.sql
|
+-- static/                               # Arquivos estáticos
|   +-- logo.png                          # Logo da aplicação
|   +-- certificado_assinatura.p12        # Certificado para PDFs
|   +-- assinatura_orienta.png            # Assinatura para certificados
|
+-- templates/                            # Templates de Email
    +-- mentor_register.html
    +-- password_changed.html
    +-- password_reset.html
```

---

## Endpoints da API

### Documentação Interativa

Acesse a documentação completa via Swagger:
- **URL**: `http://localhost:8080/swagger-ui.html`
- **API Docs**: `http://localhost:8080/v3/api-docs`

---

## Entidades e Modelos

### Diagrama ER Simplificado

```
┌─────────────────┐       ┌─────────────────┐       ┌─────────────────┐
│    auth_user    │       │     mentor      │       │    mentored     │
├─────────────────┤       ├─────────────────┤       ├─────────────────┤
│ id (PK)         │       │ id (PK)         │       │ id (PK)         │
│ email           │◄──────│ user_uuid (FK)  │       │ user_uuid (FK)  │──────►│
│ password        │       │ name            │       │ name            │
│ role            │       │ lastName        │       │ lastName        │
│ termsAccepted   │       │ birthDate       │       │ birthDate       │
└─────────────────┘       │ active          │       │ description     │
                          └────────┬────────┘       └────────┬────────┘
                                   │                         │
                                   │                         │
                          ┌────────▼────────┐       ┌────────▼────────┐
                          │      class      │       │  class_mentored │
                          ├─────────────────┤       ├─────────────────┤
                          │ id (PK)         │◄──────│ class_id (FK)   │
                          │ title           │       │ mentored_id (FK)│
                          │ description     │       │ certificateGen  │
                          │ link (Zoom)     │       └─────────────────┘
                          │ mentor_id (FK)  │
                          │ start_time      │
                          │ end_time        │
                          │ status          │
                          │ present_code    │
                          └─────────────────┘
                                   │
                          ┌────────▼────────┐
                          │  mentor_review  │
                          ├─────────────────┤
                          │ id (PK)         │
                          │ mentor_id (FK)  │
                          │ mentored_id (FK)│
                          │ didactics       │
                          │ punctuality     │
                          │ communication   │
                          │ feedback        │
                          └─────────────────┘
```

### Descrição das Entidades

| Entidade | Descrição |
|----------|-----------|
| **AuthUser** | Dados de autenticação (email, senha, role, aceite de termos) |
| **Mentor** | Perfil do mentor com informações pessoais e profissionais |
| **Mentored** | Perfil do mentorado com informações pessoais |
| **Lesson (class)** | Aulas/sessões de mentoria agendadas |
| **LessonMentored** | Relação N:N entre aulas e mentorados inscritos |
| **MentorReview** | Avaliações dos mentores pelos mentorados |
| **Certificate** | Certificados de participação gerados |
| **TermsAndPrivacy** | Termos de uso e política de privacidade versionados |
| **RefreshToken** | Tokens de renovação de sessão |
| **RegistrationToken** | Tokens de validação de email para registro |
| **PasswordResetToken** | Tokens para redefinição de senha |

---

## Configurações Importantes

### application.properties

```properties
# Perfil ativo
spring.profiles.active=dev

# Timezone UTC para JSON
spring.jackson.time-zone=UTC
spring.jackson.serialization.write-dates-as-timestamps=false

# JPA/Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.jdbc.time_zone=UTC

# Flyway
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true

# CORS
origins.webapp=http://localhost:4200

# URLs da aplicação
app.registration.url=http://localhost:4200/register/
app.reset-password.url=http://localhost:4200/reset-password
app.reset-password.ttl-seconds=86400
```

### Perfis de Execução

| Perfil | Uso | Arquivo |
|--------|-----|---------|
| **dev** | Desenvolvimento local | `application-dev.properties` |
| **prod** | Produção | `application-prod.properties` (criar) |
| **test** | Testes automatizados | `application-test.properties` (criar) |

---

## Guia para Desenvolvedores

### Como Contribuir

1. **Fork** o repositório
2. Crie uma **branch** para sua feature: `git checkout -b feature/nova-feature`
3. Faça o **commit**: `git commit -m 'feat: adiciona nova feature'`
4. **Push** para a branch: `git push origin feature/nova-feature`
5. Abra um **Pull Request**

### Padrões de Commit

Seguimos o [Conventional Commits](https://www.conventionalcommits.org/):

| Tipo | Descrição |
|------|-----------|
| `feat` | Nova funcionalidade |
| `fix` | Correção de bug |
| `docs` | Documentação |
| `style` | Formatação de código |
| `refactor` | Refatoração |
| `test` | Testes |
| `chore` | Configurações e tarefas |

### Adicionando Novas Funcionalidades

1. **Defina o Use Case** em `application/port/input/`
2. **Implemente o Service** em `application/service/`
3. **Crie o Controlador** em `adapters/input/rest/controller/`
4. **Defina DTOs** em `adapters/input/rest/dto/`
5. **Implemente Adapters** de saída se necessário
6. **Adicione Migrações** em `resources/db/migration/`
7. **Escreva Testes** unitários e de integração

### Padrões de Código

- **Google Java Format** (aplicado via Spotless)
- **Nomes em inglês** para código
- **Comentários em português** quando necessário
- **Lombok** para reduzir boilerplate
- **Records** para DTOs imutáveis
- **Validações** via Bean Validation

---

## Guia para Usuários Finais

### Para Mentores

1. **Registre-se** informando seu email
2. Confirme seu email clicando no link recebido
3. Faça **login** na plataforma
4. **Crie aulas** informando tema, data, horário e descrição
5. **Acompanhe inscrições** em suas aulas
6. **Forneça o código de presença** aos participantes ao final

### Para Mentorados

1. **Registre-se** informando seu email
2. Confirme seu email clicando no link recebido
3. Faça **login** na plataforma
4. **Explore aulas** disponíveis
5. **Inscreva-se** nas aulas de seu interesse
6. **Participe** via link Zoom enviado
7. **Valide presença** com o código fornecido pelo mentor
8. **Receba seu certificado** por email
9. **Avalie o mentor** após a sessão

---

## Erros Comuns e Soluções

| Erro | Causa | Solução |
|------|-------|---------|
| `Connection refused: localhost:5432` | PostgreSQL não está rodando | Execute `docker compose up -d postgres` |
| `Invalid JWT token` | Token expirado ou inválido | Faça login novamente ou use refresh token |
| `NotFoundException: Mentor não encontrado` | UUID inválido | Verifique se o ID está correto |
| `VALIDATION_ERROR` | Dados de entrada inválidos | Verifique os campos obrigatórios |
| `Flyway migration failed` | Conflito de migração | Verifique o banco ou execute `flyway:repair` |
| `Email sending failed` | Configuração SMTP incorreta | Verifique credenciais do email |

---

## Licença

Este projeto está sob a licença **MIT**. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## Autores

Desenvolvido pela equipe **Orienta+** - Projeto Final de Curso (PFC)

**Universidade de Mogi das Cruzes (UMC)**

---

<p align="center">
  <strong>Orienta+</strong> - Transformando carreiras através da mentoria
</p>

