#!/bin/bash

echo "Starting Trading Engine"
ssh herron@trading-engine-1.int.herron.se "cd /home/herron/deploy/ && bash startup-server.sh"
