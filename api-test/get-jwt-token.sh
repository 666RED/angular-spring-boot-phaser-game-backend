#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

export TOKEN=$(
	curl -s -X POST http://localhost:8080/api/v1/auth/login \
		-H "Content-Type: application/json" \
		-d '{
			"email": "test1@example.com",
			"password": "12341234"
		}' |
		jq -r '.token'
)

export TOKEN2=$(
	curl -s -X POST http://localhost:8080/api/v1/auth/login \
		-H "Content-Type: application/json" \
		-d '{
			"email": "test2@example.com",
			"password": "12341234"
		}' |
		jq -r '.token'
)
