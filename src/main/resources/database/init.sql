CREATE TABLE tienda (
    codigo NUMBER NOT NULL,
    nombre VARCHAR2(100) NOT NULL,
    CONSTRAINT pk_tienda PRIMARY KEY (codigo)
);

CREATE TABLE seccion (
    nombre VARCHAR2(50) NOT NULL,
    horas_necesarias NUMBER(2) NOT NULL,
    CONSTRAINT pk_seccion PRIMARY KEY (nombre)
);

CREATE TABLE aptitud (
    nombre VARCHAR2(100) NOT NULL,
    CONSTRAINT pk_aptitud PRIMARY KEY (nombre)
);

CREATE TABLE seccion_aptitud (
    seccion VARCHAR2(50) NOT NULL,
    aptitud VARCHAR2(100) NOT NULL,
    CONSTRAINT pk_seccion_aptitud PRIMARY KEY (seccion, aptitud),
    CONSTRAINT fk_sa_seccion FOREIGN KEY (seccion)
        REFERENCES seccion (nombre),
    CONSTRAINT fk_sa_aptitud FOREIGN KEY (aptitud)
        REFERENCES aptitud (nombre)
);

CREATE TABLE tienda_seccion (
    tienda NUMBER NOT NULL,
    seccion VARCHAR2(50) NOT NULL,
    CONSTRAINT pk_tienda_seccion PRIMARY KEY (tienda, seccion),
    CONSTRAINT fk_ts_tienda FOREIGN KEY (tienda)
        REFERENCES tienda (codigo),
    CONSTRAINT fk_ts_seccion FOREIGN KEY (seccion)
        REFERENCES seccion (nombre)
);

CREATE TABLE trabajador (
    dni VARCHAR2(9) NOT NULL,
    nombre VARCHAR2(100) NOT NULL,
    apellidos VARCHAR2(150) NOT NULL,
    tienda NUMBER NOT NULL,
    horas_disponibles NUMBER(2) NOT NULL,
    CONSTRAINT pk_trabajador PRIMARY KEY (dni),
    CONSTRAINT uq_trabajador_tienda UNIQUE (dni, tienda),
    CONSTRAINT fk_trabajador_tienda FOREIGN KEY (tienda)
        REFERENCES tienda (codigo),
    CONSTRAINT ck_trabajador_horas CHECK (horas_disponibles BETWEEN 0 AND 8)
);

CREATE TABLE trabajador_aptitud (
    trabajador VARCHAR2(9) NOT NULL,
    aptitud VARCHAR2(100) NOT NULL,
    CONSTRAINT pk_trabajador_aptitud PRIMARY KEY (trabajador, aptitud),
    CONSTRAINT fk_ta_trabajador FOREIGN KEY (trabajador)
        REFERENCES trabajador (dni),
    CONSTRAINT fk_ta_aptitud FOREIGN KEY (aptitud)
        REFERENCES aptitud (nombre)
);

CREATE TABLE trabajador_seccion (
    trabajador VARCHAR2(9) NOT NULL,
    tienda NUMBER NOT NULL,
    seccion VARCHAR2(50) NOT NULL,
    horas_asignadas NUMBER(2) NOT NULL,
    CONSTRAINT pk_trabajador_seccion
        PRIMARY KEY (trabajador, tienda, seccion),
    CONSTRAINT fk_trs_trabajador FOREIGN KEY (trabajador, tienda)
        REFERENCES trabajador (dni, tienda),
    CONSTRAINT fk_trs_tienda_seccion FOREIGN KEY (tienda, seccion)
        REFERENCES tienda_seccion (tienda, seccion),
    CONSTRAINT ck_trs_horas CHECK (horas_asignadas BETWEEN 1 AND 8)
);

INSERT INTO seccion (nombre, horas_necesarias) VALUES
('Horno', 8),
('Cajas', 16),
('Pescadería', 16),
('Verduras', 16),
('Droguería', 16);

INSERT INTO aptitud (nombre) VALUES
('Hornear Pan'),
('Repostería'),
('Simpatía'),
('Matemáticas'),
('Manejo de armas blancas'),
('Limpiar pescado'),
('Fortaleza física'),
('Alquimia');

INSERT INTO seccion_aptitud (seccion, aptitud) VALUES
('Horno', 'Hornear Pan'),
('Horno', 'Repostería'),
('Cajas', 'Simpatía'),
('Cajas', 'Matemáticas'),
('Pescadería', 'Manejo de armas blancas'),
('Pescadería', 'Limpiar pescado'),
('Verduras', 'Fortaleza física'),
('Droguería', 'Alquimia');

INSERT INTO tienda (codigo, nombre) VALUES
(1, 'Tienda 1'),
(2, 'Tienda 2'),
(3, 'Tienda 3');

INSERT INTO tienda_seccion (tienda, seccion) VALUES
(1, 'Horno'),
(1, 'Cajas'),
(1, 'Pescadería'),
(1, 'Verduras'),
(1, 'Droguería'),
(2, 'Horno'),
(2, 'Cajas'),
(2, 'Pescadería'),
(2, 'Verduras'),
(2, 'Droguería'),
(3, 'Horno'),
(3, 'Cajas'),
(3, 'Pescadería'),
(3, 'Verduras'),
(3, 'Droguería');

INSERT INTO trabajador (dni, nombre, apellidos, tienda, horas_disponibles) VALUES
('11111111A', 'Diego', 'Rodríguez Barrera', 1, 8),
('22222222B', 'Ismael', 'Buitrago Sánchez', 1, 8),
('33333333C', 'Carlos', 'Arias Chocano', 1, 8),
('44444444D', 'Christian', 'Castellón Hernández', 1, 8),
('55555555E', 'Sandra', 'López García', 1, 8),
('66666666F', 'Francisco José', 'Cerro Muñóz', 1, 8),
('77777777G', 'Ana', 'De las Heras Ruíz', 1, 8),
('88888888H', 'Raquel', 'Santos Cobacho', 1, 8),
('99999999I', 'Maria Isabel', 'Rey Sánchez', 1, 8);

INSERT INTO trabajador_aptitud (trabajador, aptitud) VALUES
('11111111A', 'Hornear Pan'),
('11111111A', 'Repostería'),
('11111111A', 'Simpatía'),
('11111111A', 'Matemáticas'),
('22222222B', 'Simpatía'),
('22222222B', 'Matemáticas'),
('22222222B', 'Manejo de armas blancas'),
('22222222B', 'Limpiar pescado'),
('33333333C', 'Manejo de armas blancas'),
('33333333C', 'Limpiar pescado'),
('33333333C', 'Fortaleza física'),
('44444444D', 'Fortaleza física'),
('44444444D', 'Alquimia'),
('55555555E', 'Alquimia'),
('55555555E', 'Hornear Pan'),
('55555555E', 'Repostería'),
('66666666F', 'Simpatía'),
('66666666F', 'Matemáticas'),
('66666666F', 'Manejo de armas blancas'),
('66666666F', 'Limpiar pescado'),
('77777777G', 'Fortaleza física'),
('77777777G', 'Alquimia'),
('88888888H', 'Manejo de armas blancas'),
('88888888H', 'Limpiar pescado'),
('88888888H', 'Alquimia'),
('99999999I', 'Simpatía'),
('99999999I', 'Matemáticas'),
('99999999I', 'Fortaleza física');

INSERT INTO trabajador_seccion (trabajador, tienda, seccion, horas_asignadas) VALUES
('11111111A', 1, 'Horno', 4),
('11111111A', 1, 'Cajas', 4),
('22222222B', 1, 'Cajas', 4),
('22222222B', 1, 'Pescadería', 4),
('33333333C', 1, 'Pescadería', 4),
('33333333C', 1, 'Verduras', 4),
('44444444D', 1, 'Verduras', 4),
('44444444D', 1, 'Droguería', 4),
('55555555E', 1, 'Droguería', 4),
('55555555E', 1, 'Horno', 4),
('66666666F', 1, 'Cajas', 4),
('66666666F', 1, 'Pescadería', 4),
('77777777G', 1, 'Verduras', 4),
('77777777G', 1, 'Droguería', 4),
('88888888H', 1, 'Pescadería', 4),
('88888888H', 1, 'Droguería', 4),
('99999999I', 1, 'Cajas', 4),
('99999999I', 1, 'Verduras', 4);

COMMIT;
