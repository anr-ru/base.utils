[![Build Status](https://travis-ci.org/anr-ru/base.utils.svg?branch=master)](https://travis-ci.org/anr-ru/base.utils)

## A part of [Base.Platform Project](https://github.com/anr-ru/base.platform.parent)

### Base.Platform.Utils

Base utilities for all projects.

The main idea of the project are [BaseParent](./src/main/java/ru/anr/base/BaseParent.java) and
[BaseSpringParent](./src/main/java/ru/anr/base/BaseSpringParent.java) (a child of BaseParent) classes which have
lots of auxiliary functions (or short-cut functions). You can just start your classes hierarchy from these classes
or import them statically.

There are lots of other useful functions:

1. Parsing operations;
2. Date/Time/Decimal/Money formatting;
3. Transformers between data types;
4. Special operation for Spring beans, tricky injection cases;

Such an approach allows to fast refer to some very often used functions right in a class.
For example,

```java
import ru.anr.base.BaseParent;

import java.time.LocalDate;

public class MyClass extends BaseSpringParent {

    public void myFunc() {

        Map<String, Object> map = isProdMode() ? toMap("value", 3.14, "desc", "pi") : toMap();
        // ...
        LocalDate d = parseLocalDate("2010-02-03", "YYYY-MM-dd").orElse(null);
        LocalDate v = nullSafe(d, d -> d.plusDays(1)).orElse(now().toLocalDate());
        
        //...
        String s = readAsString("/tmp/file.xtx");
        byte[] bytes = readAsBytes("/tmp/file.bin");
        
        //...
        parse("3.14", BigDecimal.class);
        parseEnum(XXXEnum.class, "X");
        
        //...
        regexpGroups("x > 2", "(\d{1})", 1);
        xpath("<object a='1'></object", "//@a");
    }
}
```

To start, please add the dependency below to your maven pom file.

```xml

<depepencies>
    ...
    <dependency>
        <groupId>ru.anr</groupId>
        <artifactId>base.utils</artifactId>
        <version>2.5.0</version>
    </dependency>
    ...
</dependencies>
```
