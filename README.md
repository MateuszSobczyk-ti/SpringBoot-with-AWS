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

Create bucket (like a folder) in S3 file storage service
- URL<br>
/api/file-store/createBucket
- Method<br>
POST
- URL params<br>
bucket_name=[string]
- Success response<br>
Code: 201 CREATED<br>
Content: ``Bucket `bucket_name` has been created.``
- Error response<br>
Code: 409 Conflict<br>
Content: ``Bucket name already exists.``

Upload your image to bucket
- URL<br>
/api/file-store/upload
- Method<br>
POST
- URL params<br>
file=[jpg/png/gif]<br>
  Optional:<br>
bucket_name=[string] (if not provided, then image is saved to bucket predefined as default)
- Success response<br>
Code: 200 OK<br>
Content: ``File has been saved in bucket (or folder): `bucket_name```
- Error response<br>
Code: 404 NOT_FOUND<br>
Content: ``Bucket `bucket_name` does not exists.``<br>
  or<br>
Code: 406 NOT_ACCEPATBLE<br>
Content: ``File cannot be empty and must be in jpeg, png or gif format``

Download image from bucket
- URL<br>
/api/file-store/download
- Method<br>
GET
- URL params<br>
  Optional:<br>
bucket_name=[string] (if not provided, then image is downloaded from default bucket)
- Success response<br>
Code: 200 OK<br>
Content: [byte array]
- Error response<br>
Code: 404 NOT_FOUND<br>
Content: ``Bucket `bucket_name` does not exists.``<br>

Get image url
- URL<br>
/api/file-store/getImageUrls
- Method<br>
GET
- URL params<br>
  Optional:<br>
bucket_name=[string] (if not provided, then image is from default bucket)<br>
duration=[number] (by default is 10 seconds)
- Success response<br>
Code: 200 OK<br>
Content: [string array]
- Error response<br>
Code: 404 NOT_FOUND<br>
Content: ``Bucket `bucket_name` does not exists.``<br>
- Notes
After time specified as 'duration' is over then access to url is denied. In order to get access again, you need to generate new url.

Upload your image to default bucket and save additional information in database
- URL<br>
/api/file-store/uploadDB
- Method<br>
POST
- URL params<br>
file=[jpg/png/gif]<br>
name=[string]<br>
  Optional:<br>
description=[string] 
- Success response<br>
Code: 200 OK<br>
Content: ``File `file_name` has been saved.``
- Error response<br>
Code: 406 NOT_ACCEPATBLE<br>
Content: ``File cannot be empty and must be in jpeg, png or gif format.``<br>
  or<br>
Code: 409 Conflict<br>
Content: ``File name cannot be empty and must be unique.``
- Notes
Information stored in database: name, description, link to image, image size, download counter. Image is stored in S3 in default bucket.

Get additional information about images
- URL<br>
/api/file-store/getImageUrlsDB
- Method<br>
GET
- URL params<br>
  Optional:<br>
duration=[number] (by default is 10 seconds)
- Success response<br>
Code: 200 OK<br>
Content: {<br>
&emsp;"files": [number],<br>
    "downloadCounter": [number],<br>
    "totalImagesSizeInKB": [number],<br>
    "fileData": [<br>
&emsp;&emsp;{<br>
            "name": [string],<br>
            "description": [string],<br>
            "imageUrl": [string],<br>
            "imageSizeInKB": [number]<br>
        }<br>
    ]<br>
}
