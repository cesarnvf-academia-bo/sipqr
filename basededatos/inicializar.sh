#!/usr/bin/env bash
set -Eeuo pipefail

DIRECTORIO_SCRIPT="$(cd -- "$(dirname -- "${BASH_SOURCE[0]}")" && pwd)"

BD_NOMBRE="${BD_NOMBRE:-sipprbd}"
BD_USUARIO="${BD_USUARIO:-postgres}"
BD_HOST="${BD_HOST:-localhost}"
BD_PUERTO="${BD_PUERTO:-5432}"
BD_CLAVE="${BD_CLAVE:-123qwe}"

export PGPASSWORD="${BD_CLAVE}"

dropdb -h "${BD_HOST}" -p "${BD_PUERTO}" -U "${BD_USUARIO}" --if-exists --force "${BD_NOMBRE}"
createdb -h "${BD_HOST}" -p "${BD_PUERTO}" -U "${BD_USUARIO}" -E UTF8 "${BD_NOMBRE}"
psql -h "${BD_HOST}" -p "${BD_PUERTO}" -U "${BD_USUARIO}" -d "${BD_NOMBRE}" -v ON_ERROR_STOP=1 -f "${DIRECTORIO_SCRIPT}/tablas.sql"
psql -h "${BD_HOST}" -p "${BD_PUERTO}" -U "${BD_USUARIO}" -d "${BD_NOMBRE}" -v ON_ERROR_STOP=1 -f "${DIRECTORIO_SCRIPT}/datos.sql"
