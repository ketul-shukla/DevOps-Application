#!/bin/bash

stackName=$1
echo "Stack Name:"$stackName

vpcName=$1"-csye6225-vpc"
echo "Vpc Name:"$vpcName 

internetGateway=$1"-csye6225-InternetGateway"
echo "Internet Gateway:"$internetGateway

routeTableName=$1"-csye6225-public-route-table"
echo "Route Table Name:"$routeTableName


vpcId=`aws ec2 create-vpc --cidr-block 10.0.0.0/25 --query 'Vpc.VpcId' --output text`

aws ec2 create-tags --resources $vpcId --tags Key=Name,Value=$vpcName

internetGatewayId=`aws ec2 create-internet-gateway --query 'InternetGateway.InternetGatewayId' --output text`

aws ec2 create-tags --resources $internetGatewayId --tags Key=Name,Value=$internetGateway

aws ec2 attach-internet-gateway --internet-gateway-id $internetGatewayId --vpc-id $vpcId

routeTableId=`aws ec2 create-route-table --vpc-id $vpcId --query 'RouteTable.RouteTableId' --output text`

aws ec2 create-tags --resources $routeTableId --tags Key=Name,Value=$routeTableName

aws ec2 create-route --route-table-id $routeTableId --destination-cidr-block 0.0.0.0/0 --gateway-id $internetGatewayId

echo $vpcId
echo $internetGatewayId
echo $routeTableId
