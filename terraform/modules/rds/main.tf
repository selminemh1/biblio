# =============================================================
# modules/rds/main.tf — RDS MySQL
# Remplace MySQL XAMPP local et MySQL Docker
# db.t3.micro — free tier AWS Academy
# =============================================================

# DB Subnet Group — obligatoire, nécessite 2 AZ minimum
resource "aws_db_subnet_group" "main" {
  name       = "${var.project_name}-db-subnet-group"
  subnet_ids = var.db_subnet_ids

  tags = {
    Name = "${var.project_name}-db-subnet-group"
  }
}

# Instance RDS MySQL
resource "aws_db_instance" "mysql" {
  identifier        = "${var.project_name}-mysql"
  engine            = "mysql"
  engine_version    = "8.0"
  instance_class    = var.db_instance_class

  db_name  = var.db_name
  username = var.db_username
  password = var.db_password

  # Stockage
  allocated_storage     = 20
  max_allocated_storage = 20    # pas d'autoscaling pour économiser crédits
  storage_type          = "gp2"
  storage_encrypted     = true  # DevSecOps : chiffrement au repos

  # Réseau
  db_subnet_group_name   = aws_db_subnet_group.main.name
  vpc_security_group_ids = [var.db_sg_id]
  publicly_accessible    = false    # subnet privé uniquement

  # Sauvegarde
  backup_retention_period = 1
  skip_final_snapshot     = true    # AWS Academy : évite snapshot au destroy

  # Maintenance
  auto_minor_version_upgrade = true
  deletion_protection        = false  # AWS Academy : facilite le destroy

  tags = {
    Name = "${var.project_name}-rds-mysql"
  }
}
