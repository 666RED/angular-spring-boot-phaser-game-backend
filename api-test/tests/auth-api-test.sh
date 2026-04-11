#!/bin/bash

source ../auth-api.sh

### Login ###
login '
{
	"email": "test2@example.com",
	"password": "12341234"
}
'

### Register ###
# register '
# {
# 	"name": "Test user 1",
# 	"email": "test1@example.com",
# 	"password": "12341234"
# }
# '
