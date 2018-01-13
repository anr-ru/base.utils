# Base.Platform.Utils

Base utilities for all projects. 

The main idea of the project are *BaseParent* and *BaseSpringParent* classes which have lots of auxiliary functions (or
short-cut functions) and can be used as parent classes in the hierarchy.

Such an approach allows to fast refer to the functions rights in a class.

To start, please add the dependency to your maven pom file.
```xml
  <depepencies>
    ...
        <dependency>
            <groupId>ru.anr</groupId>
            <artifactId>base.utils</artifactId>
            <version>...</version>
        </dependency>
    ...
  </dependencies>

and then extend your class with the [BaseParent](./src/main/java/ru/anr/base/BaseParent) or [BaseSpringParent](../src/main/java/ru/anr/base/BaseSpringParent)