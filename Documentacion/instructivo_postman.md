# 📡 Instructivo: Consumo de Servicios con Postman

Este instructivo te guía para consumir los endpoints expuestos por los servicios **CentralService** y **P2PNodo** usando [Postman](https://www.postman.com/).

## 🔧 Prerrequisitos

- Tener ambos servicios corriendo (por ejemplo, en `localhost:8080` y `localhost:8081`).
- Tener Postman instalado.
- Asegurarte de que no haya errores en los logs al iniciar los servicios.

---

## 🧠 1. CentralService

### 📌 POST `/api/register`

Registra un nodo en el sistema.

- **URL:** `http://localhost:8080/api/register`
- **Método:** `POST`
- **Headers:**
  - `Content-Type: application/json`
- **Body (JSON):**
```json
{
  "nodeId": "nodo1",
  "nodeUrl": "http://localhost:8081",
  "fragments": ["fragmento1", "fragmento2"]
}
```

---

### 📌 GET `/api/nodes`

Obtiene la lista de nodos registrados.

- **URL:** `http://localhost:8080/api/nodes`
- **Método:** `GET`

---

### 📌 GET `/api/fragment/{fragmentId}`

Busca qué nodo tiene un fragmento.

- **URL:**  
  `http://localhost:8080/api/fragment/fragmento1`
- **Método:** `GET`

---

## 📁 2. P2PNodo

### 📌 GET `/fragment/{id}`

Descarga un fragmento por su ID.

- **URL:**  
  `http://localhost:8081/fragment/fragmento1`
- **Método:** `GET`
- **En Postman:** usa `Send and Download` para guardar el archivo.

---

### 📌 POST `/fragment/receive`

Envía un fragmento al nodo actual.

- **URL:** `http://localhost:8081/fragment/receive`
- **Método:** `POST`
- **Tipo:** `multipart/form-data`
- **Body:**
  - `id` → Nombre del fragmento (tipo: `Text`)
  - `file` → Archivo binario (`.bin`, `.txt`, `.mp4`, etc.) (tipo: `File`)

---

## 📝 Ejemplo de flujo completo

1. **Registrar un nodo** en CentralService con sus fragmentos.
2. **Subir un fragmento** a P2PNodo.
3. **Consultar qué nodo tiene un fragmento** desde CentralService.
4. **Descargar ese fragmento** desde el nodo.

---

