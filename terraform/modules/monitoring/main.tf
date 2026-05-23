# =============================================================
# modules/monitoring/main.tf
# EC2 t3.micro : Prometheus + Grafana via Docker
# CloudWatch Alarms + SNS email alerting
# =============================================================

# AMI Amazon Linux 2023 (gratuite, légère)
data "aws_ami" "amazon_linux" {
  most_recent = true
  owners      = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-*-x86_64"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }
}

# EC2 t3.micro — Prometheus + Grafana via Docker
resource "aws_instance" "monitoring" {
  ami                    = data.aws_ami.amazon_linux.id
  instance_type          = var.monitoring_instance_type
  subnet_id              = var.db_subnet_1_id
  vpc_security_group_ids = [var.monitoring_sg_id]
  key_name               = var.monitoring_key_pair

  # Script de démarrage — installe Docker + lance Prometheus + Grafana
  user_data = base64encode(<<-EOF
    #!/bin/bash
    yum update -y
    yum install -y docker
    systemctl start docker
    systemctl enable docker

    # Créer réseau Docker
    docker network create monitoring

    # Lancer Prometheus
    docker run -d \
      --name prometheus \
      --network monitoring \
      --restart unless-stopped \
      -p 9090:9090 \
      prom/prometheus:v2.51.0

    # Lancer Grafana
    docker run -d \
      --name grafana \
      --network monitoring \
      --restart unless-stopped \
      -p 3000:3000 \
      -e GF_SECURITY_ADMIN_PASSWORD=Admin1234! \
      grafana/grafana:10.3.0
  EOF
  )

  tags = {
    Name = "${var.project_name}-monitoring-ec2"
  }
}

# ─── SNS Topic — alertes email ───────────────────────────────
resource "aws_sns_topic" "alerts" {
  name = "${var.project_name}-alerts"
}

resource "aws_sns_topic_subscription" "email" {
  topic_arn = aws_sns_topic.alerts.arn
  protocol  = "email"
  endpoint  = var.alert_email
}

# ─── CloudWatch Alarms ───────────────────────────────────────

# Alarme : CPU ECS > 80%
resource "aws_cloudwatch_metric_alarm" "ecs_cpu_high" {
  alarm_name          = "${var.project_name}-ecs-cpu-high"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "CPUUtilization"
  namespace           = "AWS/ECS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "ECS CPU > 80%"
  alarm_actions       = [aws_sns_topic.alerts.arn]

  dimensions = {
    ClusterName = var.ecs_cluster_name
  }
}

# Alarme : temps de réponse ALB > 2 secondes
resource "aws_cloudwatch_metric_alarm" "alb_response_time" {
  alarm_name          = "${var.project_name}-alb-latency"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "TargetResponseTime"
  namespace           = "AWS/ApplicationELB"
  period              = 300
  statistic           = "Average"
  threshold           = 2
  alarm_description   = "Temps de réponse ALB > 2s (BNF-02)"
  alarm_actions       = [aws_sns_topic.alerts.arn]

  dimensions = {
    LoadBalancer = var.alb_arn_suffix
  }
}

# Alarme : connexions RDS > 80%
resource "aws_cloudwatch_metric_alarm" "rds_connections" {
  alarm_name          = "${var.project_name}-rds-connections"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 2
  metric_name         = "DatabaseConnections"
  namespace           = "AWS/RDS"
  period              = 300
  statistic           = "Average"
  threshold           = 80
  alarm_description   = "Connexions RDS élevées"
  alarm_actions       = [aws_sns_topic.alerts.arn]

  dimensions = {
    DBInstanceIdentifier = var.db_identifier
  }
}
