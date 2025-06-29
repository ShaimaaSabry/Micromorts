#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ACTIVITIES_FILE="$SCRIPT_DIR/activities.json"

create_qdrant_collection() {
  echo "📦 Creating Qdrant collection: 'activities'..."

  curl --silent --location --request PUT 'http://localhost:6333/collections/activities' \
    --header 'Content-Type: application/json' \
    --data '{
      "vectors": {
        "size": 1536,
        "distance": "Cosine"
      }
    }'

  echo "✅ Collection setup attempt complete."
}

seed_activities_data() {
  echo "📝 Seeding activities..."

  if [[ ! -f "$ACTIVITIES_FILE" ]]; then
    echo "⚠️  activities.json not found, skipping seeding."
    return
  fi

  curl --silent --location 'http://localhost:8080/v1/activities' \
    --header 'Content-Type: application/json' \
    --data "@${ACTIVITIES_FILE}"

  echo "✅ Activities seed attempt complete."
}

# 🔁 Main Execution
create_qdrant_collection
seed_activities_data
