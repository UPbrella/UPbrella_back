#!/bin/bash
container_name=upbrella-server-dev

#ECR Login
#aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin awsaccountid.dkr.ecr.us-east-1.am

#Pulling image from ECR
docker pull .dkr.ecr.ap-northeast-2.amazonaws.com/upbrella-server-dev:latest

##Changing image tag
# docker image tag awsaccountid.dkr.ecr.us-east-1.amazonaws.com/image:latest $container_name:latest

#stop and remove the current container docker rm -f $container_name

#Creating and starting a docker container using a new image
docker run -d -p 80:80 --name $container_name $container_name:latest