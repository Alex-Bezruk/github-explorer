#!/bin/bash

# Set your AWS region
AWS_REGION="eu-west-1"

# Set your CloudFormation stack name
STACK_NAME="GithubExplorerStackTest"

# AWS CLI command to deploy the CloudFormation stack
aws cloudformation create-stack \
  --stack-name $STACK_NAME \
  --template-body file://template.yml  \
  --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM CAPABILITY_AUTO_EXPAND \
  --region $AWS_REGION
