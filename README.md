# TestThingApp

## Overview
This is simple backend application, which helped me develop my skills in term of Java language, Spring framework and database management.
What's more, I learned and used brand new (for me) technologies like: AWS Cloud, Java streams, Docker.

But what does application do? It provides API to:
- create new bucket in S3 service
- save image to bucket (not docker image, just image)
- download images from bucket in byte format
- download images URL with time duration (after time duration is over - access to url is denied)
- save image to bucket and save information about image (like name, description, size, link to image, download counter) to RDS - cloud database service
- download images URL and more information about files in bucket like: number of images, total images size, total number of download

## Technologies
- Java 17, Maven, Spring Boot, Git, REST
- Docker (to build image based on application and then run in AWS ECS environment)
- AWS ECS - it is fully managed container orchestration service, so this service runs EC2 server by itself, we only put image to ECS service. 
Under the hood, it consist of many AWS services like: Task, Service, Cluster. I deployed Docker image via web-based wizard, not AWS CLI (so it cannot be automated unfortunatelly)
- Amazon RDS - Relational Database Service with MySQL engine (in my case)
- Spring Data Jpa - improve the implementation of data access layer

## How exactly it works?
I recommend you to test my app with your favorite API tool like Postman, Swagger or curl. My domain is 54.72.253.131, port is 8080. So to test
if you can reach my api, send GET request without parameters `54.72.253.131:8080/api/file-store/test`. The answer should be "Helllllo" (It may doesn't work because AWS ECS
is not a free service, so my app will not be available for a long time. Otherwise you can download docker image and run app on-premises).

#### API's description:
- URL 
/api/file-store/createBucket
- Method
GET
- URL params
bucket_name=[string]
- Success response
Code:200
Content: ``Bucket `bucket_name` has been created.``
- Error response
Code:409 Conflict
Content: ``Bucket name already exists.``

