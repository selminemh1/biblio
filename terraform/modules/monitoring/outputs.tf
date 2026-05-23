output "monitoring_public_ip"  { value = aws_instance.monitoring.public_ip }
output "monitoring_private_ip" { value = aws_instance.monitoring.private_ip }
output "sns_topic_arn"         { value = aws_sns_topic.alerts.arn }
