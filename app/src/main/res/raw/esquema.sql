-- ==========================================================
-- Recetario México Típico - Esquema de la base de datos
-- Lo ejecuta RecetarioDbHelper.onCreate() la primera vez
-- ==========================================================

-- Tipo de comida (1 categoría -> N recetas)
CREATE TABLE categorias (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre  TEXT    NOT NULL UNIQUE,
    color   TEXT    NOT NULL
);

-- Recetas
CREATE TABLE recetas (
    id            INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre        TEXT    NOT NULL,
    categoria_id  INTEGER NOT NULL REFERENCES categorias(id) ON DELETE RESTRICT,
    dificultad    INTEGER NOT NULL CHECK (dificultad BETWEEN 1 AND 3),
    tiempo_min    INTEGER NOT NULL DEFAULT 0 CHECK (tiempo_min >= 0),
    porciones     INTEGER NOT NULL DEFAULT 4 CHECK (porciones > 0),
    preparacion   TEXT    NOT NULL DEFAULT '',
    foto          TEXT,
    creada_en     INTEGER NOT NULL
);

-- Catálogo de ingredientes (sin repetir nombres)
CREATE TABLE ingredientes (
    id      INTEGER PRIMARY KEY AUTOINCREMENT,
    nombre  TEXT    NOT NULL UNIQUE COLLATE NOCASE
);

-- Tabla intermedia: relación N a M entre recetas e ingredientes
CREATE TABLE receta_ingrediente (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    receta_id       INTEGER NOT NULL REFERENCES recetas(id) ON DELETE CASCADE,
    ingrediente_id  INTEGER NOT NULL REFERENCES ingredientes(id) ON DELETE RESTRICT,
    cantidad        REAL    NOT NULL DEFAULT 0,
    unidad          TEXT    NOT NULL,
    orden           INTEGER NOT NULL DEFAULT 0
);

CREATE INDEX idx_ri_receta ON receta_ingrediente(receta_id);
CREATE INDEX idx_ri_ingrediente ON receta_ingrediente(ingrediente_id);

-- Favoritos (1 receta -> 0 o 1 favorito)
CREATE TABLE favoritos (
    receta_id    INTEGER PRIMARY KEY REFERENCES recetas(id) ON DELETE CASCADE,
    agregada_en  INTEGER NOT NULL
);

-- Planificador semanal: varias recetas/bebidas por día y tiempo de comida
-- tiempo: 0 = desayuno, 1 = comida, 2 = cena
CREATE TABLE plan_comidas (
    id         INTEGER PRIMARY KEY AUTOINCREMENT,
    fecha      TEXT    NOT NULL,
    tiempo     INTEGER NOT NULL CHECK (tiempo BETWEEN 0 AND 2),
    receta_id  INTEGER NOT NULL REFERENCES recetas(id) ON DELETE CASCADE,
    porciones  INTEGER NOT NULL CHECK (porciones > 0)
);

CREATE INDEX idx_plan_fecha ON plan_comidas(fecha);
