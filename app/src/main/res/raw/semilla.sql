-- ==========================================================
-- Recetario México Típico - Datos iniciales (menú del restaurante)
-- ==========================================================

INSERT INTO categorias (nombre, color) VALUES ('Desayunos', '#D19A2A');
INSERT INTO categorias (nombre, color) VALUES ('Entradas', '#5DA193');
INSERT INTO categorias (nombre, color) VALUES ('Sopas y Caldos', '#C9784A');
INSERT INTO categorias (nombre, color) VALUES ('Platos Fuertes', '#D75B57');
INSERT INTO categorias (nombre, color) VALUES ('Bebidas', '#5B4F8F');
INSERT INTO categorias (nombre, color) VALUES ('Postres', '#B9739C');

INSERT INTO ingredientes (nombre) VALUES ('Aceite vegetal');
INSERT INTO ingredientes (nombre) VALUES ('Agua');
INSERT INTO ingredientes (nombre) VALUES ('Aguacate');
INSERT INTO ingredientes (nombre) VALUES ('Ajo');
INSERT INTO ingredientes (nombre) VALUES ('Ajonjolí');
INSERT INTO ingredientes (nombre) VALUES ('Almendra');
INSERT INTO ingredientes (nombre) VALUES ('Arroz');
INSERT INTO ingredientes (nombre) VALUES ('Azúcar');
INSERT INTO ingredientes (nombre) VALUES ('Cacahuate');
INSERT INTO ingredientes (nombre) VALUES ('Café molido');
INSERT INTO ingredientes (nombre) VALUES ('Calabacita');
INSERT INTO ingredientes (nombre) VALUES ('Caldo de pollo');
INSERT INTO ingredientes (nombre) VALUES ('Canela en raja');
INSERT INTO ingredientes (nombre) VALUES ('Canela molida');
INSERT INTO ingredientes (nombre) VALUES ('Carne molida de res y cerdo');
INSERT INTO ingredientes (nombre) VALUES ('Cebolla blanca');
INSERT INTO ingredientes (nombre) VALUES ('Chile ancho');
INSERT INTO ingredientes (nombre) VALUES ('Chile chipotle adobado');
INSERT INTO ingredientes (nombre) VALUES ('Chile de árbol');
INSERT INTO ingredientes (nombre) VALUES ('Chile en polvo');
INSERT INTO ingredientes (nombre) VALUES ('Chile guajillo');
INSERT INTO ingredientes (nombre) VALUES ('Chile mulato');
INSERT INTO ingredientes (nombre) VALUES ('Chile pasilla');
INSERT INTO ingredientes (nombre) VALUES ('Chile poblano');
INSERT INTO ingredientes (nombre) VALUES ('Chile serrano');
INSERT INTO ingredientes (nombre) VALUES ('Chocolate de mesa');
INSERT INTO ingredientes (nombre) VALUES ('Cilantro');
INSERT INTO ingredientes (nombre) VALUES ('Clavo de olor');
INSERT INTO ingredientes (nombre) VALUES ('Crema');
INSERT INTO ingredientes (nombre) VALUES ('Durazno');
INSERT INTO ingredientes (nombre) VALUES ('Ejote');
INSERT INTO ingredientes (nombre) VALUES ('Elote');
INSERT INTO ingredientes (nombre) VALUES ('Epazote');
INSERT INTO ingredientes (nombre) VALUES ('Flor de jamaica');
INSERT INTO ingredientes (nombre) VALUES ('Garbanzo cocido');
INSERT INTO ingredientes (nombre) VALUES ('Granada');
INSERT INTO ingredientes (nombre) VALUES ('Hielo');
INSERT INTO ingredientes (nombre) VALUES ('Huevo');
INSERT INTO ingredientes (nombre) VALUES ('Jitomate');
INSERT INTO ingredientes (nombre) VALUES ('Leche');
INSERT INTO ingredientes (nombre) VALUES ('Leche condensada');
INSERT INTO ingredientes (nombre) VALUES ('Leche evaporada');
INSERT INTO ingredientes (nombre) VALUES ('Lima');
INSERT INTO ingredientes (nombre) VALUES ('Limón');
INSERT INTO ingredientes (nombre) VALUES ('Maciza de cerdo');
INSERT INTO ingredientes (nombre) VALUES ('Manteca de cerdo');
INSERT INTO ingredientes (nombre) VALUES ('Mantequilla');
INSERT INTO ingredientes (nombre) VALUES ('Manzana');
INSERT INTO ingredientes (nombre) VALUES ('Mayonesa');
INSERT INTO ingredientes (nombre) VALUES ('Muslo de pollo');
INSERT INTO ingredientes (nombre) VALUES ('Naranja');
INSERT INTO ingredientes (nombre) VALUES ('Nuez de Castilla');
INSERT INTO ingredientes (nombre) VALUES ('Orégano seco');
INSERT INTO ingredientes (nombre) VALUES ('Pasas');
INSERT INTO ingredientes (nombre) VALUES ('Pechuga de pollo');
INSERT INTO ingredientes (nombre) VALUES ('Pera');
INSERT INTO ingredientes (nombre) VALUES ('Perejil');
INSERT INTO ingredientes (nombre) VALUES ('Piloncillo');
INSERT INTO ingredientes (nombre) VALUES ('Pimiento verde');
INSERT INTO ingredientes (nombre) VALUES ('Plátano macho');
INSERT INTO ingredientes (nombre) VALUES ('Pollo en piezas');
INSERT INTO ingredientes (nombre) VALUES ('Queso cotija');
INSERT INTO ingredientes (nombre) VALUES ('Queso crema');
INSERT INTO ingredientes (nombre) VALUES ('Queso de cabra');
INSERT INTO ingredientes (nombre) VALUES ('Queso fresco');
INSERT INTO ingredientes (nombre) VALUES ('Sal');
INSERT INTO ingredientes (nombre) VALUES ('Salsa verde');
INSERT INTO ingredientes (nombre) VALUES ('Tomate verde');
INSERT INTO ingredientes (nombre) VALUES ('Tortilla de maíz');
INSERT INTO ingredientes (nombre) VALUES ('Totopos');
INSERT INTO ingredientes (nombre) VALUES ('Vainilla');
INSERT INTO ingredientes (nombre) VALUES ('Zanahoria');

-- Chilaquiles Rojos
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Chilaquiles Rojos', (SELECT id FROM categorias WHERE nombre = 'Desayunos'), 1, 30, 4, 'Corta las tortillas en triángulos y fríelas en el aceite caliente hasta que estén doradas. Escúrrelas sobre papel absorbente.
Desvena los chiles guajillo y de árbol, tuéstalos unos segundos en el comal y remójalos en agua caliente por 10 minutos.
Asa los jitomates y licúalos con los chiles, el ajo, media cebolla y una taza del agua de remojo. Sazona con sal.
Fríe la salsa en una cazuela con una cucharada de aceite y deja que hierva 8 minutos.
Agrega los totopos a la salsa, revuelve con cuidado y apaga el fuego al minuto para que queden crujientes.
Sirve con crema, queso desmoronado, aros de cebolla y un huevo estrellado encima.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 900000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Tortilla de maíz'), 16, 'pz', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Jitomate'), 4, 'pz', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Chile guajillo'), 4, 'pz', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Chile de árbol'), 2, 'pz', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Ajo'), 2, 'diente', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Cebolla blanca'), 1, 'pz', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Aceite vegetal'), 250, 'ml', 6);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Crema'), 150, 'ml', 7);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Queso fresco'), 120, 'g', 8);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Huevo'), 4, 'pz', 9);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chilaquiles Rojos'), (SELECT id FROM ingredientes WHERE nombre = 'Sal'), 0, 'al gusto', 10);

-- Huevos a la Mexicana
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Huevos a la Mexicana', (SELECT id FROM categorias WHERE nombre = 'Desayunos'), 1, 15, 2, 'Pica finamente el jitomate, la cebolla y el chile serrano.
Sofríe la cebolla y el chile en el aceite por 2 minutos y agrega el jitomate. Cocina 3 minutos más.
Bate los huevos con sal, viértelos en el sartén y revuelve a fuego medio hasta que estén cocidos.
Sirve de inmediato con tortillas calientes.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 840000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Huevos a la Mexicana'), (SELECT id FROM ingredientes WHERE nombre = 'Huevo'), 4, 'pz', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Huevos a la Mexicana'), (SELECT id FROM ingredientes WHERE nombre = 'Jitomate'), 1, 'pz', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Huevos a la Mexicana'), (SELECT id FROM ingredientes WHERE nombre = 'Cebolla blanca'), 0.5, 'pz', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Huevos a la Mexicana'), (SELECT id FROM ingredientes WHERE nombre = 'Chile serrano'), 1, 'pz', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Huevos a la Mexicana'), (SELECT id FROM ingredientes WHERE nombre = 'Aceite vegetal'), 1, 'cda', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Huevos a la Mexicana'), (SELECT id FROM ingredientes WHERE nombre = 'Tortilla de maíz'), 6, 'pz', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Huevos a la Mexicana'), (SELECT id FROM ingredientes WHERE nombre = 'Sal'), 0, 'al gusto', 6);

-- Guacamole con Totopos
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Guacamole con Totopos', (SELECT id FROM categorias WHERE nombre = 'Entradas'), 1, 15, 4, 'Pica finamente el jitomate sin semillas, la cebolla, el cilantro y el chile serrano.
Parte los aguacates, saca la pulpa y macháchala con un tenedor o en el molcajete, dejando algunos trozos.
Mezcla el aguacate con lo picado, el jugo de limón y sal al gusto.
Sirve al momento acompañado de los totopos.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 780000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Guacamole con Totopos'), (SELECT id FROM ingredientes WHERE nombre = 'Aguacate'), 3, 'pz', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Guacamole con Totopos'), (SELECT id FROM ingredientes WHERE nombre = 'Jitomate'), 1, 'pz', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Guacamole con Totopos'), (SELECT id FROM ingredientes WHERE nombre = 'Cebolla blanca'), 0.25, 'pz', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Guacamole con Totopos'), (SELECT id FROM ingredientes WHERE nombre = 'Cilantro'), 6, 'rama', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Guacamole con Totopos'), (SELECT id FROM ingredientes WHERE nombre = 'Chile serrano'), 1, 'pz', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Guacamole con Totopos'), (SELECT id FROM ingredientes WHERE nombre = 'Limón'), 1, 'pz', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Guacamole con Totopos'), (SELECT id FROM ingredientes WHERE nombre = 'Totopos'), 200, 'g', 6);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Guacamole con Totopos'), (SELECT id FROM ingredientes WHERE nombre = 'Sal'), 0, 'al gusto', 7);

-- Elotes Asados
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Elotes Asados', (SELECT id FROM categorias WHERE nombre = 'Entradas'), 1, 30, 4, 'Asa los elotes en el comal o la parrilla de 15 a 20 minutos, girándolos para que se doren parejo.
Úntalos con mantequilla y después con una capa delgada de mayonesa.
Espolvorea el queso cotija rallado y el chile en polvo.
Sirve con mitades de limón.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 720000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Elotes Asados'), (SELECT id FROM ingredientes WHERE nombre = 'Elote'), 4, 'pz', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Elotes Asados'), (SELECT id FROM ingredientes WHERE nombre = 'Mantequilla'), 30, 'g', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Elotes Asados'), (SELECT id FROM ingredientes WHERE nombre = 'Mayonesa'), 4, 'cda', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Elotes Asados'), (SELECT id FROM ingredientes WHERE nombre = 'Queso cotija'), 80, 'g', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Elotes Asados'), (SELECT id FROM ingredientes WHERE nombre = 'Chile en polvo'), 1, 'cdita', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Elotes Asados'), (SELECT id FROM ingredientes WHERE nombre = 'Limón'), 2, 'pz', 5);

-- Sopa de Lima
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Sopa de Lima', (SELECT id FROM categorias WHERE nombre = 'Sopas y Caldos'), 2, 50, 4, 'Cuece la pechuga en el caldo con el ajo y el orégano durante 25 minutos. Sácala y deshébrala.
Sofríe la cebolla, el pimiento y el jitomate picados hasta que se suavicen.
Agrega el sofrito al caldo junto con el pollo y el jugo de dos limas. Deja hervir 10 minutos y rectifica la sal.
Corta las tortillas en tiras y fríelas en el aceite hasta que estén crujientes.
Sirve la sopa con las tiras de tortilla, rodajas de lima y cilantro picado.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 660000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Pechuga de pollo'), 500, 'g', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Caldo de pollo'), 1.5, 'l', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Lima'), 3, 'pz', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Jitomate'), 2, 'pz', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Cebolla blanca'), 1, 'pz', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Pimiento verde'), 1, 'pz', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Ajo'), 2, 'diente', 6);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Orégano seco'), 1, 'cdita', 7);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Tortilla de maíz'), 6, 'pz', 8);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Aceite vegetal'), 100, 'ml', 9);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Cilantro'), 4, 'rama', 10);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Sopa de Lima'), (SELECT id FROM ingredientes WHERE nombre = 'Sal'), 0, 'al gusto', 11);

-- Caldo Tlalpeño
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Caldo Tlalpeño', (SELECT id FROM categorias WHERE nombre = 'Sopas y Caldos'), 2, 60, 4, 'Cuece el pollo en el agua con media cebolla, un diente de ajo y sal durante 30 minutos.
Licúa el jitomate con los chipotles, el resto de la cebolla y el ajo. Fríe la salsa 5 minutos.
Agrega la salsa al caldo junto con la zanahoria, el ejote y el garbanzo. Cocina 15 minutos.
Añade la calabacita y el epazote, y deja 8 minutos más.
Sirve con cubos de aguacate y limón.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 600000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Muslo de pollo'), 600, 'g', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Agua'), 2, 'l', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Garbanzo cocido'), 250, 'g', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Zanahoria'), 2, 'pz', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Calabacita'), 2, 'pz', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Ejote'), 100, 'g', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Chile chipotle adobado'), 2, 'pz', 6);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Jitomate'), 2, 'pz', 7);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Cebolla blanca'), 1, 'pz', 8);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Ajo'), 2, 'diente', 9);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Epazote'), 1, 'rama', 10);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Aguacate'), 1, 'pz', 11);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Limón'), 2, 'pz', 12);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Caldo Tlalpeño'), (SELECT id FROM ingredientes WHERE nombre = 'Sal'), 0, 'al gusto', 13);

-- Chile en Nogada
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Chile en Nogada', (SELECT id FROM categorias WHERE nombre = 'Platos Fuertes'), 3, 120, 6, 'Asa los chiles directo al fuego, mételos 10 minutos en una bolsa para que suden y pélalos. Haz un corte a lo largo y quita semillas y venas con cuidado.
Para el picadillo, sofríe la cebolla y el ajo en el aceite, agrega la carne y cocina hasta que dore.
Añade el jitomate picado, las frutas en cubos, las pasas y las almendras. Cocina 15 minutos y sazona con sal.
Para la nogada, pela las nueces (remojadas en leche desde la noche anterior) y licúalas con el queso de cabra, la leche y el azúcar hasta tener una salsa tersa.
Rellena los chiles con el picadillo, báñalos con la nogada y decora con granos de granada y hojas de perejil.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 540000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Chile poblano'), 6, 'pz', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Carne molida de res y cerdo'), 500, 'g', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Manzana'), 1, 'pz', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Pera'), 1, 'pz', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Durazno'), 1, 'pz', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Plátano macho'), 1, 'pz', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Pasas'), 30, 'g', 6);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Almendra'), 30, 'g', 7);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Jitomate'), 2, 'pz', 8);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Cebolla blanca'), 1, 'pz', 9);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Ajo'), 2, 'diente', 10);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Aceite vegetal'), 2, 'cda', 11);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Nuez de Castilla'), 200, 'g', 12);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Queso de cabra'), 90, 'g', 13);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Leche'), 250, 'ml', 14);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Azúcar'), 1, 'cda', 15);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Granada'), 1, 'pz', 16);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Perejil'), 4, 'rama', 17);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), (SELECT id FROM ingredientes WHERE nombre = 'Sal'), 0, 'al gusto', 18);
INSERT INTO favoritos (receta_id, agregada_en) VALUES ((SELECT id FROM recetas WHERE nombre = 'Chile en Nogada'), CAST(strftime('%s', 'now') AS INTEGER) * 1000);

-- Enchiladas Verdes
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Enchiladas Verdes', (SELECT id FROM categorias WHERE nombre = 'Platos Fuertes'), 2, 45, 4, 'Cuece la pechuga en agua con sal y un trozo de cebolla por 25 minutos. Deshébrala.
Hierve los tomates y los chiles 10 minutos. Licúalos con el ajo, un trozo de cebolla y el cilantro.
Fríe la salsa en una cucharada de aceite y deja que hierva 5 minutos. Sazona con sal.
Pasa las tortillas por aceite caliente, rellénalas con pollo y enróllalas.
Acomódalas en el plato, báñalas con la salsa caliente y decora con crema, queso y aros de cebolla.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 480000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Pechuga de pollo'), 500, 'g', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Tortilla de maíz'), 12, 'pz', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Tomate verde'), 500, 'g', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Chile serrano'), 2, 'pz', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Cebolla blanca'), 1, 'pz', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Ajo'), 1, 'diente', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Cilantro'), 5, 'rama', 6);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Aceite vegetal'), 3, 'cda', 7);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Crema'), 150, 'ml', 8);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Queso fresco'), 100, 'g', 9);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Enchiladas Verdes'), (SELECT id FROM ingredientes WHERE nombre = 'Sal'), 0, 'al gusto', 10);

-- Mole Poblano
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Mole Poblano', (SELECT id FROM categorias WHERE nombre = 'Platos Fuertes'), 3, 150, 6, 'Cuece el pollo en agua con sal y un trozo de cebolla durante 40 minutos. Guarda el caldo.
Desvena los chiles, fríelos unos segundos en la manteca y remójalos en caldo caliente.
En la misma manteca fríe por separado la tortilla, el plátano, las almendras, el cacahuate, las pasas y dos cucharadas de ajonjolí.
Asa el jitomate, la cebolla y el ajo. Licúa todo con los chiles, la canela y el caldo necesario hasta tener una pasta lisa.
Fríe la pasta en manteca a fuego bajo, moviendo sin parar, durante 20 minutos. Agrega el chocolate, el azúcar y caldo hasta lograr la consistencia deseada.
Incorpora el pollo, cocina 15 minutos más y sirve con el ajonjolí restante encima.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 420000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Pollo en piezas'), 1.2, 'kg', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Chile mulato'), 4, 'pz', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Chile ancho'), 4, 'pz', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Chile pasilla'), 3, 'pz', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Jitomate'), 2, 'pz', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Cebolla blanca'), 0.5, 'pz', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Ajo'), 3, 'diente', 6);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Ajonjolí'), 3, 'cda', 7);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Cacahuate'), 40, 'g', 8);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Almendra'), 40, 'g', 9);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Pasas'), 30, 'g', 10);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Tortilla de maíz'), 1, 'pz', 11);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Plátano macho'), 0.5, 'pz', 12);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Canela en raja'), 1, 'pz', 13);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Chocolate de mesa'), 45, 'g', 14);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Manteca de cerdo'), 3, 'cda', 15);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Caldo de pollo'), 1, 'l', 16);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Azúcar'), 1, 'cda', 17);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Mole Poblano'), (SELECT id FROM ingredientes WHERE nombre = 'Sal'), 0, 'al gusto', 18);

-- Tacos de Carnitas
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Tacos de Carnitas', (SELECT id FROM categorias WHERE nombre = 'Platos Fuertes'), 2, 180, 6, 'Corta la carne en trozos grandes y sazónala con la sal.
Derrite la manteca en una olla gruesa, agrega la carne, los ajos y el jugo de las naranjas con sus cáscaras.
Cocina a fuego bajo durante 2 horas y media, moviendo de vez en cuando, hasta que la carne esté suave.
Sube el fuego los últimos 15 minutos para que la carne dore por fuera.
Pica la carne y sírvela en tortillas calientes con cebolla, cilantro, salsa verde y limón.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 360000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), (SELECT id FROM ingredientes WHERE nombre = 'Maciza de cerdo'), 1.5, 'kg', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), (SELECT id FROM ingredientes WHERE nombre = 'Manteca de cerdo'), 500, 'g', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), (SELECT id FROM ingredientes WHERE nombre = 'Naranja'), 2, 'pz', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), (SELECT id FROM ingredientes WHERE nombre = 'Ajo'), 4, 'diente', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), (SELECT id FROM ingredientes WHERE nombre = 'Sal'), 1, 'cda', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), (SELECT id FROM ingredientes WHERE nombre = 'Tortilla de maíz'), 18, 'pz', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), (SELECT id FROM ingredientes WHERE nombre = 'Cebolla blanca'), 1, 'pz', 6);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), (SELECT id FROM ingredientes WHERE nombre = 'Cilantro'), 10, 'rama', 7);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), (SELECT id FROM ingredientes WHERE nombre = 'Salsa verde'), 250, 'ml', 8);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), (SELECT id FROM ingredientes WHERE nombre = 'Limón'), 3, 'pz', 9);
INSERT INTO favoritos (receta_id, agregada_en) VALUES ((SELECT id FROM recetas WHERE nombre = 'Tacos de Carnitas'), CAST(strftime('%s', 'now') AS INTEGER) * 1000);

-- Agua de Jamaica
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Agua de Jamaica', (SELECT id FROM categorias WHERE nombre = 'Bebidas'), 1, 20, 8, 'Hierve medio litro de agua con la flor de jamaica durante 5 minutos.
Deja reposar 10 minutos y cuela el concentrado.
Agrega el resto del agua y el azúcar, y mezcla hasta que se disuelva.
Sirve bien fría con hielo.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 300000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Jamaica'), (SELECT id FROM ingredientes WHERE nombre = 'Flor de jamaica'), 60, 'g', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Jamaica'), (SELECT id FROM ingredientes WHERE nombre = 'Agua'), 2.5, 'l', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Jamaica'), (SELECT id FROM ingredientes WHERE nombre = 'Azúcar'), 150, 'g', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Jamaica'), (SELECT id FROM ingredientes WHERE nombre = 'Hielo'), 0, 'al gusto', 3);

-- Agua de Horchata
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Agua de Horchata', (SELECT id FROM categorias WHERE nombre = 'Bebidas'), 1, 20, 8, 'Remoja el arroz con la canela en dos tazas de agua por lo menos 4 horas.
Licúa el arroz con la canela y el agua del remojo hasta que quede muy fino.
Cuela y agrega el resto del agua, la leche, el azúcar y la vainilla.
Mezcla bien y sirve con hielo.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 240000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Horchata'), (SELECT id FROM ingredientes WHERE nombre = 'Arroz'), 1, 'taza', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Horchata'), (SELECT id FROM ingredientes WHERE nombre = 'Canela en raja'), 1, 'pz', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Horchata'), (SELECT id FROM ingredientes WHERE nombre = 'Agua'), 2, 'l', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Horchata'), (SELECT id FROM ingredientes WHERE nombre = 'Leche'), 250, 'ml', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Horchata'), (SELECT id FROM ingredientes WHERE nombre = 'Azúcar'), 150, 'g', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Horchata'), (SELECT id FROM ingredientes WHERE nombre = 'Vainilla'), 1, 'cdita', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Agua de Horchata'), (SELECT id FROM ingredientes WHERE nombre = 'Hielo'), 0, 'al gusto', 6);

-- Café de Olla
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Café de Olla', (SELECT id FROM categorias WHERE nombre = 'Bebidas'), 1, 15, 4, 'Pon a hervir el agua con el piloncillo, la canela y los clavos en una olla de barro.
Cuando el piloncillo se disuelva, apaga el fuego y agrega el café.
Tapa y deja reposar 5 minutos.
Cuela y sirve caliente.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 180000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Café de Olla'), (SELECT id FROM ingredientes WHERE nombre = 'Agua'), 1, 'l', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Café de Olla'), (SELECT id FROM ingredientes WHERE nombre = 'Café molido'), 4, 'cda', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Café de Olla'), (SELECT id FROM ingredientes WHERE nombre = 'Piloncillo'), 80, 'g', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Café de Olla'), (SELECT id FROM ingredientes WHERE nombre = 'Canela en raja'), 1, 'pz', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Café de Olla'), (SELECT id FROM ingredientes WHERE nombre = 'Clavo de olor'), 2, 'pz', 4);
INSERT INTO favoritos (receta_id, agregada_en) VALUES ((SELECT id FROM recetas WHERE nombre = 'Café de Olla'), CAST(strftime('%s', 'now') AS INTEGER) * 1000);

-- Flan Napolitano
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Flan Napolitano', (SELECT id FROM categorias WHERE nombre = 'Postres'), 2, 75, 8, 'Derrite el azúcar en un sartén a fuego medio, sin moverla, hasta que tome color dorado. Vierte el caramelo en la flanera.
Licúa la leche condensada, la leche evaporada, el queso crema, los huevos y la vainilla.
Vierte la mezcla sobre el caramelo, cubre con papel aluminio y hornea a baño María a 180 °C durante 60 minutos.
Deja enfriar y refrigera mínimo 4 horas.
Pasa un cuchillo por la orilla y desmolda sobre un platón.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 120000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Flan Napolitano'), (SELECT id FROM ingredientes WHERE nombre = 'Azúcar'), 150, 'g', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Flan Napolitano'), (SELECT id FROM ingredientes WHERE nombre = 'Leche condensada'), 387, 'g', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Flan Napolitano'), (SELECT id FROM ingredientes WHERE nombre = 'Leche evaporada'), 360, 'ml', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Flan Napolitano'), (SELECT id FROM ingredientes WHERE nombre = 'Queso crema'), 190, 'g', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Flan Napolitano'), (SELECT id FROM ingredientes WHERE nombre = 'Huevo'), 5, 'pz', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Flan Napolitano'), (SELECT id FROM ingredientes WHERE nombre = 'Vainilla'), 1, 'cdita', 5);

-- Arroz con Leche
INSERT INTO recetas (nombre, categoria_id, dificultad, tiempo_min, porciones, preparacion, foto, creada_en) VALUES ('Arroz con Leche', (SELECT id FROM categorias WHERE nombre = 'Postres'), 1, 40, 6, 'Cuece el arroz en el agua con la canela hasta que el agua se consuma.
Agrega la leche y cocina a fuego bajo 20 minutos, moviendo seguido para que no se pegue.
Añade la leche condensada, el azúcar y las pasas. Cocina 5 minutos más.
Sirve caliente o frío con canela molida encima.', NULL, CAST(strftime('%s', 'now') AS INTEGER) * 1000 - 60000);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Arroz con Leche'), (SELECT id FROM ingredientes WHERE nombre = 'Arroz'), 1, 'taza', 0);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Arroz con Leche'), (SELECT id FROM ingredientes WHERE nombre = 'Agua'), 500, 'ml', 1);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Arroz con Leche'), (SELECT id FROM ingredientes WHERE nombre = 'Canela en raja'), 1, 'pz', 2);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Arroz con Leche'), (SELECT id FROM ingredientes WHERE nombre = 'Leche'), 1, 'l', 3);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Arroz con Leche'), (SELECT id FROM ingredientes WHERE nombre = 'Leche condensada'), 200, 'g', 4);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Arroz con Leche'), (SELECT id FROM ingredientes WHERE nombre = 'Azúcar'), 50, 'g', 5);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Arroz con Leche'), (SELECT id FROM ingredientes WHERE nombre = 'Pasas'), 30, 'g', 6);
INSERT INTO receta_ingrediente (receta_id, ingrediente_id, cantidad, unidad, orden) VALUES ((SELECT id FROM recetas WHERE nombre = 'Arroz con Leche'), (SELECT id FROM ingredientes WHERE nombre = 'Canela molida'), 1, 'cdita', 7);
