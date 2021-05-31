# Tesler Simple Project

### Startup process:

##### 0. The following components must be installed:
  - Java Development Kit
  - IntelliJ IDEA Ultimate with lombok plugin
  - Node.js
  - Git
#####  1. Сlone project repository
##### 2. Checkout *dev* branch
##### 3. Start db infrastructure locally in docker 
Press green button in docker-compose.yml file
##### 4. Add PostgreSQL Data Source in tab *Database* in IntelliJ IDEA
##### 5. Compile project
In Terminal in IntelliJ IDEA:
`$ mvn clean install -P UI`
##### 5.1. Compile project when ui is not changed
`$ mvn clean install`
##### 5.2. Compile project when ui is not changed
Press green button in Application.java class
##### 6. Run project
In Terminal in IntelliJ IDEA:
`$ mvn spring-boot:run`
##### 6.1 (optional) Run project in dev mode to enable show/refresh meta options
In package.json set change "proxy" to *http://localhost:8080/api/v1*,
press green button in package.json on "start" script
##### 7. Open application
In browser go to:  *http://locallhost:8080/ui/#*
(*http://localhost:3000/ui#/* for dev mode)

Username: *vanilla*

Password: *vanilla*
