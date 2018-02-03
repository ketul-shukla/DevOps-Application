#!/bin/bash

stackName=$1
echo "StackName:"$stackName 

aws cloudformation create-stack --stack-name $stackName --template-body file://csye6225-cf-networking.json

aws cloudformation wait stack-create-complete --stack-name $stackName

aws cloudformation describe-stacks --stack-name $stackName







