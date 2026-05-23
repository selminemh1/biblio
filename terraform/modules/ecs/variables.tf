variable "project_name"      { type = string }
variable "environment"        { type = string }
variable "aws_region"         { type = string }
variable "app_image"          { type = string }
variable "app_cpu"            { type = number }
variable "app_memory"         { type = number }
variable "app_count"          { type = number }
variable "private_subnet_id"  { type = string }
variable "ecs_sg_id"          { type = string }
variable "ecs_task_role_arn"  { type = string }
variable "ecs_exec_role_arn"  { type = string }
variable "target_group_arn"   { type = string }
variable "db_host"            { type = string }
variable "db_name"            { type = string }
variable "db_username"        { type = string }
variable "db_password" {
  type      = string
  sensitive = true
}