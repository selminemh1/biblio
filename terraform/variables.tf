# =============================================================
# variables.tf — Variables globales du projet
# =============================================================

# ─── Général ─────────────────────────────────────────────────

variable "aws_region" {
  description = "Région AWS"
  type        = string
  default     = "us-east-1"
}

variable "environment" {
  description = "Environnement (dev, staging, production)"
  type        = string
  default     = "production"
}

variable "project_name" {
  description = "Nom du projet"
  type        = string
  default     = "library-management"
}

# ─── Réseau VPC ──────────────────────────────────────────────

variable "vpc_cidr" {
  description = "CIDR du VPC"
  type        = string
  default     = "10.0.0.0/16"
}

variable "availability_zone_1" {
  description = "Zone de disponibilité principale (active)"
  type        = string
  default     = "us-east-1a"
}

variable "availability_zone_2" {
  description = "Zone de disponibilité secondaire (déclarée pour RDS subnet group)"
  type        = string
  default     = "us-east-1b"
}

# Subnets AZ-1 (actifs)
variable "public_subnet_cidr" {
  description = "CIDR subnet public AZ-1 — ALB + NAT Gateway"
  type        = string
  default     = "10.0.1.0/24"
}

variable "private_subnet_cidr" {
  description = "CIDR subnet privé AZ-1 — ECS Fargate"
  type        = string
  default     = "10.0.2.0/24"
}

variable "db_subnet_1_cidr" {
  description = "CIDR subnet DB AZ-1 — RDS + Monitoring"
  type        = string
  default     = "10.0.3.0/24"
}

# Subnet AZ-2 (requis par RDS subnet group, non déployé)
variable "db_subnet_2_cidr" {
  description = "CIDR subnet DB AZ-2 — requis par RDS, vide"
  type        = string
  default     = "10.0.4.0/24"
}

# ─── Base de données RDS ──────────────────────────────────────

variable "db_name" {
  description = "Nom de la base de données"
  type        = string
  default     = "librarydb"
}

variable "db_username" {
  description = "Utilisateur MySQL"
  type        = string
  default     = "libraryuser"
}

variable "db_password" {
  description = "Mot de passe MySQL — ne jamais committer"
  type        = string
  sensitive   = true
}

variable "db_instance_class" {
  description = "Taille instance RDS"
  type        = string
  default     = "db.t3.micro"
}

# ─── ECS Fargate ─────────────────────────────────────────────

variable "app_image" {
  description = "Image Docker de l'app — sera remplacé par ECR URI"
  type        = string
  default     = "library-management:latest"
}

variable "app_cpu" {
  description = "CPU alloué à la tâche ECS (unités)"
  type        = number
  default     = 256
}

variable "app_memory" {
  description = "RAM allouée à la tâche ECS (MB)"
  type        = number
  default     = 512
}

variable "app_count" {
  description = "Nombre de tâches ECS en cours"
  type        = number
  default     = 1
}

# ─── Monitoring EC2 ──────────────────────────────────────────

variable "monitoring_instance_type" {
  description = "Type instance EC2 monitoring"
  type        = string
  default     = "t3.micro"
}

variable "monitoring_key_pair" {
  description = "Nom de la clé SSH pour EC2 monitoring (créée dans AWS Console)"
  type        = string
  default     = "library-monitoring-key"
}

# ─── Alertes ─────────────────────────────────────────────────

variable "alert_email" {
  description = "Email pour les alertes CloudWatch SNS"
  type        = string
  default     = "mahmoudiselmine@gmail.com"
}
