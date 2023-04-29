#!/usr/bin/env bash

set -x
SCRIPT_DIR=$( cd -- "$( dirname -- "${BASH_SOURCE[0]}" )" &> /dev/null && pwd )
PROJECT_DIR=$( cd ${SCRIPT_DIR}/.. ; pwd)

install -m 0755 -d /etc/apt/keyrings

export DEBIAN_FRONTEND=noninteractive

# Base tools
apt-get update
apt-get install -qqy apt-transport-https ca-certificates curl gnupg-agent software-properties-common lsb-release unattended-upgrades git wget openjdk-17-jdk-headless maven python-is-python3 unzip net-tools jq  python3-venv python3-pip

# Add docker repo
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo "deb [arch="$(dpkg --print-architecture)" signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu "$(. /etc/os-release && echo "$VERSION_CODENAME")" stable" > /etc/apt/sources.list.d/docker.list

# Add kubernetes repo
curl -fsSLo /etc/apt/keyrings/kubernetes-archive-keyring.gpg https://packages.cloud.google.com/apt/doc/apt-key.gpg
echo "deb [signed-by=/etc/apt/keyrings/kubernetes-archive-keyring.gpg] https://apt.kubernetes.io/ kubernetes-xenial main" | sudo tee /etc/apt/sources.list.d/kubernetes.list

# Add helm repo
curl -fsSL https://baltocdn.com/helm/signing.asc | gpg --dearmor -o /usr/share/keyrings/helm.gpg
echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/helm.gpg] https://baltocdn.com/helm/stable/debian/ all main" | sudo tee /etc/apt/sources.list.d/helm-stable-debian.list

# Add terraform repo
wget -O- https://apt.releases.hashicorp.com/gpg | sudo gpg --dearmor -o /usr/share/keyrings/hashicorp-archive-keyring.gpg
echo "deb [signed-by=/usr/share/keyrings/hashicorp-archive-keyring.gpg] https://apt.releases.hashicorp.com $(lsb_release -cs) main" | sudo tee /etc/apt/sources.list.d/hashicorp.list

# Install tools
apt-get update
apt-get install -qqy kubectl helm docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin terraform

usermod -aG docker ubuntu
sudo curl -L "https://github.com/docker/compose/releases/download/1.29.2/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose


# Isntall k9s
curl -fsSL https://github.com/derailed/k9s/releases/download/v0.27.3/k9s_Linux_amd64.tar.gz | tar -C /usr/local/bin -xz k9s

# Install awscli
curl -fsSL "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip -qq awscliv2.zip
rm awscliv2.zip
./aws/install
rm -rf aws

curl "https://s3.amazonaws.com/session-manager-downloads/plugin/latest/ubuntu_64bit/session-manager-plugin.deb" -o "session-manager-plugin.deb"
sudo dpkg -i session-manager-plugin.deb
rm session-manager-plugin.deb

# Install yb_stats
wget -q 'https://github.com/fritshoogland-yugabyte/yb_stats/releases/download/v0.9.8/yb-stats_0.9.8_amd64.deb'
apt-get install -qqy ./yb-stats_0.9.8_amd64.deb
rm -f yb-stats_0.9.8_amd64.deb

# Add yb repo
wget -q https://s3.us-west-2.amazonaws.com/downloads.yugabyte.com/repos/reporpms/yb-apt-repo_1.0.0_all.deb
apt-get install -qqy ./yb-apt-repo_1.0.0_all.deb
rm yb-apt-repo_1.0.0_all.deb

# Install yb-voyager
apt-get update
apt-get install -qqy yb-voyager

# Install ybm-cli
wget -q https://github.com/yugabyte/ybm-cli/releases/download/v0.1.6/ybm_0.1.6_linux_amd64.zip
unzip -qq ybm_0.1.6_linux_amd64.zip  ybm  -d /usr/local/bin
rm ybm_0.1.6_linux_amd64.zip

# Install yugabyte
curl -fsSL https://downloads.yugabyte.com/releases/2.17.2.0/yugabyte-2.17.2.0-b216-linux-x86_64.tar.gz | tar -C /opt -xz
/opt/yugabyte-2.17.2.0/bin/post_install.sh
echo "export PATH=\$PATH:/opt/yugabyte-2.17.2.0/bin:/opt/yugabyte-2.17.2.0/tools:/opt/yugabyte-2.17.2.0/postgres/bin" > /etc/profile.d/yugabyte.sh

# Add support for individual config files
mkdir -p /home/ubuntu/.ssh/configs
touch /home/ubuntu/.ssh/config
echo "Include configs/*" >> /home/ubuntu/.ssh/config
chown  -R ubuntu:ubuntu /home/ubuntu/.ssh

# Setup Direnv
curl -sfL https://direnv.net/install.sh | bash
echo 'eval "$(direnv hook bash)"' >> /home/ubuntu/.bashrc
