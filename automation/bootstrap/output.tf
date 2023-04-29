output "jumpbox" {
  value = aws_instance.jumpbox.id
}

output "private-key-file" {
  value = local_file.ssh-key-private.filename
}
output "access-info" {
  value = <<INFO

Infrastructure
--------------
Prefix                : ${local.prefix}
Instance ID           : ${aws_instance.jumpbox.id}
VPC ID                : ${local.vpc_id}
Subnet ID             : ${local.subnet_id}
Region                : ${data.aws_region.region.name}

SSM
---
Connect               : aws ssm start-session --target ${aws_instance.jumpbox.id} --region ${data.aws_region.region.name}
Port Forward / Tunnel : aws ssm start-session --target ${aws_instance.jumpbox.id} --region ${data.aws_region.region.name} --document-name AWS-StartPortForwardingSession --parameters '{"portNumber":["8080"],"localPortNumber":["8080"]}'
SSH over SSM          : ssh -o "UserKnownHostsFile=/dev/null" -o "StrictHostKeyChecking=no" -i ${abspath(local_file.ssh-key-private.filename)} ubuntu@ssm.${aws_instance.jumpbox.id}.${local.aws-profile}.${data.aws_region.region.name}

SSH
----
Host Alias            : ${var.create-ssh-host-alias-file ? "${local.prefix}-jumpbox" : "<Undefined>"}
Host Alias Config     : (for manual alias creation. Add to ~/.ssh/config)
${local.ssh-host-alias-config}
Connect               : ssh ${local.prefix}-jumpbox

GUI
---
VS Code               : code --remote ssh-remote+${local.prefix}-jumpbox /home/ubuntu
CloudWeaver           : <TBA>


INFO
}



