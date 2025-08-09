## 🚀 Configuración Inicial

### Prerrequisitos
- Postman instalado
- Sistema P2P ejecutándose con Docker Compose
- Puertos disponibles: 8080 (Central Service), 8081-8083 (Nodos P2P)

### URLs Base
```
Central Service: http://localhost:8080
Nodo 1: http://localhost:8081
Nodo 2: http://localhost:8082
Nodo 3: http://localhost:8083
```

## 📋 API del Servicio Central

### 1. Consultar Nodos Registrados
**Obtiene la lista de todos los nodos registrados en el sistema**

- **Método**: `GET`
- **URL**: `http://localhost:8080/api/nodes`
- **Headers**: No requeridos

**Respuesta esperada**:
```json
{
    "p2pnodo1": {
        "lastSeen": 1754704441263,
        "fragments": ["video_001_part_01", "serie_s01e01_segment_03"],
        "url": "http://p2pnodo1:8080"
    },
    "p2pnodo2": {
        "lastSeen": 1754704236902,
        "fragments": ["video_001_part_02", "documental_part_01"],
        "url": "http://p2pnodo2:8080"
    },
    "p2pnodo3": {
        "lastSeen": 1754704236902,
        "fragments": ["video_001_part_03"],
        "url": "http://p2pnodo3:8080"
    }
}
```

### 2. Buscar Ubicación de Fragmento
**Encuentra en qué nodo está almacenado un fragmento específico**

- **Método**: `GET`
- **URL**: `http://localhost:8080/api/fragment/{fragmentId}`
- **Ejemplo**: `http://localhost:8080/api/fragment/video_001_part_01`

**Respuesta esperada**:
```
Fragment available at: http://p2pnodo1:8080
```

**Posibles respuestas de error**:
- `404 Not Found`: Si el fragmento no existe

## 🎬 API de Fragmentación Automática

### 3. Subir Video para Fragmentación Automática
**Sube un video completo que será automáticamente fragmentado y distribuido**

- **Método**: `POST`
- **URL**: `http://localhost:8080/api/videos/upload`
- **Content-Type**: `multipart/form-data`

**Parámetros del formulario**:
- `file` (file): Video completo (.mp4, .avi, .mkv, .mov, etc.)

**Configuración en Postman**:
1. Selecciona método `POST`
2. En la pestaña `Body`, selecciona `form-data`
3. Agrega key `file` (tipo File) y selecciona tu video completo
4. El sistema automáticamente:
   - Fragmenta el video en partes de 1MB
   - Distribuye los fragmentos entre los nodos disponibles
   - Registra la ubicación de cada fragmento

**Respuesta esperada**:
```
Video dividido en 15 fragmentos. Directorio: /app/video_fragments
```

**Proceso automático**:
1. **Fragmentación**: El video se divide en chunks de 1MB con nombres `video_part_0.mp4`, `video_part_1.mp4`, etc.
2. **Distribución**: Los fragmentos se asignan automáticamente a nodos disponibles
3. **Registro**: Cada fragmento se registra en el servicio central con su ubicación

## 📁 API de Nodos P2P

### 4. Subir Fragmento Manual (Opcional)
**Sube un fragmento específico a un nodo (para casos especiales)**

- **Método**: `POST`
- **URL**: `http://localhost:8081/fragment/receive` (o 8082, 8083 para otros nodos)
- **Content-Type**: `multipart/form-data`

**Parámetros del formulario**:
- `id` (text): Identificador único del fragmento
- `file` (file): Fragmento de video

**Respuesta esperada**:
```
Fragmento 'video_part_5' recibido correctamente.
```

### 5. Descargar Fragmento de Video
**Descarga un fragmento específico desde cualquier nodo**

- **Método**: `GET`
- **URL**: `http://localhost:8082/fragment/{fragmentId}`
- **Ejemplo**: `http://localhost:8082/fragment/video_part_0`

**Respuesta**: El fragmento de video descargado como archivo binario

**Headers de respuesta**:
```
Content-Type: application/octet-stream
Content-Disposition: attachment; filename="video_part_0.bin"
```

**Nota**: Los fragmentos generados automáticamente tienen nombres como:
- `video_part_0` (primer fragmento)
- `video_part_1` (segundo fragmento)  
- `video_part_2` (tercer fragmento)
- etc.

**Uso típico**: Los fragmentos descargados se pueden:
- Reproducir secuencialmente para streaming
- Concatenar para reconstruir el video original
- Cachear localmente para reproducción offline

## 🧪 Casos de Prueba Recomendados

### Caso 1: Fragmentación Automática de Video

1. **Verificar nodos activos**
   ```
   GET http://localhost:8080/api/nodes
   ```

2. **Subir video completo para fragmentación automática**
   ```
   POST http://localhost:8080/api/videos/upload
   Body: form-data
   - file: [mi_pelicula.mp4] (video completo)
   ```

3. **Verificar fragmentos generados**
   ```
   GET http://localhost:8080/api/nodes
   ```
   (Verás los fragmentos `video_part_0`, `video_part_1`, etc. distribuidos entre nodos)

4. **Descargar fragmentos para streaming**
   ```
   GET http://localhost:8081/fragment/video_part_0
   GET http://localhost:8082/fragment/video_part_1
   GET http://localhost:8083/fragment/video_part_2
   ```

### Caso 2: Streaming Secuencial de Video Fragmentado

**Escenario**: Subir un episodio completo y reproducirlo secuencialmente

1. **Subir episodio completo**
   ```
   POST http://localhost:8080/api/videos/upload
   Body: form-data
   - file: [episodio_s01e01.mp4] (archivo de ~50MB)
   ```

2. **El sistema automáticamente genera ~50 fragmentos de 1MB cada uno**
   - `video_part_0` → Nodo 1
   - `video_part_1` → Nodo 2  
   - `video_part_2` → Nodo 3
   - `video_part_3` → Nodo 1 (rotación)
   - etc.

3. **Verificar distribución automática**
   ```
   GET http://localhost:8080/api/nodes
   ```

4. **Simular streaming: descargar secuencialmente**
   ```
   GET http://localhost:8081/fragment/video_part_0
   GET http://localhost:8082/fragment/video_part_1  
   GET http://localhost:8083/fragment/video_part_2
   GET http://localhost:8081/fragment/video_part_3
   ... (continuar secuencialmente)
   ```


#### Requests Sugeridos

**1. Get All Nodes**
- Method: GET
- URL: `{{central_service}}/api/nodes`

**2. Find Fragment Location**
- Method: GET  
- URL: `{{central_service}}/api/fragment/{{fragment_id}}`



**3. Download Video Fragment from Node 1**
- Method: GET
- URL: `{{node1}}/fragment/video_part_0`

**4. Download Video Fragment from Node 2**
- Method: GET
- URL: `{{node2}}/fragment/video_part_1`

**5. Download Video Fragment from Node 3**
- Method: GET
- URL: `{{node3}}/fragment/video_part_2`



## ⚠️ Consideraciones Importantes

### Limitaciones
- Los fragmentos de video se almacenan en memoria, se pierden al reiniciar los contenedores
- No hay autenticación implementada
- Los fragmentos se almacenan temporalmente en `/app/fragments`

### Troubleshooting
- **Error de conexión**: Verifica que Docker Compose esté ejecutándose
- **404 Fragment not found**: El fragmento no existe o no está registrado
- **500 Internal Server Error**: Revisa los logs del contenedor correspondiente


