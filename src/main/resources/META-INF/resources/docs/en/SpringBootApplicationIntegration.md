Guide for Spring-boot application integration
---

1. You should add following dependencies to project:
- 	implementation('org.springframework.cloud:spring-cloud-starter-config:2.2.5.RELEASE')
-  	implementation('org.springframework.boot:spring-boot-starter-web')

2. You should create bootstrap.properties files in resource directory
```
spring.application.name=gp-address-service
spring.profiles.active=LOCALHOST:localhost
spring.cloud.config.uri=http://localhost:8081
spring.cloud.config.username=root
spring.cloud.config.password=s3cr3t
```
Important part is: spring.profiles.active
It should be build by pattern:
{ENVIRONMENT}:{ENVIRONMENT_HOST}