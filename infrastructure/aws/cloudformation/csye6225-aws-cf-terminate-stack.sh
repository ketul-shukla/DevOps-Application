#!/bin/bash

stackName=$1
echo "StackName:"$stackName 

stackId=`aws cloudformation describe-stacks --stack-name $1 --query 'Stacks[0].StackId' --output text`
echo $stackId

aws cloudformation delete-stack --stack-name $stackName

aws cloudformation wait stack-delete-complete --stack-name $stackName

aws cloudformation describe-stacks --stack-name $stackId


