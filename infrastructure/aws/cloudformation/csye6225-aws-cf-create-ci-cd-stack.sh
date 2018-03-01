#!/bin/bash

stackName=$1

aws cloudformation create-stack --stack-name $stackName --template-body file://csye6225-cf-ci-cd.json --parameters ParameterKey=CodeDeployEC2S3PolicyName,ParameterValue="CodeDeploy-EC2-S3" ParameterKey=WebAppBucketName,ParameterValue="web-app.csye6225-spring2018-dantalaa.me" ParameterKey=TravisUploadToS3PolicyName,ParameterValue="Travis-Upload-To-S3" ParameterKey=TravisCodeDeployPolicyName,ParameterValue="Travis-Code-Deploy" ParameterKey=TravisUser,ParameterValue="travis" ParameterKey=CodeDeployEC2ServiceRoleName,ParameterValue="CodeDeployEC2ServiceRole" ParameterKey=CodeDeployServiceRoleName,ParameterValue="CodeDeployServiceRole" ParameterKey=S3BucketName,ParameterValue="code-deploy.csye6225-spring2018-dantalaa.me" ParameterKey=CodeDeployApplicationName,ParameterValue="CodeDeployServiceApplication" ParameterKey=CodeDeployApplicationComputePlatform,ParameterValue="Server" ParameterKey=DeploymentStyleForApplication,ParameterValue="IN_PLACE" ParameterKey=DeploymentOptionForApplication,ParameterValue="WITHOUT_TRAFFIC_CONTROL" ParameterKey=Ec2TagFiltersKey,ParameterValue="Name" ParameterKey=Ec2TagFiltersValue,ParameterValue="csye6225-EC2Instance" ParameterKey=Ec2TagFiltersType,ParameterValue="KEY_AND_VALUE" ParameterKey=CodeDeployConfig,ParameterValue="CodeDeployDefault.OneAtATime" ParameterKey=IamInstanceProfileName,ParameterValue="CodeDeployEC2InstanceProfile" --capabilities CAPABILITY_NAMED_IAM

aws cloudformation wait stack-create-complete --stack-name $stackName

aws cloudformation describe-stacks --stack-name $stackName
