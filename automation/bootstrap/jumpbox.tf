
resource "tls_private_key" "ssh-key" {
  algorithm = "RSA"
  rsa_bits  = "4096"
}
resource "local_file" "ssh-key-private" {
  content         = tls_private_key.ssh-key.private_key_pem
  filename        = "${path.root}/private/${local.prefix}-ssh-key.pem"
  file_permission = "0600"
}
resource "local_file" "ssh-key-public" {
  content         = tls_private_key.ssh-key.public_key_openssh
  filename        = "${path.root}/private/${local.prefix}-ssh-key.pub"
  file_permission = "0600"
}
resource "aws_key_pair" "ssh-key" {
  key_name   = "${local.prefix}-jumpbox"
  public_key = tls_private_key.ssh-key.public_key_openssh
}




data "aws_ami" "ubuntu" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-focal-20.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}

resource "aws_security_group" "sg" {
  name        = "tradex-jumpbox"
  vpc_id      = local.vpc_id
  description = "tradex-jumpbox SG"
  #allow http
  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  # allow https
  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
  #all outbound
  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
  lifecycle {
    create_before_destroy = true
  }
}
resource "aws_ebs_volume" "jumpbox-data" {
  availability_zone = data.aws_availability_zone.az.name
  size              = 40
  type              = "gp3"

  tags = {
    Name = "${local.prefix}-jumbox-data"
  }
}
data "cloudinit_config" "config" {
  gzip          = true
  base64_encode = true

  part {
    content_type = "text/cloud-config"
    content = templatefile("${path.module}/templates/cloud-init.yaml", {
    })
  }
  part {
    content_type = "text/x-shellscript"
    filename     = "init.sh"
    content      = file("${path.module}/templates/init.sh")
  }
  part {
    content_type = "text/x-shellscript"
    filename     = "setup-code.sh"
    content = templatefile("${path.module}/templates/setup-code.sh", {
      git_repo = var.git-repo
    })
  }
}

resource "aws_instance" "jumpbox" {
  ami                    = data.aws_ami.ubuntu.id
  instance_type          = "t3.xlarge"
  subnet_id              = local.subnet_id
  vpc_security_group_ids = [aws_security_group.sg.id]
  iam_instance_profile   = aws_iam_instance_profile.instance-profile.name
  key_name               = aws_key_pair.ssh-key.key_name
  user_data_base64       = data.cloudinit_config.config.rendered
  root_block_device {
    delete_on_termination = true
    volume_type           = "gp3"
    volume_size           = 100
  }

  tags = {
    Name = "${local.prefix}-jumpbox"
  }
}

locals {
  ssh-host-alias-config = <<-SSHCONFIG
Host ${local.prefix}-jumpbox
  IdentityFile ${abspath(local_file.ssh-key-private.filename)}
  User ubuntu
  UserKnownHostsFile /dev/null
  StrictHostKeyChecking no
  ProxyCommand bash -c "aws ssm start-session --target ${aws_instance.jumpbox.id}  --profile ${local.aws-profile} --region ${data.aws_region.region.name} --document-name AWS-StartSSHSession --parameters 'portNumber=%p'"
SSHCONFIG
}

resource "local_file" "ssh-host-alias-config" {
  count           = var.create-ssh-host-alias-file ? 1 : 0
  content         = local.ssh-host-alias-config
  filename        = "${local.ssh-host-alias-file-location}/${local.prefix}-jumpbox"
  file_permission = 0600
}


# resource "aws_volume_attachment" "jumpbox-data" {
#   device_name = "/dev/sdf"
#   volume_id   = aws_ebs_volume.jumpbox-data.id
#   instance_id = aws_instance.jumpbox.id
#   skip_destroy = true
# }
