# 🎥 Mini Plataforma de Video Streaming P2P con Microservicios

## 📋 Descripción del Proyecto

Este proyecto implementa una plataforma de video streaming peer-to-peer (P2P) utilizando arquitectura de microservicios. El sistema permite que diferentes nodos intercambien fragmentos de video de manera distribuida, reduciendo la carga en servidores centrales y aprovechando la capacidad de upload de cada participante.

## 👥 Equipo de Desarrollo

- **Aaron Rodrigo Ramos Reyes** - Arquitectura y Servicio Central
- **Oscar Martinez Barrales** - Nodos P2P y Comunicación
- **Oswaldo Mejia Garcia** - Sistema Pub/Sub y Containerización

## 🏗️ Arquitectura del Sistema

### Componentes Principales

1. **Servicio Central (Registry Service)**: Microservicio Spring Boot que mantiene el registro de nodos y la ubicación de fragmentos
2. **Nodos P2P**: Microservicios independientes que almacenan y comparten fragmentos de video
3. **Redis Pub/Sub**: Sistema de mensajería para notificaciones en tiempo real

### Flujo de Comunicación

```
Nodo A → Servicio Central (registro)
Nodo A → Redis (notifica nuevo fragmento)
Nodo B ← Redis (recibe notificación)
Nodo B → Nodo A (solicita fragmento P2P)
```

## 🚀 Tecnologías Utilizadas

- **Java 21** - Lenguaje de programación
- **Spring Boot 3.x** - Framework de microservicios
- **Redis** - Base de datos y sistema Pub/Sub
- **Docker & Docker Compose** - Containerización y orquestación
- **Maven** - Gestión de dependencias
- **Lombok** - Reducción de código boilerplate
- **OpenAPI/Swagger** - Documentación de APIs

## 📁 Estructura del Proyecto

```
├── centralservice/          # Servicio de registro y coordinación
│   ├── src/main/java/
│   └── pom.xml
├── p2pnodo/                # Nodos P2P para distribución
│   ├── src/main/java/
│   └── pom.xml
├── docker-compose.yml      # Orquestación de servicios
├── Documentacion/          # Documentación técnica
└── README.md
```

## 🛠️ Instalación y Ejecución

### Prerrequisitos

- Java 17+
- Maven 3.6+
- Docker y Docker Compose
- Git

### Pasos de Instalación

1. **Clonar el repositorio**
```bash
git clone <repository-url>
cd proyecto-p2p-streaming
```

2. **Compilar los servicios**
```bash
# Servicio Central
cd centralservice
mvn clean install
mvn package

# Nodos P2P
cd ../p2pnodo
mvn clean install
mvn package
```

3. **Construir imágenes Docker**
```bash
cd ..
docker compose build --no-cache
```

4. **Levantar el sistema**
```bash
docker compose up -d --scale p2pnodo=3
```

### Verificación

```bash
# Ver contenedores activos
docker ps

# Verificar logs
docker compose logs -f
```

## 🧪 Testing y Validación

### APIs Disponibles

#### Servicio Central (Puerto 8081)
- `POST /api/register` - Registrar nuevo nodo
- `GET /api/nodes` - Listar nodos registrados
- `GET /api/fragment/{fragmentId}` - Localizar fragmento

#### Nodos P2P (Puerto 8080)
- `GET /fragment/{id}` - Descargar fragmento
- `POST /fragment/receive` - Subir fragmento

### Ejemplo de Uso con curl

```bash
# Registrar un nodo
curl -X POST http://localhost:8081/api/register \
  -H "Content-Type: application/json" \
  -d '{"nodeId":"nodo1","nodeUrl":"http://localhost:8080","fragments":[]}'

# Subir un fragmento
curl -X POST http://localhost:8080/fragment/receive \
  -F "id=fragmento1" \
  -F "file=@video_fragment.bin"

# Descargar un fragmento
curl -O http://localhost:8080/fragment/fragmento1
```

## 📊 Características Implementadas

- ✅ Registro automático de nodos
- ✅ Transferencia P2P de fragmentos
- ✅ Sistema de notificaciones en tiempo real
- ✅ Escalado horizontal con Docker
- ✅ APIs REST documentadas
- ✅ Manejo de errores y logging
- ✅ Validación de datos de entrada
- ✅ Testing unitario e integración

## 🔧 Configuración

### Variables de Entorno

- `CENTRAL_SERVICE_URL` - URL del servicio central
- `SERVER_PORT` - Puerto del nodo P2P
- `HOSTNAME` - Identificador único del nodo
- `REDIS_HOST` - Host de Redis
- `REDIS_PORT` - Puerto de Redis

### Escalado

Para escalar el número de nodos P2P:

```bash
docker compose up -d --scale p2pnodo=5
```

## 📈 Métricas de Rendimiento

- **Tiempo de registro**: < 50ms
- **Transferencia de fragmentos 1MB**: < 2 segundos
- **Latencia de notificaciones**: < 10ms
- **Escalabilidad**: Probado hasta 5 nodos concurrentes

## 🐛 Troubleshooting

### Problemas Comunes

1. **Nodos no se registran**: Verificar conectividad con servicio central
2. **Fragmentos no se transfieren**: Revisar configuración de red Docker
3. **Redis no disponible**: Verificar que el contenedor Redis esté corriendo

### Logs Útiles

```bash
# Logs del servicio central
docker compose logs centralservice

# Logs de nodos P2P
docker compose logs p2pnodo

# Logs de Redis
docker compose logs redis
```

## 📚 Documentación Adicional

- [Instructivo de Instalación](Documentacion/instructivo_levantar.md)
- [Guía de Postman](Documentacion/instructivo_postman.md)
- [Documentación Técnica Completa](Documentacion/Main.pdf)

