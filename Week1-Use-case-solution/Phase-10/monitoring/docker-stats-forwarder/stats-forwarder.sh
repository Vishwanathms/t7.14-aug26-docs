#!/bin/sh
set -eu

HEC_URL="http://splunk:8088/services/collector/event"
HEC_TOKEN="${SPLUNK_HEC_TOKEN:-00000000-0000-0000-0000-000000000000}"
INTERVAL="${STATS_INTERVAL:-10}"

echo "docker-stats-forwarder: sending docker stats to $HEC_URL every ${INTERVAL}s"

while true; do
  docker stats --no-stream --format '{{json .}}' | while IFS= read -r line; do
    name=$(echo "$line" | jq -r '.Name')
    cpu=$(echo "$line" | jq -r '.CPUPerc' | tr -d '%')
    mem_perc=$(echo "$line" | jq -r '.MemPerc' | tr -d '%')
    mem_usage=$(echo "$line" | jq -r '.MemUsage')
    mem_used_raw=$(echo "$mem_usage" | awk -F'/' '{print $1}' | xargs)
    mem_limit_raw=$(echo "$mem_usage" | awk -F'/' '{print $2}' | xargs)

    event=$(jq -n \
      --arg name "$name" \
      --arg cpu "$cpu" \
      --arg mem_perc "$mem_perc" \
      --arg mem_used "$mem_used_raw" \
      --arg mem_limit "$mem_limit_raw" \
      '{
        event: {
          container_name: $name,
          cpu_percent: ($cpu | tonumber),
          mem_percent: ($mem_perc | tonumber),
          mem_used: $mem_used,
          mem_limit: $mem_limit
        },
        sourcetype: "docker-stats"
      }')

    curl -s -o /dev/null "$HEC_URL" \
      -H "Authorization: Splunk $HEC_TOKEN" \
      -d "$event"
  done
  sleep "$INTERVAL"
done
