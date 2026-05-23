# =============================================================
# main.tf — Orchestration de tous les modules
# Ordre : vpc → security → ecr → rds → ecs → alb → monitoring
# =============================================================

# ─── Module VPC ──────────────────────────────────────────────
module "vpc" {
  source = "./modules/vpc"

  project_name        = var.project_name
  environment         = var.environment
  vpc_cidr            = var.vpc_cidr
  availability_zone_1 = var.availability_zone_1
  availability_zone_2 = var.availability_zone_2
  public_subnet_cidr  = var.public_subnet_cidr
  private_subnet_cidr = var.private_subnet_cidr
  db_subnet_1_cidr    = var.db_subnet_1_cidr
  db_subnet_2_cidr    = var.db_subnet_2_cidr
}

# ─── Module Security ─────────────────────────────────────────
module "security" {
  source = "./modules/security"

  project_name = var.project_name
  environment  = var.environment
  vpc_id       = module.vpc.vpc_id
}

# ─── Module ECR ──────────────────────────────────────────────
module "ecr" {
  source = "./modules/ecr"

  project_name = var.project_name
  environment  = var.environment
}

# ─── Module RDS ──────────────────────────────────────────────
module "rds" {
  source = "./modules/rds"

  project_name      = var.project_name
  environment       = var.environment
  db_name           = var.db_name
  db_username       = var.db_username
  db_password       = var.db_password
  db_instance_class = var.db_instance_class
  db_subnet_ids     = [module.vpc.db_subnet_1_id, module.vpc.db_subnet_2_id]
  db_sg_id          = module.security.rds_sg_id
}

# ─── Module ALB ──────────────────────────────────────────────
module "alb" {
  source = "./modules/alb"

  project_name      = var.project_name
  environment       = var.environment
  vpc_id            = module.vpc.vpc_id
  public_subnet_ids = [module.vpc.public_subnet_id, module.vpc.public_subnet_2_id]
  alb_sg_id         = module.security.alb_sg_id
}
# ─── Module ECS ──────────────────────────────────────────────
module "ecs" {
  source = "./modules/ecs"

  project_name        = var.project_name
  environment         = var.environment
  aws_region          = var.aws_region
  app_image           = module.ecr.repository_url
  app_cpu             = var.app_cpu
  app_memory          = var.app_memory
  app_count           = var.app_count
  private_subnet_id   = module.vpc.public_subnet_id # ← subnet public
  ecs_sg_id           = module.security.ecs_sg_id
  ecs_task_role_arn   = module.security.ecs_task_role_arn
  ecs_exec_role_arn   = module.security.ecs_exec_role_arn
  target_group_arn    = module.alb.target_group_arn
  db_host             = module.rds.db_endpoint
  db_name             = var.db_name
  db_username         = var.db_username
  db_password         = var.db_password
}

# ─── Module Monitoring ───────────────────────────────────────
module "monitoring" {
  source = "./modules/monitoring"

  project_name             = var.project_name
  environment              = var.environment
  vpc_id                   = module.vpc.vpc_id
  db_subnet_1_id           = module.vpc.public_subnet_id   # ← subnet public
  monitoring_sg_id         = module.security.monitoring_sg_id
  monitoring_instance_type = var.monitoring_instance_type
  monitoring_key_pair      = var.monitoring_key_pair
  alert_email              = var.alert_email
  ecs_cluster_name         = module.ecs.cluster_name
  alb_arn_suffix           = module.alb.alb_arn_suffix
  db_identifier            = module.rds.db_identifier
}
