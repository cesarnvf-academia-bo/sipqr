# SIPQR - Sistema de Información de Pagos QR

## Estructura
- `basededatos/` - Scripts SQL y script de inicialización
- `partelogica/` - Backend Java 25 + Spring Boot 4.1.x + MyBatis XML
- `partevisual/` - Frontend Angular 22 + TypeScript 6 + Tailwind CSS 4
- `odd/` - Documentación ODD

## Base de datos
```bash
cd basededatos
chmod +x inicializar.sh
# Configurar variables opcionales: BD_NOMBRE=sipprbd BD_USUARIO=postgres BD_CLAVE=123qwe
./inicializar.sh
```

## Backend
```bash
cd partelogica
mvn clean test
mvn spring-boot:run
```
Swagger UI: http://localhost:8080/swagger-ui.html  
Verificación: http://localhost:8080/api/verificacion/estado

## Frontend
```bash
cd partevisual
npm install
npm run build
npm start
```

## Convenciones
- Español para nomenclatura propia
- Sangría de 4 espacios
- Sin JPA/Hibernate
- MyBatis con XML
