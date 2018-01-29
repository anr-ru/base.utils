## [Base.Platform Project](https://github.com/anr-ru/base.platform.parent)

### Base.Platform.Utils

Base utilities for all projects. 

The main idea of the project are *BaseParent* and *BaseSpringParent* classes which have lots of auxiliary functions (or
short-cut functions) and can be used as parent classes in the hierarchy or can be imported statically.

Such an approach allows to fast refer to some very often used functions right in a class.

To start, please add the dependency below to your maven pom file.
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
```
and start your class hierarchy from the [BaseParent](./src/main/java/ru/anr/base/BaseParent.java) or
[BaseSpringParent](./src/main/java/ru/anr/base/BaseSpringParent.java).