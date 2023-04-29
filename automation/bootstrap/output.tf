output "jumpbox" {
  value = aws_instance.jumpbox.id
}

output "private-key-file" {
  value = local_file.ssh-key-private.filename
}
output "access-info" {
  value = <<INFO
Prefix                : ${local.prefix}
Instance ID           : ${aws_instance.jumpbox.id}
VPC ID                : ${local.vpc_id}
Subnet ID             : ${local.subnet_id}
Region                : ${data.aws_region.region.name}
SSM Connect           : aws ssm start-session --target ${aws_instance.jumpbox.id} --region ${data.aws_region.region.name}
SSH Conenct (via SSM) : ssh -o "UserKnownHostsFile=/dev/null" -o "StrictHostKeyChecking=no" -i ${abspath(local_file.ssh-key-private.filename)} ubuntu@ssm.${aws_instance.jumpbox.id}.${local.aws-profile}.${data.aws_region.region.name}
SSH Connect (direct)  : ssh ${local.prefix}-jumpbox
SSH Host Alias        : ${ var.create-ssh-host-alias-file ?  "${local.prefix}-jumpbox" : "<Undefined>" }
SSH Host Alias Config : (for manual alias creation. Add to ~/.ssh/config)
${local.ssh-host-alias-config}
VS Code Remote Access : code --remote ssh-remote+${local.prefix}-jumpbox /home/ubuntu

INFO
}



