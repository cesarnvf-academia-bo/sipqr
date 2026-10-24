-- ODD-002: Datos iniciales tabla usuario
-- Claves almacenadas con BCrypt (datos de desarrollo)

INSERT INTO usuario (usuario, clave, estado)
VALUES
    ('administrador', '$2a$10$OqM1idoEvvVkYrvszHJg.ejRYmG49c8pd/UkOfrX.2oa9pV0EIjbO', TRUE),
    ('operador', '$2a$10$ZZL11BIxYPABbad0sEu/BuO5sFyX1AvUsPGb.yEmxi9JtKD74H.GK', TRUE)
ON CONFLICT (usuario) DO NOTHING;
