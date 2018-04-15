#!/bin/bash

stackName=$1

read -p "Enter User: " userName
bucketName="web-app.csye6225-spring2018-"$userName".me"
echo $bucketName



aws cloudformation create-stack --stack-name $stackName --template-body file://csye6225-cf-ci-cd.json --parameters ParameterKey=CodeDeployEC2S3PolicyName,ParameterValue="CodeDeploy-EC2-S3" ParameterKey=WebAppBucketName,ParameterValue=$bucketName ParameterKey=TravisUploadToS3PolicyName,ParameterValue="Travis-Upload-To-S3" ParameterKey=TravisCodeDeployPolicyName,ParameterValue="Travis-Code-Deploy" ParameterKey=TravisUser,ParameterValue="travis" ParameterKey=CodeDeployEC2ServiceRoleName,ParameterValue="CodeDeployEC2ServiceRole" ParameterKey=CodeDeployServiceRoleName,ParameterValue="CodeDeployServiceRole" ParameterKey=S3BucketName,ParameterValue="code-deploy.csye6225-spring2018-"$userName".me" ParameterKey=CodeDeployApplicationName,ParameterValue="CodeDeployServiceApplication" ParameterKey=IamInstanceProfileName,ParameterValue="CodeDeployEC2InstanceProfile" ParameterKey=LambdaExecutionRoleName,ParameterValue="LambdaExecutionRole" --capabilities CAPABILITY_NAMED_IAM

aws cloudformation wait stack-create-complete --stack-name $stackName

aws cloudformation describe-stacks --stack-name $stackName
