# =============================================================
# modules/ecr/main.tf — Amazon ECR
# Registre privé pour les images Docker de l'app
# Remplace Docker Hub une fois Terraform déployé
# =============================================================

resource "aws_ecr_repository" "app" {
  name                 = "${var.project_name}"
  image_tag_mutability = "MUTABLE"
   force_delete         = true

  image_scanning_configuration {
    scan_on_push = true    # DevSecOps : scan Trivy automatique au push
  }

  tags = {
    Name = "${var.project_name}-ecr"
  }
}

# Politique de lifecycle — garder seulement les 5 dernières images
resource "aws_ecr_lifecycle_policy" "app" {
  repository = aws_ecr_repository.app.name

  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Garder les 5 dernières images"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 5
      }
      action = { type = "expire" }
    }]
  })
}
