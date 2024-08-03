#!/bin/bash

echo "Starting Bitstamp Consumer"
ssh herron@bitstamp-consumer-1.int.herron.se "cd /home/herron/deploy/ && bash startup-server.sh"
