#!/bin/bash

echo "Shutdown Trading Engine"
ssh herron@trading-engine-1.int.herron.se "cd /home/herron/deploy/ && bash shutdown-server.sh"
