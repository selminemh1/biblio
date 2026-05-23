# =============================================================
# modules/ecs/main.tf — ECS Fargate
# Cluster + Task Definition + Service + CloudWatch Logs
# App Spring Boot monolithe (Thymeleaf + API)
# =============================================================

# CloudWatch Log Group pour les logs de l'app
resource "aws_cloudwatch_log_group" "app" {
  name              = "/ecs/${var.project_name}"
  retention_in_days = 7

  tags = {
    Name = "${var.project_name}-logs"
  }
}

# ECS Cluster
resource "aws_ecs_cluster" "main" {
  name = "${var.project_name}-cluster"

  setting {
    name  = "containerInsights"
    value = "enabled"
  }

  tags = {
    Name = "${var.project_name}-ecs-cluster"
  }
}

# ECS Task Definition — décrit le conteneur Spring Boot
resource "aws_ecs_task_definition" "app" {
  family                   = "${var.project_name}-task"
  network_mode             = "awsvpc"
  requires_compatibilities = ["FARGATE"]
  cpu                      = var.app_cpu
  memory                   = var.app_memory
  execution_role_arn       = var.ecs_exec_role_arn
  task_role_arn            = var.ecs_task_role_arn

  container_definitions = jsonencode([{
    name      = "${var.project_name}-container"
    image     = var.app_image
    essential = true

    portMappings = [{
      containerPort = 8081
      hostPort      = 8081
      protocol      = "tcp"
    }]

    # Variables d'environnement — DB injectée depuis RDS
    environment = [
      { name = "DB_HOST",     value = var.db_host },
      { name = "DB_PORT",     value = "3306" },
      { name = "DB_NAME",     value = var.db_name },
      { name = "DB_USER",     value = var.db_username },
      { name = "DB_PASSWORD", value = var.db_password },
      { name = "SPRING_PROFILES_ACTIVE", value = "docker" }
    ]

    # Logs vers CloudWatch
    logConfiguration = {
      logDriver = "awslogs"
      options = {
        "awslogs-group"         = aws_cloudwatch_log_group.app.name
        "awslogs-region"        = var.aws_region
        "awslogs-stream-prefix" = "ecs"
      }
    }

    # Health check interne
 
  }])

  tags = {
    Name = "${var.project_name}-task-definition"
  }
}

# ECS Service — maintient 1 tâche active
resource "aws_ecs_service" "app" {
  name            = "${var.project_name}-service"
  cluster         = aws_ecs_cluster.main.id
  task_definition = aws_ecs_task_definition.app.arn
  desired_count   = var.app_count
  launch_type     = "FARGATE"

  network_configuration {
    subnets          = [var.private_subnet_id]
    security_groups  = [var.ecs_sg_id]
    assign_public_ip = true   # subnet privé → pas d'IP publique
  }

  load_balancer {
    target_group_arn = var.target_group_arn
    container_name   = "${var.project_name}-container"
    container_port   = 8081
  }

  # Redéploiement forcé si l'image change
  force_new_deployment = true

  depends_on = [var.target_group_arn]

  tags = {
    Name = "${var.project_name}-ecs-service"
  }
}
