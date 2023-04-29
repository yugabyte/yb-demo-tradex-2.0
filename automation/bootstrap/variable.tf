variable "vpc-id"{
  type = string
  description = "VPC ID"
  default = "vpc-0aa158023c29d81b8"
}

variable "subnet-id"{
  type = string
  description = "Subnet ID"
  default = "subnet-0b7bae8a91c5d0ca2"
}
variable "prefix"{
  type = string
  description = "Prefix for resource names"
  default = "tradex-bootstrap"
}

variable "aws-profile" {
  type = string
  description = "AWS Profile. Used for generating SSH host entry."
  default = "yb-americas-presales"
}
variable "ssh-host-alias-file-location" {
  type = string
  description = "SSH Host alias file"
  default = "~/.ssh/configs"
}

variable "create-ssh-host-alias-file" {
  type = bool
  description = "Create host alias file in the 'ssh-host-alias-file-location'"
  default = true
}

variable "git-repo"{
  type = string
  description = "Git repository address. https format"
  default = "git://github.com/yugabyte/yb-demo-tradex-v2.0.git"
}
