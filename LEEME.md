# Recetario Mexico Tipico - Proyecto 4

Aplicacion nativa de Android desarrollada en Java para la gestion de recetas de cocina, planificacion semanal de platillos y bebidas, y generacion de listas de compras de ingredientes. Funciona sin conexion a Internet mediante SQLite, SharedPreferences y archivos locales.

---

## Descripcion de Archivos del Proyecto

### Configuracion del Proyecto
* settings.gradle.kts: Define el nombre del proyecto y los modulos incluidos.
* gradle.properties: Contiene las configuraciones globales del entorno de construccion Gradle y memoria JVM.
* build.gradle.kts (raiz): Configuracion general de plugins y repositorios del proyecto.
* app/build.gradle.kts: Configuracion del modulo principal de la aplicacion, dependencias y version de Java.
* app/src/main/AndroidManifest.xml: Registro de componentes (actividades, servicios, permisos y proveedor de archivos).

---

### Capa de Modelo (com.example.recetario.modelo)
* Categoria.java: Modela los tipos de comida (id, nombre, color).
* ComidaPlan.java: Modela un platillo o bebida agendado en el planificador semanal.
* IngredienteCatalogo.java: Modela un ingrediente registrado en el catalogo con su conteo de uso.
* IngredienteReceta.java: Modela la relacion ingrediente-receta con cantidad, unidad y calculo de porciones.
* Receta.java: Clase principal que representa la receta con sus datos, ingredientes y lista de pasos.

---

### Capa de Datos (com.example.recetario.datos)
* RecetarioDbHelper.java: Administrador Singleton de la base de datos SQLite (recetario.db) y sus migraciones.
* RecetaDao.java: Objeto de acceso a datos para buscar, guardar, editar, consultar y eliminar recetas, categorias y favoritos.
* PlanDao.java: Objeto de acceso a datos para gestionar las comidas del planificador semanal y calcular los ingredientes acumulados.

---

### Capa de Utilidades y Persistencia (com.example.recetario.util)
* Preferencias.java: Administrador de SharedPreferences para guardar porciones, estado de modo oscuro, orden y filtros.
* FotoStorage.java: Gestiona el procesamiento, escalado, orientacion EXIF y guardado de imagenes JPEG en la memoria interna.
* FotoProvider.java: ContentProvider para compartir el archivo de captura con la aplicacion nativa de camara.
* Exportador.java: Genera y escribe archivos de texto plano (.txt) y respaldos en formato JSON (.json).
* ListaCompras.java: Algoritmo que agrupa, suma y convierte unidades (gramos a kilos, ml a litros) de la lista de compras.
* Formato.java: Formatea numeros a fracciones (1/2, 1/4), tiempos y limpia cadenas para nombres de archivo.
* Semana.java: Calcula fechas semanales de lunes a domingo, rangos y nombres de dias en espanol.
* ScriptSql.java: Divide archivos de script .sql en sentencias individuales para su ejecucion.
* Vistas.java: Utilidades de interfaz para bordes redondeados, colores de categorias e iniciales de avatares.

---

### Capa de Pantallas e Interfaz de Usuario (com.example.recetario)
* BaseActivity.java: Clase base abstracta que gestiona la aplicacion del tema claro/oscuro y soporte Edge-to-Edge.
* MainActivity.java: Pantalla principal que muestra el listado de recetas, buscador por texto y filtros.
* RecetaAdapter.java: Adaptador de la lista de recetas con memoria cache de imagenes LRU para desplazamiento fluido.
* DetalleRecetaActivity.java: Pantalla para visualizar la receta completa y cambiar dinamicamente sus porciones.
* FormRecetaActivity.java: Pantalla para crear o editar recetas con seleccion de foto e ingredientes dinamicos.
* PlanificadorActivity.java: Pantalla del planificador semanal para agregar platillos y bebidas por tiempo de comida.
* ListaIngredientesActivity.java: Pantalla que muestra la lista de compras de la semana con casillas para tachar productos.
* IngredientesActivity.java: Pantalla del catalogo de ingredientes para consultar en cuales recetas se utiliza cada insumo.
* AjustesActivity.java: Pantalla de configuracion global, porciones por defecto y exportacion masiva.

---

### Recursos de Base de Datos (app/src/main/res/raw)
* esquema.sql: Script SQL con las sentencias de creacion de las 6 tablas relacionales de la base de datos.
* semilla.sql: Script SQL con los datos de inicio de 15 recetas tipicas mexicanas.

---

### Pruebas Unitarias (app/src/test/java/com/example/recetario/util)
* FormatoTest.java: Pruebas unitarias JUnit para el formateo de numeros, fracciones y tiempos.
* ListaComprasTest.java: Pruebas unitarias JUnit para el agrupamiento y suma de insumos de la lista de supermercado.
* SemanaTest.java: Pruebas unitarias JUnit para los calculos de fechas y rangos semanales.
* ScriptSqlTest.java: Pruebas unitarias JUnit para el parser de scripts SQL.
