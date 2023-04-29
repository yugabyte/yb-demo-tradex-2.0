#! /bin/bash
set -Eeuo pipefail
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

if [ $# -lt 2 ]
  then
    echo "No arguments supplied."
    echo "Usage: ./buildDBSchema.sh <envfile> <action>"
    exit 1
fi

IMG_TAG=${IMG_TAG:-tradexdb:local}
source .env

docker run --rm --env-file $1 $IMG_TAG $2



