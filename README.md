# Framework is no longer supported. 

Tesler ![Build Status](https://github.com/tesler-platform/tesler/actions/workflows/build.yml/badge.svg) [![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=tesler-platform_tesler&metric=alert_status)](https://sonarcloud.io/dashboard?id=tesler-platform_tesler) 
=========
Tesler is a framework that allows you to quickly develop applications using the full power of existing enterprise Java solutions. 

1. [Live Demo](http://demo.tesler.io/)
login: `demo`, password: `demo`
2. [Interactive Docs](http://idocs.tesler.io/)
login: `demo`, password: `demo`
3. [Base Info](http://tesler.io/) 
---

## Core features

Tesler includes:

- Abstraction of a business component to simplify access to data;
- A fixed contract with a user interface called [Tesler-UI](https://github.com/tesler-platform/tesler-ui), which allows you to create typical interface elements in the form of Json files;
- A single DAO layer, simplifying work with JPA;
- The SQL engine that allows you to quickly generate typical business components in the application;
- Abstraction of the task scheduler to create background tasks;
- Built-in BPM - an engine that allows you to create business processes.

## Projects

Tesler is based on the following opensource projects:

- Spring 5.x and Spring Boot 2.x;
- Hibernate as a JPA implementaton;
- Liquibase for database migration;
- Quartz for planning tasks;
- Etc.

## Database support

The following databases are currently supported:

- Postgresql 9.6.15 and later
- Oracle 11g and later

## Versioning

Tesler follows [semver](https://semver.org/), e.g. MAJOR.MINOR.PATCH
All significant changes are documented in our [changelog file](./CHANGELOG.md).  
Backwards incompatible changes are denoted with `[BREAKING CHANGE]` mark

## Contributing

Please check ours [contributing guide](./CONTRIBUTING.md)
