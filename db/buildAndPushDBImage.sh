#!/usr/bin/env bash

set -Eeuo pipefail

SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )

IMG_TAG=${IMG_TAG:-tradexdb:local}

docker build $SCRIPT_DIR -t $IMG_TAG

docker push $IMG_TAG





