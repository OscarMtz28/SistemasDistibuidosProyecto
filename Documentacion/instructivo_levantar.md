
# 🚀 Guía para Compilar y Levantar los Servicios P2P con Docker

Este instructivo explica paso a paso cómo compilar los servicios `central-service` y `p2p-node`, y luego levantarlos usando Docker Compose con múltiples nodos.

---

## ✅ Requisitos Previos

- Java 17+ instalado
- Maven instalado
- Docker y Docker Compose instalados y corriendo
- Redis (será manejado por Docker)
- Estructura del proyecto con los servicios separados

---

## 📁 Estructura del Proyecto

```
tu-proyecto/
├── central-service/
│   └── pom.xml
├── p2p-node/
│   └── pom.xml
├── docker-compose.yml
└── Dockerfiles para cada servicio
```

---

## 🧼 Paso 1: Compilar los Servicios

Abre una terminal por servicio y ejecuta:

### En `centralservice/`:

```bash
cd centralservice
mvn clean install
mvn package
```

### En `p2pnode/`:

```bash
cd p2pnode
mvn clean install
mvn package
```

---

## 🐳 Paso 2: Construir las Imágenes Docker

Desde la raíz del proyecto, ejecuta:

```bash
docker compose build --no-cache
```

Esto generará imágenes actualizadas de cada microservicio.

---

## 🚀 Paso 3: Levantar los Servicios (Escalado Incluido)

```bash
docker compose up -d --scale p2pnodo=3
```

Esto levantará:

- `redis` (base de datos)
- `centralservice` en un contenedor
- `p2pnodo` con 3 nodos en paralelo (p2pnodo_1, p2pnodo_2, p2pnodo_3)

---

## ✅ Verificar que Todo Funciona

### Ver contenedores activos:

```bash
docker ps
```

Deberías ver 5 contenedores: 1 de Redis, 1 Central, 3 nodos.

---

## 🧪 Consumir Servicios

Puedes probar los servicios con Postman o curl:

- Obtener nodos registrados:
  ```
  GET http://localhost:8081/central/nodes
  ```

- Subir fragmento a un nodo:
  ```
  POST http://localhost:8080/fragment/receive
  FormData:
    - id: part_0
    - file: [archivo .bin]
  ```

- Descargar fragmento:
  ```
  GET http://localhost:8080/fragment/part_0
  ```

---

## 🧹 Apagar Servicios

```bash
docker compose down
```

---

