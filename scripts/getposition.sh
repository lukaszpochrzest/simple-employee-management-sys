#!/bin/sh

if [ $# -eq 0 ]
  then
    echo "Error - position id needs to be provided as argument"
    exit 1
fi

curl -H "Accept: application/json" -H "Content-Type: application/json" http://127.0.0.1:8080/position/$1 -v
echo
