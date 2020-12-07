Гайд по интеграции SpringBoot-приложения с конфигурационным сервером
---

1. Добавить следующие зависимости:
- 	implementation('org.springframework.cloud:spring-cloud-starter-config:2.2.5.RELEASE')
-  	implementation('org.springframework.boot:spring-boot-starter-web')

2. Создать bootstrap.properties файл в директории с ресурсами
```
spring.application.name=gp-address-service
spring.profiles.active=LOCALHOST:localhost
spring.cloud.config.uri=http://localhost:8081
spring.cloud.config.username=root
spring.cloud.config.password=s3cr3t
```
Важно: spring.profiles.active
Должно быть построено по шаблону:
{ENVIRONMENT}:{ENVIRONMENT_HOST}