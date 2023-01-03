#!/bin/bash

echo "Shutdown all systems"
./shutdown-kafka-server.sh
slepp 5

./shutdown-server.sh
slepp 5

./shutdown-trading-engine-server.sh
slepp 5

./shutdown-bitstamp-consumer-server.sh
slepp 5

