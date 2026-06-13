# 📁 ESTRUTURA DE DIRETÓRIOS - Travel Graph API Reestruturada

## Árvore Completa do Projeto

```
travel-graph-api/
│
├── pom.xml                                 (Configuração Maven)
├── mvnw / mvnw.cmd                         (Maven Wrapper)
│
├── 📖 Documentação
│   ├── README.md                           (Documentação original)
│   ├── RESTRUCTURED.md                     (✨ NOVO - Guia de reestruturação)
│   ├── CHANGELOG_RESTRUCTURE.md            (✨ NOVO - Mudanças detalhadas)
│   ├── TESTING_GUIDE.md                    (✨ NOVO - Guia de testes)
│   └── FINAL_SUMMARY.md                    (✨ NOVO - Sumário executivo)
│
├── src/
│   │
│   ├── main/
│   │   │
│   │   ├── java/com/travelGraph/
│   │   │   │
│   │   │   ├── TravelGraphApplication.java (Classe principal Spring Boot)
│   │   │   │
│   │   │   ├── 🔐 authConfig/             (Configuração de segurança)
│   │   │   │   ├── CurrentUserAuthentication.java
│   │   │   │   ├── CustomAuthenticationProvider.java
│   │   │   │   ├── CustomAuthEntryPoint.java
│   │   │   │   ├── CustomFilter.java
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   └── TokenService.java
│   │   │   │
│   │   │   ├── 🎯 controller/             (Controllers REST)
│   │   │   │   ├── MainController.java
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── 📍 CityController.java         (CRUD Cidades)
│   │   │   │   ├── 🎭 AttractionController.java  (CRUD Pontos Turísticos)
│   │   │   │   ├── 🔗 ConexaoController.java     (✨ NOVO - Conexões)
│   │   │   │   ├── GraphController.java
│   │   │   │   │
│   │   │   │   └── exceptions/
│   │   │   │       ├── ResourceExceptionHandler.java
│   │   │   │       └── StandardError.java
│   │   │   │
│   │   │   ├── 📦 dto/                    (Data Transfer Objects)
│   │   │   │   ├── 📍 city/
│   │   │   │   │   ├── CityResponseDTO.java       (✏️ Simplificado)
│   │   │   │   │   ├── CreateCityRequestDTO.java  (✏️ Simplificado)
│   │   │   │   │   └── UpdateCityRequestDTO.java  (✏️ Simplificado)
│   │   │   │   │
│   │   │   │   ├── 🎭 attraction/
│   │   │   │   │   ├── AttractionResponseDTO.java       (✏️ Simplificado)
│   │   │   │   │   ├── CreateAttractionRequestDTO.java  (✏️ Simplificado)
│   │   │   │   │   └── UpdateAttractionRequestDTO.java  (✏️ Simplificado)
│   │   │   │   │
│   │   │   │   ├── 🔗 connection/        (✨ NOVO)
│   │   │   │   │   ├── ConexaoRequestDTO.java
│   │   │   │   │   └── ConexaoResponseDTO.java
│   │   │   │   │
│   │   │   │   ├── auth/
│   │   │   │   │   ├── LoginRequestDTO.java
│   │   │   │   │   ├── LoginResponseDTO.java
│   │   │   │   │   └── CurrentUserDTO.java
│   │   │   │   │
│   │   │   │   ├── user/
│   │   │   │   │   ├── UserResponseDTO.java
│   │   │   │   │   ├── CreateUserRequestDTO.java
│   │   │   │   │   └── UpdateUserRequestDTO.java
│   │   │   │   │
│   │   │   │   ├── route/
│   │   │   │   │   ├── ShortestPathResponseDTO.java
│   │   │   │   │   ├── RouteStepDTO.java
│   │   │   │   │   ├── NearbyLocationDTO.java
│   │   │   │   │   └── ItineraryDTO.java
│   │   │   │   │
│   │   │   │   └── connection/
│   │   │   │       └── CreateConnectionRequestDTO.java
│   │   │   │
│   │   │   ├── 🗂️ entities/              (Modelos Neo4j)
│   │   │   │   ├── 📍 CityNode.java          (✏️ Simplificado)
│   │   │   │   ├── 🎭 AttractionNode.java   (✏️ Simplificado)
│   │   │   │   ├── 🔗 CityConnection.java   (✏️ Atualizado)
│   │   │   │   ├── AttractionConnection.java
│   │   │   │   └── UserEntity.java
│   │   │   │
│   │   │   ├── 🔧 helpers/               (Utilitários)
│   │   │   │   ├── DateHelper.java
│   │   │   │   ├── UpdateValueHelper.java
│   │   │   │   └── WriteErrorResponse.java
│   │   │   │
│   │   │   ├── 📊 mapper/                (Conversores DTO/Entity)
│   │   │   │   ├── CityMapper.java       (✏️ Atualizado)
│   │   │   │   ├── AttractionMapper.java (✏️ Atualizado)
│   │   │   │   └── UserMapper.java
│   │   │   │
│   │   │   ├── 💾 repositories/          (Data Access Objects)
│   │   │   │   ├── 📍 CityRepository.java          (✏️ Simplificado)
│   │   │   │   ├── 🎭 AttractionRepository.java   (✏️ Simplificado)
│   │   │   │   ├── 🔗 ConexaoRepository.java      (✨ NOVO)
│   │   │   │   └── UserRepository.java
│   │   │   │
│   │   │   └── 🧠 services/              (Lógica de negócio)
│   │   │       ├── 📍 CityService.java          (✏️ Simplificado)
│   │   │       ├── 🎭 AttractionService.java   (✏️ Simplificado)
│   │   │       ├── 🔗 ConexaoService.java      (✨ NOVO)
│   │   │       ├── GraphQueryService.java      (✏️ Corrigido)
│   │   │       ├── AuthService.java
│   │   │       ├── UserService.java
│   │   │       │
│   │   │       └── exceptions/
│   │   │           ├── ResourceNotFoundException.java
│   │   │           ├── ResourceAlreadyExistsException.java
│   │   │           ├── DatabaseException.java
│   │   │           ├── InvalidRouteException.java
│   │   │           └── UnprocessableEntityException.java
│   │   │
│   │   └── resources/                    (Configurações)
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   │
│   └── test/                              (Testes)
│       └── (A implementar)
│
└── target/                                (Build artifacts)
    ├── travelGraph-0.0.1-SNAPSHOT.jar    (JAR executável)
    └── classes/                           (Classes compiladas)
```

---

## 📊 Legenda de Mudanças

| Símbolo | Significado |
|---------|------------|
| ✨ NOVO | Arquivo criado nesta reestruturação |
| ✏️ Simplificado | Arquivo modificado - reduzido/simplificado |
| ✏️ Atualizado | Arquivo modificado - ajustes/correções |
| ✏️ Corrigido | Arquivo modificado - bugs/erros corrigidos |

---

## 🎯 Módulos Principais

### 1. 🔐 Authentication (`authConfig/`)
Responsável por segurança e autenticação JWT
```
- CustomAuthenticationProvider: Provider de autenticação
- CustomFilter: Filtro de requisições
- SecurityConfig: Configuração de segurança Spring
- TokenService: Geração e validação de JWT
```

### 2. 🎯 Controllers (`controller/`)
Endpoints REST da aplicação
```
- MainController: Endpoints auxiliares
- AuthController: Autenticação/Login
- CityController: CRUD Cidades (rota: /cidades)
- AttractionController: CRUD Pontos (rota: /pontos)
- ConexaoController: Conexões (rota: /conexoes) ✨
- GraphController: Queries de grafo
- UserController: Gerenciamento de usuários
```

### 3. 📦 DTOs (`dto/`)
Estruturas de dados para comunicação
```
Simplificados com apenas campos essenciais
- City: id, name, latitude, longitude
- Attraction: id, name, description, category, latitude, longitude, city
- Conexao: id, origem, destino, distancia, tempo ✨
```

### 4. 🗂️ Entities (`entities/`)
Modelos Neo4j
```
- CityNode: Nó "Cidade" com propriedades básicas
- AttractionNode: Nó "PontoTuristico" com propriedades essenciais
- CityConnection: Relacionamento "CONECTA" entre cidades
- UserEntity: Nó "User" para autenticação
```

### 5. 💾 Repositories (`repositories/`)
Camada de acesso a dados
```
- CityRepository: Operações em Cidade
- AttractionRepository: Operações em Atração
- ConexaoRepository: Operações em Conexões ✨
- UserRepository: Operações em Usuário
```

### 6. 🧠 Services (`services/`)
Lógica de negócio
```
- CityService: CRUD e regras de negócio para cidades
- AttractionService: CRUD e regras para atrações
- ConexaoService: Lógica de conexões ✨
- GraphQueryService: Queries avançadas de grafo
- AuthService: Autenticação
- UserService: Gerenciamento de usuários
```

### 7. 📊 Mappers (`mapper/`)
Conversão entre DTOs e Entities
```
- CityMapper: DTO ↔ CityNode
- AttractionMapper: DTO ↔ AttractionNode
- UserMapper: DTO ↔ UserEntity
```

### 8. 🔧 Helpers (`helpers/`)
Utilitários diversos
```
- DateHelper: Manipulação de datas
- UpdateValueHelper: Atualização de valores
- WriteErrorResponse: Escrita de erros
```

---

## 🔗 Relacionamentos Neo4j

### Estrutura de Grafo

```
Cidade (Label: "Cidade")
├── Properties: id, name, latitude, longitude
├── Relationships:
│   ├── CONECTA → Cidade (com properties: distancia, tempo)
│   └── ← PERTENCE_A de PontoTuristico
│
PontoTuristico (Label: "PontoTuristico")
├── Properties: id, name, description, category, latitude, longitude
└── Relationships:
    └── PERTENCE_A → Cidade
```

### Exemplo de Instância

```
Rio de Janeiro (ID: 1)
├── CONECTA → São Paulo (distancia: 430.5 km, tempo: 6.0 horas)
├── CONECTA → Belo Horizonte (distancia: 434 km, tempo: 6.5 horas)
│
├── ← PERTENCE_A
│   ├── Cristo Redentor
│   ├── Pão de Açúcar
│   └── Copacabana
```

---

## 🚀 Fluxo de Requisição

### Exemplo: GET /pontos?cidadeId=1

```
Cliente HTTP
    ↓
CityController.findAll(cidadeId=1)
    ↓
AttractionService.findByCityId(1)
    ↓
AttractionRepository.findByCityId(1)
    ↓ (Cypher Query)
Neo4j Database
    ↓
MATCH (a:PontoTuristico)-[:PERTENCE_A]-(c:Cidade {id:1}) RETURN a
    ↓
List<AttractionNode>
    ↓
AttractionMapper.toDTO()
    ↓
List<AttractionResponseDTO>
    ↓
HTTP 200 OK + JSON
```

---

## 📈 Estatísticas Estruturais

### Contagem de Arquivos por Categoria

| Categoria | Quantidade | Status |
|-----------|-----------|--------|
| Controllers | 7 | 2 ✨ novo, 2 ✏️ alterado |
| Services | 6 | 1 ✨ novo, 3 ✏️ alterado |
| Repositories | 4 | 1 ✨ novo, 2 ✏️ alterado |
| DTOs | 18 | 2 ✨ novo, 6 ✏️ alterado |
| Entities | 5 | 3 ✏️ alterado |
| Mappers | 3 | 2 ✏️ alterado |
| Exceptions | 5 | - |
| **Total** | **48** | **+8 novo, +18 alterado** |

---

## ✅ Checklist de Estrutura

- [x] Controllers implementados
- [x] Services implementados
- [x] Repositories configurados
- [x] DTOs com validação
- [x] Entities com anotações Neo4j
- [x] Mappers configurados
- [x] Exceções tratadas
- [x] Logging implementado
- [x] Documentação completa
- [x] Build compilado com sucesso

---

## 🎓 Como Navegar o Projeto

1. **Entender a arquitetura**: Comece por `FINAL_SUMMARY.md`
2. **Detalhes técnicos**: Leia `RESTRUCTURED.md`
3. **O que mudou**: Consulte `CHANGELOG_RESTRUCTURE.md`
4. **Testar endpoints**: Use `TESTING_GUIDE.md`
5. **Implementar novo endpoint**: Copie padrão de City/Attraction
6. **Debugar erro**: Procure em `exceptions/`
7. **Entender um DTO**: Veja o mapper correspondente
8. **Modificar query**: Edite o repositório e service

---

**Estrutura de Diretórios - Travel Graph API v1.0**  
*Última atualização: 12 de Junho de 2026*

