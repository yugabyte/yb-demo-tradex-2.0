
terraform {
  required_providers {
    aws = {
      source = "hashicorp/aws"
    }
  }
  backend "s3" {
    bucket = "tradex-terraform-state"
    key    = "yr-db"
    region = "us-east-2"
  }
}
