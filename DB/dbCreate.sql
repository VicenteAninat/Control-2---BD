-- Crear la base de datos
CREATE DATABASE tbdc2;

-- Conectarse a la base de datos
\c tbdc2

-- Habilitar extensión PostGIS
CREATE EXTENSION IF NOT EXISTS postgis;

CREATE TABLE Usuario
(
  id BIGSERIAL NOT NULL,
  username VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  location GEOMETRY(Point) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE (username)
);

CREATE TABLE Sector
(
  id BIGSERIAL NOT NULL,
  nombre VARCHAR(255) NOT NULL,
  descripcion VARCHAR(255) NOT NULL,
  ubicacion GEOMETRY(Point) NOT NULL,
  PRIMARY KEY (id)
);

CREATE TABLE Tarea
(
  id BIGSERIAL NOT NULL,
  nombre VARCHAR(255) NOT NULL,
  descripcion VARCHAR(255) NOT NULL,
  fecha_vencimiento DATE NOT NULL,
  completado BOOLEAN NOT NULL,
  usuario_id BIGINT NOT NULL,
  sector_id BIGINT NOT NULL,
  PRIMARY KEY (id),
  FOREIGN KEY (usuario_id) REFERENCES Usuario(id),
  FOREIGN KEY (sector_id) REFERENCES Sector(id)
);

-- Crear índices espaciales
CREATE INDEX IF NOT EXISTS idx_usuario_location ON Usuario USING GIST (location);
CREATE INDEX IF NOT EXISTS idx_sector_ubicacion ON Sector USING GIST (ubicacion);