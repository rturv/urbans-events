#!/usr/bin/env bash
set -euo pipefail

# Script para crear los topics usados por el proyecto en entorno local
# Detecta si existe el contenedor (por defecto: kraft-kafka-debug-commented)
# y usa docker exec; si no, usa docker run con la imagen oficial.

CONTAINER="${KAFKA_CONTAINER:-kraft-kafka-debug-commented}"
BOOTSTRAP="${BOOTSTRAP_SERVER:-localhost:9092}"
TOPICS=(incidencias.creadas incidencias.priorizadas incidencias.notificadas)
PARTS="${PARTITIONS:-3}"
REPL="${REPLICATION_FACTOR:-1}"


container_exists() {
  docker ps --format '{{.Names}}' | grep -q "^${CONTAINER}$"
}

if ! container_exists; then
  echo "ERROR: contenedor '${CONTAINER}' no está en ejecución."
  echo "Levantalo y vuelve a ejecutar el script. Ejemplos de comandos de arranque:"
  echo "  docker compose up -d kafka"
  echo "  o: docker run --rm -d --name ${CONTAINER} -p 9092:9092 apache/kafka:4.1.1"
  exit 1
fi

for t in "${TOPICS[@]}"; do
  echo "Creando topic: $t (partitions=$PARTS replication=$REPL)"
  # Detectar comando kafka-topics dentro del contenedor
  KTOP_CMD=$(docker exec -i "${CONTAINER}" bash -lc '\
    if command -v kafka-topics >/dev/null 2>&1; then echo kafka-topics; \
    elif command -v kafka-topics.sh >/dev/null 2>&1; then echo kafka-topics.sh; \
    elif [ -x /opt/kafka/bin/kafka-topics.sh ]; then echo /opt/kafka/bin/kafka-topics.sh; \
    elif [ -x /usr/bin/kafka-topics.sh ]; then echo /usr/bin/kafka-topics.sh; \
    else echo ""; fi')

  if [ -z "${KTOP_CMD}" ]; then
    echo "ERROR: no se encontró 'kafka-topics' dentro del contenedor ${CONTAINER}."
    echo "Comprueba la imagen usada o ejecuta los comandos desde una imagen que incluya las utilidades de Kafka."
    exit 2
  fi

  docker exec -i "${CONTAINER}" bash -lc "${KTOP_CMD} --bootstrap-server ${BOOTSTRAP} --create --if-not-exists --topic ${t} --partitions ${PARTS} --replication-factor ${REPL}"
done

echo
echo "Listado de topics disponibles:"
run_cmd "kafka-topics --bootstrap-server ${BOOTSTRAP} --list"

echo
echo "Hecho. Si hace falta, ejecuta: chmod +x scripts/create-topics.sh"
