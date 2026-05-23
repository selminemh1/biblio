# =============================================================
# modules/security/main.tf
# Security Groups + LabRole existant (AWS Academy)
# =============================================================

data "aws_iam_role" "lab_role" {
  name = "LabRole"
}

# ─── Security Group — ALB ────────────────────────────────────
resource "aws_security_group" "alb" {
  name        = "${var.project_name}-sg-alb"
  description = "Security Group ALB - HTTP depuis internet"
  vpc_id      = var.vpc_id

  ingress {
    description = "HTTP depuis internet"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    description = "Tout le trafic sortant"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-sg-alb" }
}

# ─── Security Group — ECS Fargate ────────────────────────────
resource "aws_security_group" "ecs" {
  name        = "${var.project_name}-sg-ecs"
  description = "Security Group ECS - depuis ALB et monitoring"
  vpc_id      = var.vpc_id

  ingress {
    description     = "Depuis ALB uniquement"
    from_port       = 8081
    to_port         = 8081
    protocol        = "tcp"
    security_groups = [aws_security_group.alb.id]
  }

  ingress {
    description     = "Prometheus scraping depuis EC2 monitoring"
    from_port       = 8081
    to_port         = 8081
    protocol        = "tcp"
    security_groups = [aws_security_group.monitoring.id]
  }

  egress {
    description = "Tout le trafic sortant"
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-sg-ecs" }
}

# ─── Security Group — RDS MySQL ──────────────────────────────
resource "aws_security_group" "rds" {
  name        = "${var.project_name}-sg-rds"
  description = "Security Group RDS - depuis ECS et monitoring"
  vpc_id      = var.vpc_id

  ingress {
    description     = "MySQL depuis ECS"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.ecs.id]
  }

  ingress {
    description     = "MySQL depuis EC2 monitoring"
    from_port       = 3306
    to_port         = 3306
    protocol        = "tcp"
    security_groups = [aws_security_group.monitoring.id]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-sg-rds" }
}

# ─── Security Group — EC2 Monitoring ─────────────────────────
resource "aws_security_group" "monitoring" {
  name        = "${var.project_name}-sg-monitoring"
  description = "Security Group EC2 Monitoring"
  vpc_id      = var.vpc_id

  ingress {
    description = "Grafana"
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Prometheus"
    from_port   = 9090
    to_port     = 9090
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "SSH admin"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = { Name = "${var.project_name}-sg-monitoring" }
}
