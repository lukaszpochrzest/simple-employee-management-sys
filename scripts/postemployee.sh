#!/bin/sh

if [ $# -lt 4 ]
  then
    echo "Error - forename, surname, email and position need to be provided"
    exit 1
fi

curl --data "{ \"forename\": \"$1\", \"surname\": \"$2\", \"email\": \"$3\", \"position\":\"$4\" }" -H "Content-Type: application/json" http://127.0.0.1:8080/employee -vvv