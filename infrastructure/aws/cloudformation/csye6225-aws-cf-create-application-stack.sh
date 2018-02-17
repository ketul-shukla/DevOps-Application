#!/bin/bash

stackName=$1
echo "StackName:"$stackName 

imageId="ami-66506c1c"
instanceType="t2.micro"

export SubnetId=$(aws ec2 describe-subnets | grep SubnetId | awk '{print$2}' | tr -d '",'| tr '\n' ' ' | cut -d ' ' -f 1)
export SubnetId2=$(aws ec2 describe-subnets | grep SubnetId | awk '{print$2}' | tr -d '",'| tr '\n' ' ' | cut -d ' ' -f 2)

aws cloudformation create-stack --stack-name $stackName --template-body file://csye6225-cf-application.json --parameters ParameterKey=imgID,ParameterValue=$imageId ParameterKey=instanceType,ParameterValue=$instanceType ParameterKey=volumeType,ParameterValue="gp2" ParameterKey=volumeSize,ParameterValue="16" ParameterKey=DynamoDBTableName,ParameterValue="csye6225" ParameterKey=S3BucketName,ParameterValue="s3.csye6225-spring2018-shuklake.me" ParameterKey=DBEngine,ParameterValue="MySQL" ParameterKey=DBEngineVersion,ParameterValue="5.6.37" ParameterKey=DBInstanceClass,ParameterValue="db.t2.medium" ParameterKey=DBInstanceIdentifier,ParameterValue="csye6225-spring2018" ParameterKey=DBUser,ParameterValue="csye6225master" ParameterKey=DBPassword,ParameterValue="csye6225password" ParameterKey=SubnetId,ParameterValue=$SubnetId ParameterKey=SubnetId,ParameterValue=$SubnetId2 ParameterKey=DBName,ParameterValue="csye6225"

aws cloudformation wait stack-create-complete --stack-name $stackName

aws cloudformation describe-stacks --stack-name $stackName
