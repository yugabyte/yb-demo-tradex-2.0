data "http" "yba-customer" {
  url = "${var.yba.api-endpoint}/session_info"
  request_headers = {
    Accept              = "application/json"
    X-AUTH-YW-API-TOKEN = var.yba.api-token
  }
  lifecycle {
    postcondition {
      condition     = contains([200], self.status_code)
      error_message = "Status code invalid"
    }
  }
  insecure = var.yba.insecure
}
locals {
  yba-customer-uuid = jsondecode(chomp(data.http.yba-customer.response_body)).customerUUID
  yba-client = merge({
    customer-uuid = local.yba-customer-uuid
  }, var.yba)
}
module "single-region-universe" {
  source        = "./yba-universe-info"
  yba           = local.yba-client
  universe-name = var.yba.single-region-universe-name
}

module "multi-region-universe" {
  source        = "./yba-universe-info"
  yba           = local.yba-client
  universe-name = var.yba.multi-region-universe-name
}


module "multi-region-read-replica-universe" {
  source        = "./yba-universe-info"
  yba           = local.yba-client
  universe-name = var.yba.multi-region-read-replica-universe-name
}

module "geo-partition-universe" {
  source        = "./yba-universe-info"
  yba           = local.yba-client
  universe-name = var.yba.geo-partition-universe-name
}

locals {
  db-map = {
    sr = {
      title = "Single Region"
      module = module.single-region-universe
      dns = {
        all = one(aws_route53_record.single-region[*])
        nodes = aws_route53_record.single-region-nodes[*]
      }
    }
    mr = {
      title = "Multi Region"
      module = module.multi-region-universe
      dns = {
        all = one(aws_route53_record.multi-region[*])
        nodes = aws_route53_record.multi-region-nodes[*]
      }
    }
    mrrr = {
      title = "Multi Region with Read Replica"
      module = module.multi-region-read-replica-universe
      dns = {
        all = one(aws_route53_record.multi-region-read-replica[*])
        nodes = aws_route53_record.multi-region-read-replica-nodes[*]
        rr = one(aws_route53_record.multi-region-read-replica-rr[*])
        rr-nodes = aws_route53_record.multi-region-read-replica-rr-nodes[*]
      }
    }
    gp = {
      title = "Geo Partitioned"
      module = module.geo-partition-universe
      dns = {
        all = one(aws_route53_record.geopart[*])
        nodes = aws_route53_record.geopart-nodes[*]
      }
    }
  }
}
