-- ============================================================================
-- Consultas de demostracion para mostrar la base de datos en MySQL Workbench
-- Sistema de Gestion de Citas Medicas
--
-- Como usarlas: abre MySQL Workbench, conectate a tu servidor local, abre
-- este archivo (File > Open SQL Script) y ejecuta cada bloque con Ctrl+Enter
-- (o el rayo amarillo) mientras explicas.
-- ============================================================================

USE citas_medicas;

-- 1) Panorama general: cuantos registros hay en cada tabla
SELECT 'usuarios' AS tabla, COUNT(*) AS registros FROM usuarios
UNION ALL SELECT 'pacientes', COUNT(*) FROM pacientes
UNION ALL SELECT 'medicos', COUNT(*) FROM medicos
UNION ALL SELECT 'especialidades', COUNT(*) FROM especialidades
UNION ALL SELECT 'servicios', COUNT(*) FROM servicios
UNION ALL SELECT 'citas', COUNT(*) FROM citas;

-- 2) Especialidades y sus servicios con precio (el catalogo completo)
SELECT e.nombre AS especialidad, s.nombre AS servicio, s.precio, s.duracion_estimada
FROM servicios s
JOIN especialidades e ON e.id = s.especialidad_id
ORDER BY e.nombre, s.precio;

-- 3) Personal medico por especialidad
SELECT m.nombres, m.apellidos, m.colegiatura, m.email, e.nombre AS especialidad
FROM medicos m
JOIN especialidades e ON e.id = m.especialidad_id
ORDER BY e.nombre;

-- 4) Vista completa de citas: paciente + medico + servicio + pago, todo unido
SELECT
    c.id,
    CONCAT(p.nombres, ' ', p.apellidos)  AS paciente,
    CONCAT(m.nombres, ' ', m.apellidos)  AS atendido_por,
    s.nombre                              AS servicio,
    c.fecha,
    c.hora,
    c.estado,
    CASE WHEN c.pagado THEN 'Si' ELSE 'No' END AS pagado,
    c.monto_pagado,
    c.metodo_pago
FROM citas c
JOIN pacientes p ON p.id = c.paciente_id
JOIN medicos m   ON m.id = c.medico_id
LEFT JOIN servicios s ON s.id = c.servicio_id
ORDER BY c.fecha DESC, c.hora DESC;

-- 5) Cuentas de acceso y su rol (para mostrar que cada rol es una cuenta real,
--    con la contrasena hasheada, no en texto plano)
SELECT u.id, u.username, u.rol, u.password AS password_hasheado
FROM usuarios u
ORDER BY FIELD(u.rol, 'ADMIN', 'RECEPCIONISTA', 'MEDICO', 'PACIENTE');

-- 6) Cuanto se ha facturado por especialidad (solo citas pagadas)
SELECT e.nombre AS especialidad, COUNT(*) AS citas_pagadas, SUM(c.monto_pagado) AS total_facturado
FROM citas c
JOIN medicos m ON m.id = c.medico_id
JOIN especialidades e ON e.id = m.especialidad_id
WHERE c.pagado = 1
GROUP BY e.nombre
ORDER BY total_facturado DESC;
