#!/bin/sh

if [ $# -gt 0 ]
  then
  QUERY=$1
fi

curl -H "Accept: application/json" -H "Content-Type: application/json" http://127.0.0.1:8080/employee?$QUERY -v
echo