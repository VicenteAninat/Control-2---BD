# 🚀 Instrucciones de Uso

## 1️⃣ Configuración previa

- Clona este repositorio y entra a la carpeta raíz del proyecto.
- Asegúrate de tener **Docker** y **Docker Compose** instalados en tu máquina.
- Se necesita que en el backend se ejecute en el apartado maven, las opciones de clean y package (para la creación de target)

---

## 2️⃣ Configura las variables de entorno

### 📁 Archivo `.env` en la carpeta `Multiple_replicas`

Debes crear o editar el archivo `.env` en la carpeta `Multiple_replicas` (donde está tu `docker-compose.yml`) con el siguiente formato de ejemplo:

```env
POSTGRES_DB=tbdc2
POSTGRES_USER=usuario_db
POSTGRES_PASSWORD=tu_contraseña_segura
JWT_SECRET_KEY=0txPZzvTRMpWSjKQLghGBiXbme2lDVJq97C1O5Na
```
Observación: Se indico la Secret_Key por propósitos meramente de evaluación.

> **Nota:** Cambia los valores por los que desees usar, correspondientes a su configuración.
> Ejemplo base:
> ```
> POSTGRES_USER=postgres
> POSTGRES_PASSWORD=1234
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
- **Replicas:** Se definieron 3 replicas para el frontend y para Backend, además de la imagen correspondiente de la Base de datos.

---

## 📌 Consideraciones de dominio

- Se considera que un **sector** es una **categoría**.
- Cada **sector** tiene asignado un **punto geoespacial** (latitud y longitud).
- Se considera la implementación de un sector según las especificaciones del ayudante pertinente, un sector como un punto en el espacio
- Respecto a cada pregunta:

○ ¿Cuál es la tarea más cercana al usuario (que esté pendiente)?
    
	- Asumimos que es la tarea es del usuario
	- Asumimos que es respecto a la ubicación registrada del usuario

○ ¿Cuál es el sector con más tareas completadas en un radio de 2 kilómetros
del usuario?
    
	- Asumimos que son las tareas completadas del usuario

○ ¿Cuál es el promedio de distancia de las tareas completadas respecto a la
ubicación del usuario?
    
	- Asumimos que es respecto a las tareas completadas por el usuario

○ ¿En qué sectores geográficos se concentran la mayoría de las tareas
pendientes? (utilizando agrupación espacial).
        
	- La implementación considera la agrupación espacial entre sectores.

○ ¿Cuál es la tarea pendiente más cercana a la ubicación del usuario?
    
	- Asumimos que es cualquier tarea
	- Asumimos que es respecto a la ubicación del login

○ ¿Cuál es el sector con más tareas completadas dentro de un radio de 5 km
desde la ubicación del usuario?
    
	- Asumimos que es la ubicación del usuario al momento de hacer login

○ ¿Cuál es el promedio de distancia entre las tareas completadas y el punto
registrado del usuario?
    
	- Asumimos que es la ubicación del usuario al momento de hacer login


---

## 🔄 Notas adicionales

- Los datos de ejemplo se cargan automáticamente al iniciar la base de datos (`dbCreate.sql` y `populate.sql`).
- Si necesitas reiniciar la base de datos desde cero:
  ```sh
  docker compose down -v
  docker compose up -d --build
  ```
- Si cambias variables de entorno, recuerda reconstruir los contenedores afectados.
- Si en algún momento una de las peticiones dice que no se pueden encontrar datos no necesariamente es que fallo, si no que se debe refrescar otra vez, ósea apretar otro botón y volver al anterior para que funcione debido a temas de latencia y tiempos de espera, ya que como se montan en docker compose, puede afectar la velocidad de conexión de internet y los llamados debido a las diversas replicas que se manejan.
- Si falla al levantar en docker compose, basta con volver a intentar levantarlo.

---

## 📚 Documentación útil

- [Nuxt 3 Docs](https://nuxt.com/docs)
- [Spring Boot Docs](https://spring.io/projects/spring-boot)
- [PostGIS Docs](https://postgis.net/documentation/)
- [Docker Compose Docs](https://docs.docker.com/compose/)

---