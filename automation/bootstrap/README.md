# Jumpbox for YugabyteDB Demo Apps

This module creates a jumpbox on AWS with common utils.

OS: Ubuntu
Tools:
- aws-cli
- curl
- docker (client, containerd,  docker-compose)
- git
- helm
- kubectl
- maven
- openjdk-17
- python 3
- terraform
- unzip
- wget
- yb_stats
- yb-voyager
- ybm cli
- yugabyte-db 2.17

It also does:
1. Create a new SSH key
2. Add new key pair
3. Create SSM instance profile
4. Create EC2 instance
    1. Install tools
    2. Setup SSM login
5. Setup local ssh host alias for easy connecting
    1. Via ssh command line
    2. Via vscode


## Usage
```bash

```


## Use bootstrap module

1. Create a workspace directory for terraform
2. Create main.tf with:

    ```hcl
    module "tradex-jumpbox" {
      source = "git@github.com:yugabyte/yb-demo-tradex-2.0.git//automation/bootstrap"
      vpc-id ="<VPC-ID>"
      subnet-id="<SUBNET-ID>"
      prefix="<PREFIX>"
      aws-profile="<LOCAL-AWS-PROFILE-NAME>"
      tags = {} // Tags to put on all resources
    }
    ```

