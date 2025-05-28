# 🚀 Instrucciones de Uso

## 1️⃣ Configuración previa

- Clona este repositorio y entra a la carpeta raíz del proyecto.
- Asegúrate de tener **Docker** y **Docker Compose** instalados en tu máquina.

---

## 2️⃣ Configura las variables de entorno

### 📁 Archivo `.env` en la carpeta `Multiple_replicas`

Debes crear o editar el archivo `.env` en la carpeta `Multiple_replicas` (donde está tu `docker-compose.yml`) con el siguiente formato de ejemplo:

```env
POSTGRES_DB=tbdc2
POSTGRES_USER=usuario_db
POSTGRES_PASSWORD=tu_contraseña_segura
JWT_SECRET_KEY=tu_clave_secreta
```

> **Nota:** Cambia los valores por los que desees usar.  
> Ejemplo base:
> ```
> POSTGRES_USER=postgres
> POSTGRES_PASSWORD=1234
> JWT_SECRET_KEY=arreglo alfanumerico de 40 caracteres
> ```

### 📁 Archivo `.env` en la carpeta `frontend/`

Asegúrate de que el archivo `.env` en la carpeta `frontend/` tenga:

```env
BACKEND_SERVER=localhost
BACKEND_PORT=8090
```

---

## 3️⃣ Despliegue con Docker Compose

Desde la carpeta donde está tu `docker-compose.yml`, ejecuta:

```sh
docker compose build
docker compose up -d
```

Esto levantará todos los servicios: base de datos, backend, frontend y balanceadores Nginx.

---

## 4️⃣ Acceso a la aplicación

- **Frontend:**  
  [http://localhost:8070](http://localhost:8070)

- **Backend (API):**  
  [http://localhost:8090](http://localhost:8090)

- **Base de datos:**  
  PostgreSQL/PostGIS en el puerto 5432

---

# 🛠️ Detalles de Implementación

- **Frontend:** Vue 3 + Nuxt 3, servido por Nginx en Docker.
- **Backend:** Spring Boot (Java), autenticación JWT, soporte geoespacial.
- **Base de datos:** PostgreSQL con extensión PostGIS.
- **Balanceo de carga:** Nginx balancea tanto el frontend como el backend.
- **Contenedores:** Orquestados con Docker Compose.

---

## 📌 Consideraciones de dominio

- Se considera que un **sector** es una **categoría**.
- Cada **sector** tiene asignado un **punto geoespacial** (latitud y longitud).

---

## 🔄 Notas adicionales

- Los datos de ejemplo se cargan automáticamente al iniciar la base de datos (`dbCreate.sql` y `populate.sql`).
- Si necesitas reiniciar la base de datos desde cero:
  ```sh
  docker compose down -v
  docker compose up -d --build
  ```
- Si cambias variables de entorno, recuerda reconstruir los contenedores afectados.

---

## 📚 Documentación útil

- [Nuxt 3 Docs](https://nuxt.com/docs)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [PostGIS Docs](https://postgis.net/documentation/)
- [Docker Compose Docs](https://docs.docker.com/compose/)

---

¡Listo! Si tienes dudas, revisa los comentarios en el código o contacta al equipo de desarrollo.