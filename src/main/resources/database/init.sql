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

INSERT INTO seccion (nombre, horas_necesarias) VALUES ('Horno', 8);
INSERT INTO seccion (nombre, horas_necesarias) VALUES ('Cajas', 16);
INSERT INTO seccion (nombre, horas_necesarias) VALUES ('Pescaderia', 16);
INSERT INTO seccion (nombre, horas_necesarias) VALUES ('Verduras', 16);
INSERT INTO seccion (nombre, horas_necesarias) VALUES ('Drogueria', 16);

INSERT INTO tienda (codigo, nombre) VALUES (1, 'Madrid centro');
INSERT INTO tienda (codigo, nombre) VALUES (2, 'Valencia centro');
INSERT INTO tienda (codigo, nombre) VALUES (3, 'Barcelona centro');

INSERT INTO tienda_seccion (tienda, seccion) VALUES (1, 'Horno');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (1, 'Cajas');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (1, 'Pescaderia');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (1, 'Verduras');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (1, 'Drogueria');

INSERT INTO tienda_seccion (tienda, seccion) VALUES (2, 'Horno');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (2, 'Cajas');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (2, 'Pescaderia');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (2, 'Verduras');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (2, 'Drogueria');

INSERT INTO tienda_seccion (tienda, seccion) VALUES (3, 'Horno');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (3, 'Cajas');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (3, 'Pescaderia');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (3, 'Verduras');
INSERT INTO tienda_seccion (tienda, seccion) VALUES (3, 'Drogueria');

INSERT INTO trabajador (dni, nombre, apellidos, tienda, horas_disponibles) VALUES ('12345678A', 'Diego', 'Rodríguez Barrera', 1, 8);

COMMIT;
