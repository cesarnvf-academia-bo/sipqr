-- ODD-002 / ODD-003: Datos iniciales SIPQR
-- Claves de usuarios almacenadas con BCrypt (datos de desarrollo).
-- Los datos de configuracionqr se almacenan en texto plano, tal como se ingresan.

INSERT INTO usuario (usuario, clave, estado)
VALUES
    ('administrador', '$2a$10$Ko53Em.9.ZFoDJMSN2f51uZ582TPU6j09ZlcOqDdL9ERnPRwas75W', TRUE),
    ('operador', '$2a$10$hO3cG34NJMA6Y1dMRN18dO3JlZizE2k2qFjRymXR1yYMmQC4UH/x.', TRUE)
ON CONFLICT (usuario) DO NOTHING;

INSERT INTO configuracionqr (id, "userName", "password", keyencript, account, url, fecha_actualizacion)
VALUES (
    1,
    'demo_usuario',
    'demo_password',
    'demo_clave_aes',
    'demo_cuenta',
    'https://demo.baneco.com.bo',
    NOW()
)
ON CONFLICT (id) DO UPDATE
SET "userName" = EXCLUDED."userName",
    "password" = EXCLUDED."password",
    keyencript = EXCLUDED.keyencript,
    account = EXCLUDED.account,
    url = EXCLUDED.url,
    fecha_actualizacion = NOW();
