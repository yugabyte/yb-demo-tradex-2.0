#!/usr/bin/env bash

echo "About build stock loader image at ${PWD}"
docker build . -t tradex/stock-dataloader:latest; sleep 3: docker push tradex/stock-dataloader:latest:latest;
echo "Image built and data uploaded"


