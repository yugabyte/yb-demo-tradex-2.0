terraform {

  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
    acme = {
      source = "vancluever/acme"
    }
    pkcs12 = {
      source = "chilicat/pkcs12"
    }
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0.1"
    }
  }
  backend "s3" {
    bucket = "tradex-terraform-state"
    key    = "yr"
    region = "us-east-2"
  }
}



data "aws_vpc" "Boston" {
  id       = var.vpc-mapping.Boston.vpc
  provider = aws.Boston
}

data "aws_vpc" "Washington" {
  id       = var.vpc-mapping.Washington.vpc
  provider = aws.Washington
}


data "aws_vpc" "London" {
  id       = var.vpc-mapping.London.vpc
  provider = aws.London
}

data "aws_vpc" "Mumbai" {
  id       = var.vpc-mapping.Mumbai.vpc
  provider = aws.Mumbai
}

data "aws_vpc" "Sydney" {
  id       = var.vpc-mapping.Sydney.vpc
  provider = aws.Sydney
}

locals {
  ssh-host-alias-file-location = pathexpand("~/.ssh/configs")
  oracle-db-endpoint = var.oracle-db-endpoint
}
