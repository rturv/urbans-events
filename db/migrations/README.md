Guía rápida para migraciones con Flyway

- Nombre de migraciones: `V{version}__descripcion.sql` (ej. `V2__add_table_users.sql`).
- Colocar archivos SQL en esta carpeta `db/migrations`.
- El servicio `flyway` en `docker-compose-postresql.yml` aplicará las migraciones al ejecutar `docker compose up`.

Notas importantes:
- Flyway marca migraciones ya aplicadas. Si ya tienes datos persistidos en `debug/pgdata`, las migraciones solo se aplicarán si no se han aplicado antes.
- Alternativas: usar plugin `flyway-maven` en cada módulo Java o `liquibase` si prefieres control desde el código.

Cómo probar localmente (desde la raíz del proyecto):

1) Levantar Postgres y Flyway (migraciones se ejecutan automáticamente):

```powershell
docker compose -f docker-compose-postresql.yml up --build
```

2) Si ya tienes datos antiguos y quieres re-aplicar desde cero (ELIMINA DATOS):

```powershell
docker compose -f docker-compose-postresql.yml down
Remove-Item -Recurse -Force .\debug\pgdata
docker compose -f docker-compose-postresql.yml up --build
```

3) Para añadir una nueva migración: crear `V3__descripcion.sql` con cambios y volver a ejecutar Flyway.
