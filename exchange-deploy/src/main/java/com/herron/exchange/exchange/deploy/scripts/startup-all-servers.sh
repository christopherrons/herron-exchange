#!/bin/bash

echo "Starting all systems"
./startup-kafka.sh
sleep 5

./startup-server.sh
sleep 5

./startup-trading-engine-server.sh
sleep 5

./startup-bitstamp-consumer-server.sh
sleep 5

