#!/bin/bash

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

source ~/scripts/api_requests.sh
source $SCRIPT_DIR/get-jwt-token.sh

BASE_URL="http://localhost:8080/api/v1"
GAME_URL="$BASE_URL/games"

list_games() {
	local url=$GAME_URL

	get_request "$url" "$TOKEN"
}

create_game() {
	local method=POST
	local url=$GAME_URL

	request "$method" "$url" "" "$TOKEN"
}

get_game() {
	local id=$1
	local url="$GAME_URL/$id"

	get_request "$url" "$TOKEN"
}

join_game() {
	local id=$1
	local method=POST
	local url="$GAME_URL/$id"

	request "$method" "$url" "" "$TOKEN2"
}
