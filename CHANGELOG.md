# Version 3.0.4

## Breaking changes
* ⚠BREAKING⚠  Supported single Servlet configuration [#213](https://github.com/tesler-platform/tesler/pull/213)
* ⚠BREAKING⚠  Introduced new file api  [#217](https://github.com/tesler-platform/tesler/pull/217)

## Non-breaking features
* Added support to descriptionFile option on widgets [#231](https://github.com/tesler-platform/tesler/pull/231)
* Added one-liner for DTO-to-Entity mapping [#225](https://github.com/tesler-platform/tesler/pull/225)
* Reworked Spring Cache configuration [#221](https://github.com/tesler-platform/tesler/pull/221)
* Supported dataSource and entityManagerFactory injection by name defined in application.yml [#215](https://github.com/tesler-platform/tesler/pull/215)
* Updated dependencies versions [#208](https://github.com/tesler-platform/tesler/pull/208), [#209](https://github.com/tesler-platform/tesler/pull/209), [#210](https://github.com/tesler-platform/tesler/pull/210), [#211](https://github.com/tesler-platform/tesler/pull/211), [#212](https://github.com/tesler-platform/tesler/pull/212)
* Removed legacy code [#202](https://github.com/tesler-platform/tesler/pull/202), [#203](https://github.com/tesler-platform/tesler/pull/203), [#205](https://github.com/tesler-platform/tesler/pull/205), [#206](https://github.com/tesler-platform/tesler/pull/206), [#207](https://github.com/tesler-platform/tesler/pull/207)
* Connected files moved to tesler-starter-sqlbc [#204](https://github.com/tesler-platform/tesler/pull/204)
* Connected files moved to tesler-starter-notifications [#201](https://github.com/tesler-platform/tesler/pull/201)
* Connected files moved to tesler-starter-quartz [#200](https://github.com/tesler-platform/tesler/pull/200)

## Fixes
* Fxed DAO layer in Workflow plugin [#227](https://github.com/tesler-platform/tesler/pull/227)
* Added try-catch block to avoid error while loading services [#226](https://github.com/tesler-platform/tesler/pull/226)
* Fixed meta insert performance [#214](https://github.com/tesler-platform/tesler/pull/214)

# Version 3.0.3

## Breaking changes
* ⚠BREAKING⚠ tesler access lists security feature removed from tesler. Tables NOTIFICATION_RECIPIENT, NOTIFICATION_SETTINGS, NOTIFICATION_TEMPLATE, NOTIFICATION are deleted [#189](https://github.com/tesler-platform/tesler/pull/189)
* ⚠BREAKING⚠ tables PRJ, PRJ_GRP, PRJ_GRP_USER, LOGIN_ROLE removed from tesler [#192](https://github.com/tesler-platform/tesler/pull/192)
* ⚠BREAKING⚠ tables USER_ACTIVITY, LAYOUTS_VIEWS, LAYOUTS_WIDGETS, WIDGET_PROPERTY  removed from tesler [#195](https://github.com/tesler-platform/tesler/pull/195)

## Non-breaking features
* IDeletedListener, IFieldChangedListener and  ICreatedListener added in addition to IChangeListener for convetient entoty chages tracking [#193](https://github.com/tesler-platform/tesler/pull/193)
* liquibase migrations for optional dependency tesler-starter-sqlbc are now embeded in it. Readme.md for starter added with instructions to turn it on. So you do not need to create this tables if starter is not used in your project! [#186](https://github.com/tesler-platform/tesler/pull/186)
* liquibase migrations for optional dependency tesler-starter-notifications-* are now embeded in tesler-starter-notifications-model. Readme.md for starter added with instructions to turn it on. So you do not need to create this tables if starter is not used in your project! [#185](https://github.com/tesler-platform/tesler/pull/185)
* liquibase migrations for optional dependency tesler-starter-quartz are now embeded in it. Readme.md for starter added with instructions to turn it on. So you do not need to create this tables if starter is not used in your project!  [#184](https://github.com/tesler-platform/tesler/pull/184)
* commonScreens.json file is now optional (e.g. you can delete it, if you do not use it) [#183](https://github.com/tesler-platform/tesler/pull/183)
* opportunity to define roles (for which view is available) directly in .view.json. Add tesler.meta.view-allowed-roles-enabled = true to your application.properties to turn this feature on [#182](https://github.com/tesler-platform/tesler/pull/182) [#196](https://github.com/tesler-platform/tesler/pull/196)
* MultivalueOptionType additional value HIDDEN adde [#197](https://github.com/tesler-platform/tesler/pull/197)
* tesler archetype temporarily replaced with tesler-demo (see https://github.com/tesler-platform/tesler-demo). Tesler archetype will come back in next releases with tesler-demo ui and features [#198](https://github.com/tesler-platform/tesler/pull/198)

# Version 3.0.0-3.0.2

## Breaking changes

* ⚠BREAKING⚠ Bumped spring-boot version to 2.5.1 (with all transitive dependencies) [#154](https://github.com/tesler-platform/tesler/pull/154) [#156](https://github.com/tesler-platform/tesler/pull/156)
* ⚠BREAKING⚠ WF engine was moved to other git repository [#114](https://github.com/tesler-platform/tesler/pull/114)
* ⚠BREAKING⚠ Changed @SearchParameter annotation API - added ClassifyDataProvider interface [#116](https://github.com/tesler-platform/tesler/pull/116)
* ⚠BREAKING⚠ Removed custom changes in liquibase (`tesler-liquibase` module) [#156](https://github.com/tesler-platform/tesler/pull/156)
* ⚠BREAKING⚠ Hot Reload support - Moved UI tables (SCREEN, VIEW, WIDGET, BC) load from liquibase to Spring bean [#170](https://github.com/tesler-platform/tesler/pull/170)
* ⚠BREAKING⚠ SQL BC Engine moved to tesler-starter [#133](https://github.com/tesler-platform/tesler/pull/133)
* ⚠BREAKING⚠ Quartz configurataions moved to tesler-starter [#157](https://github.com/tesler-platform/tesler/pull/157)
* ⚠BREAKING⚠ Notifications engine moved to tesler-starter [#159](https://github.com/tesler-platform/tesler/pull/159)


## Non-breaking features

* Created tesler-parent module for all projects based on tesler [#156](https://github.com/tesler-platform/tesler/pull/156) 
* Added ability to use enums instead of LOV's [#160](https://github.com/tesler-platform/tesler/pull/160)
* Extend state with crudma action that invokes state saving [#118](https://github.com/tesler-platform/tesler/pull/118)
* Custom Action now can be invoked without id [#119](https://github.com/tesler-platform/tesler/pull/119)
* Drill down types now can be extended [#121](https://github.com/tesler-platform/tesler/pull/121)
* Support `spring-session` library for clustered environments (e.g. having >1 app node) [#123](https://github.com/tesler-platform/tesler/pull/123) [#124](https://github.com/tesler-platform/tesler/pull/124)
* Extend ExceptionHandler's with stacktrace [#127](https://github.com/tesler-platform/tesler/pull/127)
* Added `option` field in `row-meta` API [#128](https://github.com/tesler-platform/tesler/pull/128)
* Extend action groups with icons [#129](https://github.com/tesler-platform/tesler/pull/129)
* Created tesler-starter module abstraction [#133](https://github.com/tesler-platform/tesler/pull/133)
* Added hibernate dirty-checking compile-time optimization [#144](https://github.com/tesler-platform/tesler/pull/144)

## Fixes

* Fixed dates filtering [#122](https://github.com/tesler-platform/tesler/pull/122)
* Fixed multivalue fields filtering [#125](https://github.com/tesler-platform/tesler/pull/125) [#126](https://github.com/tesler-platform/tesler/pull/126)
* Disabled sort in count query [#138](https://github.com/tesler-platform/tesler/pull/138)
* Improved SQL sequences management for BATCH inserts [#170](https://github.com/tesler-platform/tesler/pull/141)
* Fixed support of lazy fields in hibernate [#145](https://github.com/tesler-platform/tesler/pull/145)

## Other

* Improve Javadoc coverage in core packages [#116](https://github.com/tesler-platform/tesler/pull/116)
* Replaced old archetypes with single module `tesler-simple-archetype` [#114](https://github.com/tesler-platform/tesler/pull/114) [#147](https://github.com/tesler-platform/tesler/pull/147) [#149](https://github.com/tesler-platform/tesler/pull/149)
* Added groovy script in archetype to generate UI module via `create-react-app` [#117](https://github.com/tesler-platform/tesler/pull/117) [#165](https://github.com/tesler-platform/tesler/pull/165)
* Improved dependency management [#156](https://github.com/tesler-platform/tesler/pull/156)

# Version 2.3.0

## Features

* Create ability to change Crudma flow by implement `CrudmaGatewayInvokeExtensionProvider` interface
* Change interfaces responsible for Crudma state
* Added ability to create a custom parameter in Action
* Added ability to create a custom parameter in Pre-Action
* Added scheduler settings in Spring application.properties
* Added ability to extend icon codes
* Extend JdbcTemplate based export

## Fixes

* DTO validation should respect `ignoreBusinessErrors` flag and do not validate fields during preview call
* `Cancel create` now remain after a preview request call
* Fixed duplication during OneToMany field filtering
* Fixed HierarchyFieldExtractor for non-popup Hierarchy widgets

# Version 2.2.0

## Features

* new `/sceens` endpoint that contains screen UI structure (extracted from `/login` endpoint) (#69)
* Added support of declare custom widget fields. Change `FieldMeta` deserialization process (#70)
* Added new `radio` field type and `placeholders` for `input` field (#67)
* Added support to override `BaseEntityListener` methods (#78)

## Other
* Delete deprecated `@Aliases` annotation (#65)
* Move CustomObjectMapper to `JacksonConfig` configuration file (#70)
* Delete unused methods from `BcRegistry` (#70)
* Added Code Coverage and Sonar Quality Gate checks (#71 #72 #79)

# Version 2.1.1

## Features

* Clear unused dependencies. Add `spring-boot-dependencies` (#54)
* Errors block added to row meta preview (#62)

## Fixes

* Notifications: bitand workaround for postgreSQL (#61)
* Fix filters in `SearchParameterPOJO` (#58)
* Fixed exception message in `AbstractResponseService#getOneAsEntity`

# Version 2.1.0

## Features

* Add availability to create custom field properties on `widget.json` file (#51)
* Added search parameter processing to multi value field (#53)

## Fixes

* `AbstactResponseService#getOneAsEntity` should use `getParentSpecification` and specifications, provided by `SpecificationHolder`'s (#47)
* Archetypes: fixed Postgres compitability. (#49)
* Archetypes: fixed typos. (#52)
* Custom actions now dont trigger update, if `autosaveBefore` flag is not present(#48)

# Version 2.0.3

## Fixes

* Compitability fix: JpaDao#getSupportedEntityManager now supports entity SimpleName as input argument (#43)
* Removed "Create" action while getting row-meta for new record (#45)

# Version 2.0.2

## Fixes

* Move ResponsibilityService configuration to CoreApplicationConfig (#41)
* Fixed navigation sorting on UIServiceImpl (#40)
* Added snapshotState field to FieldsMeta  (#39)
* Create interface for ResponsibilitiesService (#38)

# Version 2.0.1

## Fixes

* TransactionService#woAutoFlush should start transaction, if it doesnt exists (#32)
* Archetypes: added java 11 support and fixed formatting (#33)
* Added linked fields support in HierarchyFieldExtractor (#34)
* More flexible PostAction api (#36)

# Version 2.0.0

## Features

* Added changelog file (#25)
* [Breaking changes] Changed navigation part of screen.json metadata (#18) see wiki: https://github.com/tesler-platform/tesler/wiki/Metadata.-Navigation.
  * Changed JSON contract in liquibase module and ui module
  * Remove deprecated delegation feature.
  * Changed archetype modules.
* [Breaking changes] Added ability to run a Spring Boot with 2 or more EntityManagerFactory (#22)
  * Replaced the user and department with an Long identifier on some core entities
  * Some classes now use collection of EntityManagers instead of one.
  * Added support of declare ChainedTransactionManager.

## Fixes

* Fixed typo in io.tesler.model.core.converter package bug (#24)
* Fixed ActionCancel, now adding action happens without AutoSaveBefore.

# Version 1.0.4

## Features

* Breadcrumbs hiding flag added (#17).

# Version 1.0.3

## Features

* Add autoSaveBefore flag on ActionDTO (#15).
* Added README.md and CONTRIBUTING.md (#9).
* Bump lombok and commons-lang3. Added Java 11 compatibility (#8).
* Added feature to use custom DB sequences in entities, which are inherited from BaseEntity (#7).

## Fixes

* Fixed event processing order (#11).




