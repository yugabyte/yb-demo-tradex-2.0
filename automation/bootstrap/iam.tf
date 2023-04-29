

resource "aws_iam_policy" "policy" {
  name        = "${local.prefix}-policy"
  description = "${local.prefix} Jumpbox Customer Policy"
  policy      = jsonencode({
    "Version" : "2012-10-17",
    "Statement" : [
      {
        "Sid" : "Admin",
        "Effect" : "Allow",
        "Action" : [
          "route53:*",
          "iam:*",
          "ec2:*",
        ],
        "Resource" : "*"
      }
    ]
  })
}

resource "aws_iam_role" "role" {
  name = "${local.prefix}-role"


  managed_policy_arns = [
    "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore",
    "arn:aws:iam::aws:policy/AmazonSSMPatchAssociation",
    "arn:aws:iam::aws:policy/AmazonS3FullAccess",
    aws_iam_policy.policy.arn
  ]

  assume_role_policy = jsonencode({
    Version   = "2012-10-17"
    Statement = [
      {
        Action    = "sts:AssumeRole"
        Effect    = "Allow"
        Principal = {
          Service = "ec2.amazonaws.com"
        }
      },
    ]
  })
}

resource "aws_iam_instance_profile" "instance-profile" {
  name = "${local.prefix}-instance-profile"
  role = aws_iam_role.role.name
}
