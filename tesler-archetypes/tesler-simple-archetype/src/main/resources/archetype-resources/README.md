# Tesler Simple Project

### Prerequisites:
#####
- Java 8+
- maven 3.6+
- Node.js 14.4+
- npm 6.14+
- Git

### Startup process:
##### 1 Start infrastructure locally with docker
`docker-compose up`
##### 2 Build project
`mvn clean install -P UI`
##### 3 Run
`mvn spring-boot:run`

### Open application:
##### 1 Url
`http://localhost:8080/ui/#`
##### 2 Credentials
`admin/admin`

### (Optional) Development process (when UI is not changed):
##### 1 Build project
`mvn clean install`
##### 2 Run
`mvn spring-boot:run`

### (Optional) Development process in IntelliJ Idea (when only Java classes changed):
##### 1 Build project and run
`Press green button in Application.java class`


### (Optional) SSO Keycloack
By default archetype is configured with keycloak (see admin/admin localhost:9080). For more information please check SSOREADME.MD