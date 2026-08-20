INSERT INTO usuarios (username, password, rol)
SELECT 'admin', '123456', 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin');

INSERT INTO especialidades (nombre, descripcion)
SELECT 'Medicina General', 'Atencion medica general' WHERE NOT EXISTS (SELECT 1 FROM especialidades WHERE nombre = 'Medicina General');

INSERT INTO especialidades (nombre, descripcion)
SELECT 'Pediatria', 'Atencion medica infantil' WHERE NOT EXISTS (SELECT 1 FROM especialidades WHERE nombre = 'Pediatria');

INSERT INTO especialidades (nombre, descripcion)
SELECT 'Odontologia', 'Salud dental' WHERE NOT EXISTS (SELECT 1 FROM especialidades WHERE nombre = 'Odontologia');
