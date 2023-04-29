

locals {
  dir = path.module
  admin-cidrs = concat(
    var.additional-admin-workstation-cidrs,
    ["${local.workstation-ip}/32"],
    data.aws_vpc.Boston.cidr_block_associations.*.cidr_block,
    data.aws_vpc.Washington.cidr_block_associations.*.cidr_block,
    data.aws_vpc.London.cidr_block_associations.*.cidr_block,
    data.aws_vpc.Mumbai.cidr_block_associations.*.cidr_block,
    data.aws_vpc.Sydney.cidr_block_associations.*.cidr_block
  )
}


module "Boston" {
  providers = {
    aws = aws.Boston
  }
  name                    = "tradex-${var.env-name}-Boston"
  vpc_id                  = var.vpc-mapping.Boston.vpc
  source                  = "./regional-resources"
  public-key              = tls_private_key.ssh-key.public_key_openssh
  private-key             = tls_private_key.ssh-key.private_key_openssh
  admin-cidrs             = local.admin-cidrs
  tls-key-pem             = local_file.tls-private-key-pem.content
  tls-cert-pem            = local_file.tls-certificate-pem.content
  tls-ca-pem              = local_file.tls-ca-pem.content
  tls-pkcs                = local_file.tls-pkcs12.content_base64
  tradex-env              = local.tradex-env.boston
  instance-profile        = aws_iam_instance_profile.ssm-instance-profile.name
  post-provision-commands = var.post-provision-commands
}

module "Washington" {
  providers = {
    aws = aws.Washington
  }
  name                    = "tradex-${var.env-name}-Washington"
  vpc_id                  = var.vpc-mapping.Washington.vpc
  prefix                  = "tradex-${var.env-name}"
  source                  = "./regional-resources"
  public-key              = tls_private_key.ssh-key.public_key_openssh
  private-key             = tls_private_key.ssh-key.private_key_openssh
  admin-cidrs             = local.admin-cidrs
  tls-key-pem             = local_file.tls-private-key-pem.content
  tls-cert-pem            = local_file.tls-certificate-pem.content
  tls-ca-pem              = local_file.tls-ca-pem.content
  tls-pkcs                = local_file.tls-pkcs12.content_base64
  tradex-env              = local.tradex-env.washington
  instance-profile        = aws_iam_instance_profile.ssm-instance-profile.name
  post-provision-commands = var.post-provision-commands
}


module "Mumbai" {
  providers = {
    aws = aws.Mumbai
  }
  name                    = "tradex-${var.env-name}-Mumbai"
  vpc_id                  = var.vpc-mapping.Mumbai.vpc
  prefix                  = "tradex-${var.env-name}"
  source                  = "./regional-resources"
  public-key              = tls_private_key.ssh-key.public_key_openssh
  private-key             = tls_private_key.ssh-key.private_key_openssh
  admin-cidrs             = local.admin-cidrs
  tls-key-pem             = local_file.tls-private-key-pem.content
  tls-cert-pem            = local_file.tls-certificate-pem.content
  tls-ca-pem              = local_file.tls-ca-pem.content
  tls-pkcs                = local_file.tls-pkcs12.content_base64
  tradex-env              = local.tradex-env.mumbai
  instance-profile        = aws_iam_instance_profile.ssm-instance-profile.name
  post-provision-commands = var.post-provision-commands
}


module "Sydney" {
  providers = {
    aws = aws.Sydney
  }
  name                    = "tradex-${var.env-name}-Sydney"
  vpc_id                  = var.vpc-mapping.Sydney.vpc
  prefix                  = "tradex-${var.env-name}"
  source                  = "./regional-resources"
  public-key              = tls_private_key.ssh-key.public_key_openssh
  private-key             = tls_private_key.ssh-key.private_key_openssh
  admin-cidrs             = local.admin-cidrs
  tls-key-pem             = local_file.tls-private-key-pem.content
  tls-cert-pem            = local_file.tls-certificate-pem.content
  tls-ca-pem              = local_file.tls-ca-pem.content
  tls-pkcs                = local_file.tls-pkcs12.content_base64
  tradex-env              = local.tradex-env.sydney
  instance-profile        = aws_iam_instance_profile.ssm-instance-profile.name
  post-provision-commands = var.post-provision-commands
}


module "London" {
  providers = {
    aws = aws.London
  }
  name                    = "tradex-${var.env-name}-London"
  vpc_id                  = var.vpc-mapping.London.vpc
  prefix                  = "tradex-${var.env-name}"
  source                  = "./regional-resources"
  public-key              = tls_private_key.ssh-key.public_key_openssh
  private-key             = tls_private_key.ssh-key.private_key_openssh
  admin-cidrs             = local.admin-cidrs
  tls-key-pem             = local_file.tls-private-key-pem.content
  tls-cert-pem            = local_file.tls-certificate-pem.content
  tls-ca-pem              = local_file.tls-ca-pem.content
  tls-pkcs                = local_file.tls-pkcs12.content_base64
  tradex-env              = local.tradex-env.london
  instance-profile        = aws_iam_instance_profile.ssm-instance-profile.name
  post-provision-commands = var.post-provision-commands
}

locals {
  regional-resource-module-map = {
    Boston = module.Boston
    Washington = module.Washington
    London = module.London
    Mumbai = module.Mumbai
    Sydney = module.Sydney
  }
  regional-resources = {
    for l, m in local.regional-resource-module-map : l => {
      ip = m.ip
      region = m.region
      instance-id = m.app-vm
      vm-name = "tradex-${var.env-name}-${l}"
      sg = m.sg
      ssh-host-alias-config = <<-SSHCONFIG
Host tradex-${var.env-name}-${l}
  IdentityFile ${abspath(local_file.ssh-key-private.filename)}
  User ubuntu
  UserKnownHostsFile /dev/null
  StrictHostKeyChecking no
  ProxyCommand bash -c "aws ssm start-session --target ${m.app-vm} --region ${m.region} --document-name AWS-StartSSHSession --parameters 'portNumber=%p'"
SSHCONFIG
    }
  }
}

resource "local_file" "ssh-host-alias-file" {
  for_each        = local.regional-resources
  filename        = "${local.ssh-host-alias-file-location}/${each.value.vm-name}"
  content         = each.value.ssh-host-alias-config
  file_permission = 0600
}
