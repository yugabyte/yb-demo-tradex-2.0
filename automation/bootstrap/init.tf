# Create an ec2 machine on the VPC to run terraform on
# VM Config:VPC peered with or same as target VPC, private network, SSM Enabled
# Software: docker, terraform, git, jdk, yugabyte-client, yb_stats
# Action: Checkout the repo on the machine

terraform {

  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
  backend "s3" {
    bucket = "tradex-terraform-state"
    key    = "yr-bootstrap"
    region = "us-east-2"
  }
}


provider "aws" {
  # region = "us-east-2"
  default_tags {
    tags = var.tags
  }
}


locals {
  vpc_id                       = var.vpc-id
  subnet_id                    = var.subnet-id
  prefix                       = var.prefix
  aws-profile                  = var.aws-profile
  ssh-host-alias-file-location = pathexpand(var.ssh-host-alias-file-location)
}


data "aws_region" "region" {
}

data "aws_subnet" "subnet" {
  id = local.subnet_id
}

data "aws_availability_zone" "az" {
  name = data.aws_subnet.subnet.availability_zone
}


