output "alb_sg_id"         { value = aws_security_group.alb.id }
output "ecs_sg_id"         { value = aws_security_group.ecs.id }
output "rds_sg_id"         { value = aws_security_group.rds.id }
output "monitoring_sg_id"  { value = aws_security_group.monitoring.id }
output "ecs_exec_role_arn" { value = data.aws_iam_role.lab_role.arn }
output "ecs_task_role_arn" { value = data.aws_iam_role.lab_role.arn }
