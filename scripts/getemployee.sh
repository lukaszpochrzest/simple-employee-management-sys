#!/bin/sh

if [ $# -eq 0 ]
  then
    echo "Error - employee id needs to be provided as an argument"
    exit 1
fi

curl -H "Accept: application/json" -H "Content-Type: application/json" http://127.0.0.1:8080/employee/$1 -v
echo
