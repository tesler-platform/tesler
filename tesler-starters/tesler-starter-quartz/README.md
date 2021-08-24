# Tesler starter for Quartz

## Prerequisites
Your project uses tesler with tesler-starter-parent, e.g. you have in your pom.xml:
```
<parent>
    <groupId>io.tesler</groupId>
    <artifactId>tesler-starter-parent</artifactId>
    <version>CHANGE_ME</version>
</parent>
```

## Getting started
### Dependency
In your pom.xml add
```
<dependency>
    <groupId>io.tesler</groupId>
    <artifactId>tesler-starter-quartz</artifactId>
</dependency>
```
### Liquibase migrations

In your liquibase change log check following line is included:

```
<include file="classpath:io/tesler/db/changelog/quartz/tesler-starter-quartz.xml" relativeToChangelogFile="false"/>
```

### (Optional) Liquibase migrations
Alternatively one can copy Liquibase migrations files directly to project

