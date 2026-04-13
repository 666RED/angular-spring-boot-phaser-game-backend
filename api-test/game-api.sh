#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source ~/scripts/api_requests.sh

BASE_URL="http://localhost:8080/api/v1"
GAME_URL="$BASE_URL/games"

list_games() {
	local url=$GAME_URL

	get_request_cookie "$url"
}

create_game() {
	local method=POST
	local url=$GAME_URL

	request_cookie "$method" "$url"
}

get_game() {
	local id=$1
	local url="$GAME_URL/$id"

	get_request_cookie "$url"
}

join_game() {
	local id=$1
	local method=POST
	local url="$GAME_URL/$id"

	request_cookie "$method" "$url" "" "cookies2.txt"
}
