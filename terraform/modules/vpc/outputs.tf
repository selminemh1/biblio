output "vpc_id"            { value = aws_vpc.main.id }
output "public_subnet_id"  { value = aws_subnet.public.id }
output "private_subnet_id" { value = aws_subnet.private.id }
output "db_subnet_1_id"    { value = aws_subnet.db_1.id }
output "db_subnet_2_id"    { value = aws_subnet.db_2.id }
output "public_subnet_2_id" { value = aws_subnet.public_2.id }