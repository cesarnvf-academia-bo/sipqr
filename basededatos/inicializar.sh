#!/usr/bin/env bash
set -Eeuo pipefail

BD_NOMBRE="${BD_NOMBRE:-sipprbd}"
BD_USUARIO="${BD_USUARIO:-postgres}"
BD_HOST="${BD_HOST:-localhost}"
BD_PUERTO="${BD_PUERTO:-5432}"
BD_CLAVE="${BD_CLAVE:-123qwe}"

export PGPASSWORD="${BD_CLAVE}"

echo "[INFO] Verificando disponibilidad de PostgreSQL..."
if ! pg_isready -h "${BD_HOST}" -p "${BD_PUERTO}" -q; then
    echo "[ERROR] PostgreSQL no está disponible en ${BD_HOST}:${BD_PUERTO}" >&2
    exit 1
fi

echo "[INFO] Verificando herramientas requeridas..."
for herr in psql dropdb createdb; do
    if ! command -v "${herr}" >/dev/null 2>&1; then
        echo "[ERROR] Herramienta no encontrada: ${herr}" >&2
        exit 1
    fi
done

echo "[INFO] Verificando nombre de base de datos..."
if [[ -z "${BD_NOMBRE}" ]]; then
    echo "[ERROR] BD_NOMBRE no está definido" >&2
    exit 1
fi
case "${BD_NOMBRE}" in
    postgres|template0|template1)
        echo "[ERROR] Nombre de base de datos protegido: ${BD_NOMBRE}" >&2
        exit 1
        ;;
esac

echo "[ADVERTENCIA] Se eliminará y recreará la base de datos: ${BD_NOMBRE}"
echo "[ADVERTENCIA] Esto destruirá todos los datos existentes."
read -rp "¿Desea continuar? (escriba SI para confirmar): " CONFIRMAR
if [[ "${CONFIRMAR}" != "SI" ]]; then
    echo "[INFO] Operación cancelada."
    exit 0
fi

echo "[INFO] Cerrando conexiones existentes..."
psql -h "${BD_HOST}" -p "${BD_PUERTO}" -U "${BD_USUARIO}" -d postgres -v ON_ERROR_STOP=1 -c "SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE datname='${BD_NOMBRE}' AND pid <> pg_backend_pid();" >/dev/null || true

echo "[INFO] Eliminando base de datos anterior..."
dropdb -h "${BD_HOST}" -p "${BD_PUERTO}" -U "${BD_USUARIO}" --if-exists "${BD_NOMBRE}" || true

echo "[INFO] Creando base de datos con codificación UTF-8..."
createdb -h "${BD_HOST}" -p "${BD_PUERTO}" -U "${BD_USUARIO}" -E UTF8 "${BD_NOMBRE}"

echo "[INFO] Ejecutando tablas.sql..."
psql -h "${BD_HOST}" -p "${BD_PUERTO}" -U "${BD_USUARIO}" -d "${BD_NOMBRE}" -v ON_ERROR_STOP=1 -f tablas.sql

echo "[INFO] Ejecutando datos.sql..."
psql -h "${BD_HOST}" -p "${BD_PUERTO}" -U "${BD_USUARIO}" -d "${BD_NOMBRE}" -v ON_ERROR_STOP=1 -f datos.sql

echo "[INFO] Proceso finalizado correctamente."
echo "[INFO] Base de datos: ${BD_NOMBRE}"
