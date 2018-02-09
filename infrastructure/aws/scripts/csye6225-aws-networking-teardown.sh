stackName=$1
echo "Stack Name:"$stackName

routeTableId=`aws ec2 describe-route-tables --filters Name=tag:Name,Values=$stackName-csye6225-public-route-table --query 'RouteTables[].RouteTableId' --output text`
internetGatewayId=`aws ec2 describe-internet-gateways --filters Name=tag:Name,Values=$stackName-csye6225-InternetGateway --query 'InternetGateways[].InternetGatewayId' --output text`
vpcId=`aws ec2 describe-vpcs --filters Name=tag:Name,Values=$stackName-csye6225-vpc --query 'Vpcs[].VpcId' --output text`

echo $routeTableId
echo $internetGatewayId
echo $vpcId 

aws ec2 delete-route-table --route-table-id $routeTableId
aws ec2 detach-internet-gateway --internet-gateway-id=$internetGatewayId --vpc-id=$vpcId	
aws ec2 delete-internet-gateway --internet-gateway-id $internetGatewayId
aws ec2 delete-vpc --vpc-id $vpcId

echo "done"


