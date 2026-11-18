# Recetario México Típico · Documentación Completa del Código

Este proyecto es una aplicación nativa de Android desarrollada en **Java** para la gestión de recetas de cocina, planificación semanal de comidas (platillos y bebidas) y generación de listas de compras de ingredientes. Funciona **100% sin conexión a Internet** mediante almacenamiento local en SQLite, SharedPreferences y almacenamiento privado de archivos.

---

## 📐 Arquitectura General del Proyecto

El código está estructurado siguiendo el patrón de arquitectura **MVC (Modelo - Vista - Controlador)** apoyado por patrones **DAO (Data Access Object)** y **Singleton**:

* **Modelo (`com.example.recetario.modelo`)**: Clases de datos que representan recetas, ingredientes, categorías y plan de comidas.
* **Datos y DAO (`com.example.recetario.datos`)**: Gestión de la base de datos SQLite nativa, consultas SQL relacionales, inserciones y transacciones atómicas.
* **Utilidades y Persistencia (`com.example.recetario.util`)**: Procesamiento de archivos JPEG, preferencias `SharedPreferences`, exportación en `.txt` / `.json` y algoritmos de formateo y agrupación de compras.
* **Vistas y Controladores (`com.example.recetario`)**: Actividades (`Activity`) y adaptadores (`Adapter`) que manejan la interfaz de usuario, eventos y ciclos de vida.

---

## 📂 Explicación del Código Parte por Parte

### 1. Capa de Modelo (`com.example.recetario.modelo`)

* **`Receta.java`**
  Representa una receta completa. Contiene campos como `id`, `nombre`, `categoriaId`, `categoria`, `colorCategoria`, `dificultad`, `tiempoMin`, `porciones`, `preparacion`, `foto` (nombre de archivo JPEG), `favorita` y la lista de `ingredientes`. Incluye métodos de utilidad como `pasos()` para separar las instrucciones por saltos de línea y `textoDificultad()` para convertir valores numéricos a texto ("Fácil", "Media", "Difícil").

* **`IngredienteReceta.java`**
  Modela la relación entre una receta y un ingrediente. Guarda el nombre del ingrediente, la cantidad y la unidad de medida (ej. *250 g*, *1 taza*, *al gusto*). Contiene el método `cantidadPara(porcionesNuevas, porcionesBase)` que escala automáticamente la cantidad del ingrediente cuando el usuario ajusta las porciones en la vista de detalle.

* **`Categoria.java`**
  Representa los tipos de comida (ej. *Desayuno*, *Platillo Fuerte*, *Ensalada*, *Postre*, *Bebida*). Contiene `id`, `nombre` y `color` hexadecimal asociado para personalizar etiquetas e indicadores en la UI.

* **`ComidaPlan.java`**
  Representa una entrada en el planificador semanal. Contiene el ID del plan, la fecha (`YYYY-MM-DD`), el tiempo de comida (0 = Desayuno, 1 = Comida, 2 = Cena), la receta asignada, sus porciones y el color de su categoría.

* **`IngredienteCatalogo.java`**
  Modela un ingrediente registrado en el catálogo global del sistema, indicando cuántas recetas lo utilizan en total.

---

### 2. Capa de Datos y Persistencia SQLite (`com.example.recetario.datos`)

* **`RecetarioDbHelper.java`**
  Administrador principal de la base de datos SQLite (`recetario.db`).
  * Implementa el patrón **Singleton** mediante `get(Context)` para asegurar una sola conexión en toda la aplicación.
  * Habilita llaves foráneas (`db.setForeignKeyConstraintsEnabled(true)`) para soporte de `ON DELETE CASCADE`.
  * En `onCreate()`, ejecuta los scripts **`esquema.sql`** (creación de tablas) y **`semilla.sql`** (15 recetas iniciales).
  * En `onUpgrade()`, maneja la migración de versión (v1 a v2) eliminando la restricción `UNIQUE` para permitir múltiples platillos y bebidas por tiempo de comida.

* **`RecetaDao.java`**
  Clase DAO para gestionar la tabla de recetas, categorías, ingredientes y favoritos.
  * `buscar(...)`: Realiza búsquedas avanzadas por texto en nombres de recetas e ingredientes mediante `LIKE`, filtrando por categoría, dificultad o favoritas, con ordenamiento dinámico.
  * `obtener(id)`: Lee una receta completa realizando `JOIN` con la tabla de ingredientes y categorías.
  * `guardar(receta)`: Inserta o actualiza una receta e inserta sus ingredientes dentro de una **transacción atómica** (`beginTransaction()`). Elimina automáticamente ingredientes sin uso.
  * `eliminar(id)`: Elimina la receta y dispara el borrado en cascada en favoritos, planificador e ingredientes.
  * `alternarFavorita(id)`: Añade o quita una receta de la tabla de favoritos.
  * `catalogo(filtro)`: Devuelve el catálogo de ingredientes únicos.

* **`PlanDao.java`**
  Clase DAO encargada del planificador semanal y la lista de compras.
  * `semana(lunes)`: Obtiene todas las comidas agendadas para el rango de fechas de la semana dada.
  * `asignar(...)`: Agrega un platillo o bebida a una fecha y tiempo de comida especificados.
  * `cambiarReceta(...)`: Reemplaza la receta de un elemento del planificador.
  * `quitar(id)` y `limpiarSemana(lunes)`: Remueven elementos individuales o limpian la semana completa.
  * `ingredientesSemana(lunes)`: Ejecuta una consulta `JOIN` entre 4 tablas (`plan_comidas`, `recetas`, `receta_ingrediente`, `ingredientes`), ajustando las cantidades según las porciones planificadas y agrupándolas por ingrediente.

---

### 3. Capa de Utilidades y Archivos (`com.example.recetario.util`)

* **`Preferencias.java`**
  Encapsula el uso de `SharedPreferences` (`recetario_prefs`). Almacena y recupera:
  * Porciones predeterminadas.
  * Estado del modo oscuro (`true`/`false`).
  * Criterios y filtros guardados (categoría, dificultad, favoritas).
  * Ordenamiento seleccionado de recetas.
  * Estado de los ingredientes comprados (tachados) por semana.

* **`FotoStorage.java`**
  Maneja la persistencia de imágenes en el disco privado del dispositivo (`/files/fotos/`).
  * Mantiene las fotos fuera de la base de datos y solo guarda el nombre del archivo en SQLite.
  * `guardarDesdeUri(...)` y `guardarDesdeArchivo(...)`: Decodifican, corrigen la rotación según metadatos EXIF, redimensionan la imagen a un máximo de 1280px y la comprimen como JPEG al 85% de calidad.
  * `cargar(...)`: Carga la foto muestreada al tamaño necesario para no agotar la memoria RAM.

* **`FotoProvider.java`**
  Extiende `FileProvider` de Android para permitir a la app de cámara nativa escribir la fotografía tomada en la carpeta temporal de caché sin violar las políticas de seguridad de URI (`StrictMode`).

* **`Exportador.java`**
  Genera la representación en texto o JSON para exportar datos:
  * `recetaTexto(...)`: Formatea una receta a texto plano con porciones ajustadas e instrucciones paso a paso.
  * `listaTexto(...)`: Genera el texto de la lista de compras con casillas `[x]` / `[ ]`.
  * `recetasJson(...)`: Convierte todo el recetario a un documento `JSON` formateado.
  * `escribir(...)`: Escribe el contenido en la URI seleccionada por el usuario mediante Storage Access Framework (`ACTION_CREATE_DOCUMENT`).

* **`ListaCompras.java`**
  Contiene la lógica de consolidación para la lista de supermercado. Agrupa ingredientes idénticos, convierte unidades automáticamente (ej. *1500 g* → *1.5 kg*, *2500 ml* → *2.5 l*) y une la lista de recetas que requieren dicho ingrediente.

* **`Formato.java`**
  Formatea valores numéricos a fracciones legibles (ej. *0.5* → *½*, *0.25* → *¼*, *1.5* → *1½*), formatea tiempos de cocción (ej. *90 min* → *1 h 30 min*) y limpia nombres para generar nombres de archivo válidos.

* **`Semana.java`**
  Utilidad de fechas basada en `Calendar`. Obtiene el lunes de cualquier semana, genera arreglos de los 7 días de la semana y formatea etiquetas legibles en español (ej. *26 oct – 1 nov 2026*).

* **`ScriptSql.java`**
  Separa scripts `.sql` con múltiples sentencias respetando comillas y comentarios para poder ejecutarlas secuencialmente en SQLite.

* **`Vistas.java`**
  Genera avatares circulares con iniciales de texto y colores dinámicos cuando una receta no cuenta con fotografía.

---

### 4. Capa de Presentación e Interfaz de Usuario (`com.example.recetario`)

* **`BaseActivity.java`**
  Clase base abstracta heredada por todas las actividades de la aplicación:
  * Aplica dinámicamente el tema claro (`R.style.Tema_Recetario`) u oscuro (`R.style.Tema_Recetario_Oscuro`) antes de inflar la vista.
  * Detecta cambios en la preferencia de modo oscuro y reinicia la actividad con `recreate()`.
  * Configura `fitsSystemWindows` para compatibilidad de dibujo Edge-to-Edge en versiones recientes de Android.

* **`MainActivity.java`**
  Pantalla principal de la aplicación.
  * Muestra la lista de recetas usando `ListView` y `RecetaAdapter`.
  * Integra un `SearchView` en la barra de opciones para búsqueda en tiempo real.
  * Contiene filtros dinámicos por tipo de comida (Spinner de Categorías), dificultad y casilla para marcar "Solo Favoritas".
  * Incluye el botón flotante (FAB) para crear nuevas recetas.

* **`RecetaAdapter.java`**
  Adaptador personalizado de `BaseAdapter` para inflar los elementos de la lista de recetas (`item_receta.xml`). Muestra la foto o inicial, el nombre, categoría, tiempo, porciones y el botón interactivo para alternar la receta como favorita.

* **`DetalleRecetaActivity.java`**
  Pantalla que muestra la información completa de una receta.
  * Permite modificar interactivamente el número de porciones deseado con botones `+` y `-`, recalculando automáticamente la cantidad de cada ingrediente.
  * Muestra la lista de pasos de preparación.
  * Menú de opciones para marcar/desmarcar favorita, editar, compartir texto, exportar a `.txt` o eliminar la receta.
  * Permite agregar la receta actual directamente a cualquier día y tiempo del planificador semanal.

* **`FormRecetaActivity.java`**
  Formulario de creación y edición de recetas.
  * Permite agregar o tomar foto de la receta.
  * Maneja dinámicamente renglones de ingredientes con autocompletado (`AutoCompleteTextView`), cantidad y unidad de medida.
  * Guarda la información en la base de datos usando `RecetaDao.guardar()` dentro de una transacción.

* **`PlanificadorActivity.java`**
  Pantalla del planificador semanal.
  * Muestra los 7 días de la semana actual (de lunes a domingo) y permite navegar entre semanas pasadas o futuras.
  * Permite agregar **múltiples platillos y bebidas** para cada tiempo de comida (`Desayuno`, `Comida`, `Cena`).
  * Cada platillo/bebida permite ver la receta, ajustar sus porciones para esa comida o quitarlo del plan.
  * Botón directo para generar la lista de ingredientes de la semana.

* **`ListaIngredientesActivity.java`**
  Muestra la lista consolidada de ingredientes necesarios para las comidas planeadas en la semana.
  * Permite al usuario marcar/desmarcar productos comprados (efecto de tachado) guardando el estado en `SharedPreferences`.
  * Muestra una barra de progreso del avance de compras.
  * Opciones para exportar la lista a `.txt`, compartirla o desmarcar todos los elementos.

* **`IngredientesActivity.java`**
  Catálogo interactivo de ingredientes. Muestra una lista con el número de recetas que ocupan cada ingrediente y permite filtrar o tocar un ingrediente para consultar cuáles recetas lo contienen.

* **`AjustesActivity.java`**
  Pantalla de configuración global.
  * Switch para activar/desactivar el **Modo oscuro**.
  * Selector de **Porciones predeterminadas** para nuevas recetas y planificador.
  * Configuración de orden predeterminado de recetas (Nombre, Recientes, Tiempo).
  * Opción para borrar filtros guardados.
  * Muestra estadísticas de uso de almacenamiento (espacio usado por fotos y tamaño de la base de datos SQLite).
  * Botón para **Exportar todas las recetas en formato JSON**.

---

### 5. Recursos de Base de Datos (`app/src/main/res/raw`)

* **`esquema.sql`**: Define la estructura relacional de 6 tablas: `categorias`, `recetas`, `ingredientes`, `receta_ingrediente`, `favoritos` y `plan_comidas`.
* **`semilla.sql`**: Script de inserción inicial con 15 recetas típicas mexicanas (ej. *Enchiladas Verdes*, *Chilaquiles*, *Mole Poblano*, *Café de Olla*, *Agua de Horchata*, etc.).

---

### 6. Pruebas Unitarias (`app/src/test/java`)

* **`FormatoTest.java`**: Verifica la conversión de números a fracciones, unidades de medida y tiempos.
* **`ListaComprasTest.java`**: Valida la consolidación y suma de ingredientes idénticos en distintas unidades.
* **`SemanaTest.java`**: Prueba el cálculo exacto de rangos de fechas de lunes a domingo.
* **`ScriptSqlTest.java`**: Comprueba la correcta separación de sentencias SQL respetando cadenas con punto y coma.
