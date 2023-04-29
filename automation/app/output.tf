

output "access-info" {
  value = <<EOF
ACCESS INFORMATION
==================

%{for location, res in local.regional-resources~}
Location: ${location}
---------------------
  SSH Access:
    ssh -o "UserKnownHostsFile=/dev/null" -o "StrictHostKeyChecking=no" -i ${local_file.ssh-key-private.filename} ubuntu@${res.ip}
    ssh -o "UserKnownHostsFile=/dev/null" -o "StrictHostKeyChecking=no" -i ${local_file.ssh-key-private.filename} ubuntu@${aws_route53_record.regional-app[location].name}

  SSM Access:
    ssh ${res.vm-name}
    aws ssm start-session --target ${res.instance-id} --region ${res.region}
    ssh ssm.${res.instance-id}.${res.region}.$AWS_PROFILE

  Web Access:
    http://${aws_route53_record.regional-app[location].name}
    https://${aws_route53_record.regional-app[location].name}
    http://${aws_route53_record.regional-app[location].name}:8080
    https://${aws_route53_record.regional-app[location].name}:8443

%{endfor~}

Start App VMs
-------------
%{for location, res in local.regional-resources~}
aws ec2 start-instances --instance-ids ${res.instance-id} --region ${res.region}
%{endfor~}

Stop App VMs
-------------
%{for location, res in local.regional-resources~}
aws ec2 stop-instances --instance-ids ${res.instance-id} --region ${res.region}
%{endfor~}

Database DNS:
-------------
%{for k, u in local.db-map~}

${u.title}
  Name: ${u.module.name}
  UUID: ${u.module.universeUUID}

  ${u.dns.all.name} -> ${join(",", u.dns.all.records)}

  %{for dns in u.dns.nodes[*]~}
  ${dns.name} -> ${join(",", dns.records)} (${u.module.nodes-topology-map[join("", dns.records)]})
  %{endfor~}
  %{if contains(keys(u.dns), "rr")~}

  Read Replica

    ${u.dns.rr.name} -> ${join(",", u.dns.rr.records)}

    %{for dns in u.dns.rr-nodes[*]~}
    ${dns.name} -> ${join(",", dns.records)} (${u.module.nodes-topology-map[join("", dns.records)]})
    %{endfor~}
  %{endif~}
%{endfor~}

EOF
}


