#!/bin/bash

echo "Starting all systems"
./startup-kafka-server.sh
slepp 5

./startup-server.sh
slepp 5

./startup-trading-engine-server.sh
slepp 5

./startup-bitstamp-consumer-server.sh
slepp 5

