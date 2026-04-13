#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source ~/scripts/api_requests.sh

BASE_URL="http://localhost:8080/api/v1"
AUTH_URL="$BASE_URL/auth"

login() {
	local method=POST
	local url="$AUTH_URL/login"
	data=$1

	login_request_cookie "$method" "$url" "$data"
}

login_as_second_user() {
	local method=POST
	local url="$AUTH_URL/login"
	data=$1

	login_request_cookie "$method" "$url" "$data" "cookies2.txt"
}

register() {
	local method=POST
	local url="$AUTH_URL/register"
	data=$1

	request_cookie "$method" "$url" "$data"
}
