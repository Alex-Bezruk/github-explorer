#!/bin/bash

# Set your AWS region
AWS_REGION="eu-west-1"

# Set your CloudFormation stack name
STACK_NAME="GithubExplorer"

# AWS CLI command to deploy the CloudFormation stack
aws cloudformation create-stack \
  --stack-name $STACK_NAME \
  --template-body file://cloudformation.yml  \
  --capabilities CAPABILITY_IAM CAPABILITY_NAMED_IAM \
  --region $AWS_REGION

# Wait for the stack to complete
echo "Waiting for the stack to complete..."
aws cloudformation wait stack-create-complete --stack-name $STACK_NAME --region $AWS_REGION
