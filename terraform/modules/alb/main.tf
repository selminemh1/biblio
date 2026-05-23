# =============================================================
# modules/alb/main.tf — Application Load Balancer
# Point d'entrée public → redirige vers ECS sur port 8081
# =============================================================

resource "aws_lb" "main" {
  name               = "${var.project_name}-alb"
  internal           = false
  load_balancer_type = "application"
  security_groups    = [var.alb_sg_id]
  subnets            = var.public_subnet_ids

  tags = {
    Name = "${var.project_name}-alb"
  }
}

# Target Group — pointe vers ECS sur port 8081
resource "aws_lb_target_group" "app" {
  name        = "${var.project_name}-tg"
  port        = 8081
  protocol    = "HTTP"
  vpc_id      = var.vpc_id
  target_type = "ip"    # obligatoire pour ECS Fargate

health_check {
  enabled             = true
  path                = "/actuator/health"
  port                = "traffic-port"
  healthy_threshold   = 2
  unhealthy_threshold = 10
  timeout             = 10
  interval            = 30
  matcher             = "200"
}

  tags = {
    Name = "${var.project_name}-target-group"
  }
}

# Listener HTTP port 80 → forward vers Target Group
resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.main.arn
  port              = "80"
  protocol          = "HTTP"

  default_action {
    type             = "forward"
    target_group_arn = aws_lb_target_group.app.arn
  }
}
