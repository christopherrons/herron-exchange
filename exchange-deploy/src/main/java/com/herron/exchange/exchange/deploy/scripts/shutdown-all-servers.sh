#!/bin/bash

echo "Shutdown all systems"
./shutdown-kafka.sh
sleep 5

./shutdown-server.sh
sleep 5

./shutdown-trading-engine-server.sh
sleep 5

./shutdown-bitstamp-consumer-server.sh
sleep 5

