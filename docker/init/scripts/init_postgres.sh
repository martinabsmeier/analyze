#!/bin/zsh

function delete_file() {
  echo "Delete file: $1"
  docker exec postgres rm -rf "$1"
}

function copy_file() {
  echo "Copy file: $1"
  docker cp ../postgres/"$1" postgres:/.
}

function execute_file() {
  echo "Execute script: $1"
  docker exec -it postgres psql -U postgres -a analyze -f dropAndCreateDb.sql
}

echo "Execute 'init_postgres.sh' script"
echo "================================="

echo ""
echo "Delete SQL files:"
delete_file 'dropAndCreateDb.sql'
delete_file 'dropAndCreateTables.sql'
delete_file 'dropAndCreateIndexes.sql'

echo ""
echo "Copy SQL files:"
copy_file 'dropAndCreateDb.sql'
copy_file 'dropAndCreateTables.sql'
copy_file 'dropAndCreateIndexes.sql'

echo ""
echo "Execute scripts:"
execute_file 'dropAndCreateDb.sql'
