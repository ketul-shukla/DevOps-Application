#!/bin/bash

stackName=$1
echo "StackName:"$stackName 

imageId="ami-66506c1c"
instanceType="t2.micro"

resourceStackName=$2

vpcId=$(aws ec2 describe-vpcs --filters Name=tag:Name,Values=$resourceStackName-csye6225-vpc --query 'Vpcs[].VpcId' --output text)

securityGroupId=$(aws ec2 describe-security-groups --filters Name=vpc-id,Values=$vpcId --query 'SecurityGroups[0].GroupId' --output text)

subnetId=$(aws ec2 describe-subnets --filters Name=tag:Name,Values="$resourceStackName-csye6225-subnet-for-webservers" --query 'Subnets[].SubnetId' --output text)

subnetGroup=$(aws rds describe-db-subnet-groups --filters Name=vpc-id,Values=vpc-74d31e0f --query 'DBSubnetGroups[].DBSubnetGroupName' --output text)

aws cloudformation create-stack --stack-name $stackName --template-body file://csye6225-cf-application.json --parameters ParameterKey=imgID,ParameterValue=$imageId ParameterKey=instanceType,ParameterValue=$instanceType ParameterKey=volumeType,ParameterValue="gp2" ParameterKey=volumeSize,ParameterValue="16" ParameterKey=securityGroupId,ParameterValue=$securityGroupId ParameterKey=subnetId,ParameterValue=$subnetId ParameterKey=DynamoDBTableName,ParameterValue="csye6225" ParameterKey=S3BucketName,ParameterValue="s3.csye6225-spring2018-shuklake.me" ParameterKey=DBEngine,ParameterValue="MySQL" ParameterKey=DBEngineVersion,ParameterValue="5.6.37" ParameterKey=DBInstanceClass,ParameterValue="db.t2.medium" ParameterKey=DBInstanceIdentifier,ParameterValue="csye6225-spring2018" ParameterKey=DBUser,ParameterValue="csye6225master" ParameterKey=DBPassword,ParameterValue="csye6225password" ParameterKey=DBSubnetGroup,ParameterValue=$subnetGroup ParameterKey=DBName,ParameterValue="csye6225"

aws cloudformation wait stack-create-complete --stack-name $stackName

aws cloudformation describe-stacks --stack-name $stackName
