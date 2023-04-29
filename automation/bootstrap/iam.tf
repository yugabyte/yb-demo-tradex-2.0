resource "aws_iam_role" "role" {
  name = "${local.prefix}-role"


  managed_policy_arns = [
    "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore",
    "arn:aws:iam::aws:policy/AmazonSSMPatchAssociation",
    "arn:aws:iam::aws:policy/AmazonS3FullAccess"
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
