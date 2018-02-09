#!/bin/bash

stackName=$1
echo "StackName:"$stackName 

imageId="ami-66506c1c"
instanceType="t2.micro"


aws cloudformation create-stack --stack-name $stackName --template-body file://csye6225-cf-application.json --parameters ParameterKey=imgID,ParameterValue=$imageId ParameterKey=instanceType,ParameterValue=$instanceType ParameterKey=volumeType,ParameterValue="gp2" ParameterKey=volumeSize,ParameterValue="16"

aws cloudformation wait stack-create-complete --stack-name $stackName

aws cloudformation describe-stacks --stack-name $stackName
