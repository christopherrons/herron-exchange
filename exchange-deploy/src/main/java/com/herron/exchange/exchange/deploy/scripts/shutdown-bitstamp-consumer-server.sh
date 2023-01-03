#!/bin/bash

echo "Shutdown Bitstamp Consumer"
ssh herron@bitstamp-consumer-1.int.herron.se "cd /home/herron/deploy/ && bash shutdown-server.sh"

