# Code Review Fixes Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Fix 8 bugs and issues identified during code review of the `feature/travel-graph-restructure` branch.

**Architecture:** Each task is a self-contained fix targeting one specific file or small group of related files. Tasks are ordered by severity (critical → low) and are independent — they can be implemented in any order.

**Tech Stack:** Java 17+, Spring Boot, Spring Data Neo4j, Lombok, Neo4j GDS

---

### Task 1: Fix ConexaoService.mapToDTO copy-paste bug (Critical)

**Problem:** `mapToDTO` uses `connection.getTargetCity()` for BOTH origin and destination fields. `CityConnection` is a `@RelationshipProperties` entity with only a `@TargetNode targetCity` — it has no reference to the origin city. The `findAllWithCities` Cypher query returns the origin node, but Spring Data Neo4j discards it during mapping.

**Files:**
- Modify: `src/main/java/com/travelGraph/repositories/ConexaoRepository.java:14-15`
- Modify: `src/main/java/com/travelGraph/services/ConexaoService.java:30-46,93-104`

- [ ] **Step 1: Add a new repository method that returns origin+destination data as maps**

In `ConexaoRepository.java`, add a new query method below `findAllWithCities`:

```java
@Query("""
    MATCH (origem:Cidade)-[conn:CONECTA]->(destino:Cidade)
    RETURN id(conn) AS connectionId,
           origem.id AS originCityId, origem.name AS originCityName,
           destino.id AS destinationCityId, destino.name AS destinationCityName,
           conn.distancia AS distance, conn.tempo AS time, conn.createdAt AS createdAt
""")
List<Map<String, Object>> findAllConnectionsWithCityInfo();
```

- [ ] **Step 2: Add a similar method for finding a single connection by ID**

In `ConexaoRepository.java`, add:

```java
@Query("""
    MATCH (origem:Cidade)-[conn:CONECTA]->(destino:Cidade)
    WHERE id(conn) = $id
    RETURN id(conn) AS connectionId,
           origem.id AS originCityId, origem.name AS originCityName,
           destino.id AS destinationCityId, destino.name AS destinationCityName,
           conn.distancia AS distance, conn.tempo AS time, conn.createdAt AS createdAt
""")
Optional<Map<String, Object>> findConnectionWithCityInfoById(@Param("id") Long id);
```

- [ ] **Step 3: Update ConexaoService to use the new repository methods**

Replace `findAll()`, `findById()`, and `mapToDTO()` in `ConexaoService.java`:

```java
public List<ConnectionResponseDTO> findAll() {
    List<Map<String, Object>> connections = conexaoRepository.findAllConnectionsWithCityInfo();
    List<ConnectionResponseDTO> response = new ArrayList<>();

    for (Map<String, Object> conn : connections) {
        response.add(mapToDTO(conn));
    }

    return response;
}

public ConnectionResponseDTO findById(Long id) {
    Map<String, Object> conn = conexaoRepository.findConnectionWithCityInfoById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Connection not found with ID: " + id));

    return mapToDTO(conn);
}
```

Replace `mapToDTO`:

```java
private ConnectionResponseDTO mapToDTO(Map<String, Object> data) {
    return new ConnectionResponseDTO(
        ((Number) data.get("connectionId")).longValue(),
        data.get("originCityId") != null ? ((Number) data.get("originCityId")).longValue() : null,
        data.get("destinationCityId") != null ? ((Number) data.get("destinationCityId")).longValue() : null,
        (String) data.get("originCityName"),
        (String) data.get("destinationCityName"),
        data.get("distance") != null ? ((Number) data.get("distance")).doubleValue() : null,
        data.get("time") != null ? ((Number) data.get("time")).doubleValue() : null,
        data.get("createdAt") != null ? (LocalDateTime) data.get("createdAt") : null
    );
}
```

- [ ] **Step 4: Update the create method to return correct DTO**

The `create` method currently calls `mapToDTO(conexao)` with a `CityConnection`, but we changed the signature. Update the return in the `create` method to build the DTO inline since we have both origin and destination in scope:

```java
// Replace the return at the end of the try block in create():
return new ConnectionResponseDTO(
    conexao.getId(),
    originCity.getId(),
    destinationCity.getId(),
    originCity.getName(),
    destinationCity.getName(),
    conexao.getDistancia(),
    conexao.getTempo(),
    conexao.getCreatedAt()
);
```

- [ ] **Step 5: Verify the application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 6: Commit**

```bash
git add src/main/java/com/travelGraph/repositories/ConexaoRepository.java src/main/java/com/travelGraph/services/ConexaoService.java
git commit -m "fix: correct ConexaoService.mapToDTO to return actual origin and destination city data"
```

---

### Task 2: Fix GraphQueryService catch(Exception) masking errors as 404 (Critical)

**Problem:** 4 methods in `GraphQueryService` catch `Exception` and rethrow as `ResourceNotFoundException` (404). Infrastructure errors (ClassCastException, NPE, Neo4j driver errors) get misclassified. The `ResourceNotFoundException` for "not found" cases should remain, but generic exceptions should propagate as 500s.

**Files:**
- Modify: `src/main/java/com/travelGraph/services/GraphQueryService.java:48-97,109-132,144-162,213-243`

- [ ] **Step 1: Fix findShortestPathBetweenCities (lines 48-97)**

Replace the try/catch block:

```java
try {
    List<Map<String, Object>> results = graphQueryRepository.findShortestPath(sourceCityId, targetCityId);

    if (results.isEmpty()) {
        throw new ResourceNotFoundException("Nenhuma rota encontrada entre as cidades");
    }

    Map<String, Object> result = results.get(0);

    List<Map<String, Object>> nodes = new ArrayList<>((Collection<Map<String, Object>>) result.get("nodes"));

    Collection<Map<String, Object>> relationships = (Collection<Map<String, Object>>) result.get("rels");

    for (int i = 0; i < nodes.size(); i++) {
        Map<String, Object> node = nodes.get(i);
        Map<String, Object> properties = (Map<String, Object>) node.get("properties");

        RouteStepDTO step = new RouteStepDTO();
        step.setId(((Number) properties.get("id")).longValue());
        step.setName((String) properties.get("name"));
        step.setType("CITY");
        step.setLatitude((Double) properties.get("latitude"));
        step.setLongitude((Double) properties.get("longitude"));
        step.setOrder(i);

        routeSteps.add(step);
    }

    for (Map<String, Object> rel : relationships) {
        Map<String, Object> properties = (Map<String, Object>) rel.get("properties");

        if (properties.get("distanceKm") != null) {
            totalDistance += ((Number) properties.get("distanceKm")).doubleValue();
        }

        if (properties.get("durationMinutes") != null) {
            totalDuration += ((Number) properties.get("durationMinutes")).intValue();
        }
    }

    return new ShortestPathResponseDTO(
        routeSteps,
        totalDistance,
        totalDuration,
        routeSteps.size()
    );

} catch (ResourceNotFoundException e) {
    throw e;
} catch (Exception e) {
    throw new DatabaseException("Erro ao calcular caminho mais curto: " + e.getMessage());
}
```

- [ ] **Step 2: Fix findNearbyLocations (lines 109-131)**

Replace the catch block at lines 128-130:

```java
} catch (ResourceNotFoundException e) {
    throw e;
} catch (Exception e) {
    throw new DatabaseException("Erro ao buscar locais próximos: " + e.getMessage());
}
```

- [ ] **Step 3: Fix findNearbyAttractions (lines 144-162)**

Replace the catch block at lines 158-160:

```java
} catch (ResourceNotFoundException e) {
    throw e;
} catch (Exception e) {
    throw new DatabaseException("Erro ao buscar atrações próximas: " + e.getMessage());
}
```

- [ ] **Step 4: Fix findRecommendedItinerary (lines 213-243)**

Replace the catch block at lines 238-240:

```java
} catch (ResourceNotFoundException e) {
    throw e;
} catch (Exception e) {
    throw new DatabaseException("Error finding recommended itinerary: " + e.getMessage());
}
```

- [ ] **Step 5: Add the DatabaseException import**

Add at the top of `GraphQueryService.java`:

```java
import com.travelGraph.services.exceptions.DatabaseException;
```

- [ ] **Step 6: Verify the application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/travelGraph/services/GraphQueryService.java
git commit -m "fix: stop masking infrastructure errors as 404 in GraphQueryService"
```

---

### Task 3: Fix findNearbyAttractions ignoring radiusKm and hardcoding distanceKm (Critical)

**Problem:** `findNearbyAttractions` in `GraphQueryService` hardcodes `distanceKm` to `0.0` and never passes `radiusKm` to the repository query. The repository query `findNearbyAttractions` only takes `cityId` — it has no radius filtering and no distance calculation.

**Files:**
- Modify: `src/main/java/com/travelGraph/repositories/GraphQueryRepository.java:48-54`
- Modify: `src/main/java/com/travelGraph/services/GraphQueryService.java:138-163`

- [ ] **Step 1: Update the repository query to include distance info**

The current query at `GraphQueryRepository.java:48-54` just matches attractions by city. Since attractions are linked via `HAS_ATTRACTION` (not a distance-based relationship), the "nearby" concept here means "attractions of nearby cities." Update the query to also traverse to nearby cities' attractions within a radius:

```java
@Query("""
    MATCH (city:City {id: $cityId})-[:HAS_ATTRACTION]->(attraction:Attraction)
    RETURN attraction, 0.0 AS distance
    UNION
    MATCH (city:City {id: $cityId})-[conn:CONNECTS_TO]->(nearby:City)-[:HAS_ATTRACTION]->(attraction:Attraction)
    WHERE conn.distanceKm <= $radius
    RETURN attraction, conn.distanceKm AS distance
""")
List<Map<String, Object>> findNearbyAttractions(
    @Param("cityId") Long cityId,
    @Param("radius") Double radius
);
```

- [ ] **Step 2: Update the service method to pass radiusKm and use the real distance**

Replace `findNearbyAttractions` in `GraphQueryService.java` (lines 138-163):

```java
public List<NearbyLocationDTO> findNearbyAttractions(Long cityId, Double radiusKm) {
    cityRepository.findById(cityId)
        .orElseThrow(() -> new ResourceNotFoundException("Cidade não encontrada"));

    Double radius = radiusKm != null ? radiusKm : 100.0;

    List<NearbyLocationDTO> nearbyAttractions = new ArrayList<>();

    try {
        List<Map<String, Object>> results = graphQueryRepository.findNearbyAttractions(cityId, radius);

        for (Map<String, Object> result : results) {
            Map<String, Object> attractionNode = (Map<String, Object>) result.get("attraction");
            Map<String, Object> properties = (Map<String, Object>) attractionNode.get("properties");

            NearbyLocationDTO nearby = new NearbyLocationDTO();
            nearby.setType("ATTRACTION");
            nearby.setLocation(properties);
            nearby.setDistanceKm(((Number) result.get("distance")).doubleValue());

            nearbyAttractions.add(nearby);
        }
    } catch (ResourceNotFoundException e) {
        throw e;
    } catch (Exception e) {
        throw new DatabaseException("Erro ao buscar atrações próximas: " + e.getMessage());
    }

    return nearbyAttractions;
}
```

- [ ] **Step 3: Verify the application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/travelGraph/repositories/GraphQueryRepository.java src/main/java/com/travelGraph/services/GraphQueryService.java
git commit -m "fix: findNearbyAttractions now uses radiusKm parameter and returns real distance"
```

---

### Task 4: Fix CustomFilter returning 404 for deleted user (High)

**Problem:** When a JWT references a deleted user, `ResourceNotFoundException` is caught and returns HTTP 404, leaking user-existence info. Auth failures should uniformly return 403.

**Files:**
- Modify: `src/main/java/com/travelGraph/authConfig/CustomFilter.java:64-70`

- [ ] **Step 1: Merge the ResourceNotFoundException catch into the existing auth-error catch**

Replace the two catch blocks (lines 64-70) with a single block that treats all auth-related errors as 403:

```java
} catch (JWTDecodeException | JWTCreationException | ResponseStatusException | ResourceNotFoundException ex) {
    WriteErrorResponse.writeErrorResponse(response, request, HttpStatus.FORBIDDEN, ex, objectMapper);
    return;
}
```

- [ ] **Step 2: Verify the application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/travelGraph/authConfig/CustomFilter.java
git commit -m "fix: return 403 instead of 404 for deleted-user JWT to prevent info leakage"
```

---

### Task 5: Fix GraphController.createConnection silent no-op for unknown type (High)

**Problem:** `createConnection` has an if/else-if for "CITY" and "ATTRACTION" types but no else branch. Unknown types silently return 204 No Content.

**Files:**
- Modify: `src/main/java/com/travelGraph/controller/GraphController.java:64-73`

- [ ] **Step 1: Add an else branch that returns 400 Bad Request**

Replace the `createConnection` method (lines 64-73):

```java
@PostMapping(value = "/connections")
public ResponseEntity<Void> createConnection(@Valid @RequestBody CreateConnectionRequestDTO connectionDTO) {
    if ("CITY".equalsIgnoreCase(connectionDTO.getConnectionType())) {
        graphQueryService.createCityConnection(connectionDTO);
    } else if ("ATTRACTION".equalsIgnoreCase(connectionDTO.getConnectionType())) {
        graphQueryService.createAttractionConnection(connectionDTO);
    } else {
        return ResponseEntity.badRequest().build();
    }

    return ResponseEntity.noContent().build();
}
```

- [ ] **Step 2: Verify the application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/travelGraph/controller/GraphController.java
git commit -m "fix: return 400 for unknown connectionType instead of silent 204"
```

---

### Task 6: Fix UserService.delete ignoring Optional result (High)

**Problem:** `delete()` calls `findById(id)` but discards the `Optional` return. A non-existent user silently gets a 204 instead of 404.

**Files:**
- Modify: `src/main/java/com/travelGraph/services/UserService.java:44-55`

- [ ] **Step 1: Check the Optional result before deleting**

Replace the `delete` method (lines 44-55):

```java
public void delete(CurrentUserDTO currentUser, String id) {
    try {
        if (currentUser.getId().equals(id)) {
            throw new DatabaseException("Usuário não pode deletar a si mesmo");
        }

        Optional<UserNode> user = findById(id);

        if (user.isEmpty()) {
            throw new ResourceNotFoundException("Usuário não encontrado");
        }

        userRepository.deleteById(id);
    } catch (DataIntegrityViolationException e) {
        throw new DatabaseException(e.getMessage());
    }
}
```

- [ ] **Step 2: Verify the application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 3: Commit**

```bash
git add src/main/java/com/travelGraph/services/UserService.java
git commit -m "fix: UserService.delete now validates user exists before deleting"
```

---

### Task 7: Fix N+1 queries in RotaService.construirRoteiro (Medium)

**Problem:** `construirRoteiro` makes 4 DB calls per city in the route: `cityRepository.findById`, `attractionRepository.findByCityId`, `buscarDistanciaConexao`, and `buscarTempoConexao`. The last two call separate repository methods for the same connection.

**Files:**
- Modify: `src/main/java/com/travelGraph/services/RotaService.java:132-197`

- [ ] **Step 1: Consolidate the two connection queries into one**

Replace `buscarDistanciaConexao` and `buscarTempoConexao` (lines 176-197) with a single method:

```java
private double[] buscarConexaoInfo(Long origemId, Long destinoId) {
    try {
        Optional<CityConnection> conexao = conexaoRepository.findByOrigemAndDestino(origemId, destinoId);

        if (conexao.isPresent()) {
            Double distancia = conexao.get().getDistancia() != null ? conexao.get().getDistancia() : 0.0;
            Double tempo = conexao.get().getTempo() != null ? conexao.get().getTempo() : 0.0;
            return new double[]{distancia, tempo};
        }

        return new double[]{0.0, 0.0};
    } catch (Exception e) {
        throw new DatabaseException("Erro ao buscar informações da conexão: " + e.getMessage());
    }
}
```

- [ ] **Step 2: Update construirRoteiro to use the consolidated method**

Replace lines 156-162 in `construirRoteiro`:

```java
if (i < caminhoIds.size() - 1) {
    double[] conexaoInfo = buscarConexaoInfo(cityId, caminhoIds.get(i + 1));
    totalDistance += conexaoInfo[0];
    totalTime += conexaoInfo[1];
}
```

- [ ] **Step 3: Remove the old buscarDistanciaConexao and buscarTempoConexao methods**

Delete the `buscarDistanciaConexao` method (lines 176-184) and `buscarTempoConexao` method (lines 189-197).

- [ ] **Step 4: Verify the application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 5: Commit**

```bash
git add src/main/java/com/travelGraph/services/RotaService.java
git commit -m "fix: consolidate N+1 connection queries in construirRoteiro into single call per edge"
```

---

### Task 8: Fix RotaService manual CityNode field copy (Low)

**Problem:** `construirRoteiro` manually copies `id`, `name`, `latitude`, `longitude` from `CityNode` to `CityRouteDTO` instead of reusing existing mapper logic. If `CityNode` gains a new field, this block will silently produce incomplete data.

**Files:**
- Modify: `src/main/java/com/travelGraph/services/RotaService.java:147-154`

- [ ] **Step 1: Replace manual field copy with CityMapper usage**

First, check `CityRouteDTO` — it has fields `id`, `name`, `latitude`, `longitude`, `attractions`. The `CityMapper.toDTO` returns a `CityResponseDTO` which has the same base fields. Since the types differ (`CityRouteDTO` vs `CityResponseDTO`), add a static factory or a mapper method.

Replace the manual copy block in `construirRoteiro` (lines 147-154):

```java
CityRouteDTO cityRouteDto = new CityRouteDTO();
cityRouteDto.setId(city.getId());
cityRouteDto.setName(city.getName());
cityRouteDto.setLatitude(city.getLatitude());
cityRouteDto.setLongitude(city.getLongitude());
cityRouteDto.setAttractions(attractionDTOs);
```

Since `CityRouteDTO` has an `attractions` field that `CityResponseDTO` does not, and the types are different, the cleanest approach is to add a static method to `CityRouteDTO`:

In `src/main/java/com/travelGraph/dto/route/CityRouteDTO.java`, add:

```java
public static CityRouteDTO fromCityNode(CityNode city, List<AttractionResponseDTO> attractions) {
    CityRouteDTO dto = new CityRouteDTO();
    dto.setId(city.getId());
    dto.setName(city.getName());
    dto.setLatitude(city.getLatitude());
    dto.setLongitude(city.getLongitude());
    dto.setAttractions(attractions);
    return dto;
}
```

Add the needed imports to `CityRouteDTO.java`:

```java
import com.travelGraph.dto.attraction.AttractionResponseDTO;
import com.travelGraph.entities.CityNode;
import java.util.List;
```

- [ ] **Step 2: Use the factory method in RotaService**

Replace lines 147-152 in `construirRoteiro` with:

```java
CityRouteDTO cityRouteDto = CityRouteDTO.fromCityNode(city, attractionDTOs);
```

- [ ] **Step 3: Verify the application compiles**

Run: `./mvnw compile -q`
Expected: BUILD SUCCESS

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/travelGraph/dto/route/CityRouteDTO.java src/main/java/com/travelGraph/services/RotaService.java
git commit -m "refactor: extract CityRouteDTO.fromCityNode to avoid manual field copy"
```
