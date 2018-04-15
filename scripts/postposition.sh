#!/bin/sh

if [ $# -eq 0 ]
  then
    echo "Error - position name needs to be provided as argument"
    exit 1
fi

curl --data "{ \"name\": \"$1\" }" -H "Content-Type: application/json" http://127.0.0.1:8080/position -vvv
