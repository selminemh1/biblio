# =============================================================
# outputs.tf — Valeurs exportées après terraform apply
# =============================================================

output "alb_url" {
  description = "URL publique de l'application (via ALB)"
  value       = "http://${module.alb.alb_dns_name}"
}

output "ecr_repository_url" {
  description = "URI du registre ECR pour pousser les images Docker"
  value       = module.ecr.repository_url
}

output "rds_endpoint" {
  description = "Endpoint de la base de données RDS MySQL"
  value       = module.rds.db_endpoint
  sensitive   = true
}

output "grafana_url" {
  description = "URL Grafana (monitoring)"
  value       = "http://${module.monitoring.monitoring_public_ip}:3000"
}

output "prometheus_url" {
  description = "URL Prometheus (monitoring)"
  value       = "http://${module.monitoring.monitoring_public_ip}:9090"
}

output "vpc_id" {
  description = "ID du VPC créé"
  value       = module.vpc.vpc_id
}
