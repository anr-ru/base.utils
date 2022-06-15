package ru.anr.base;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ModelUtils}.
 *
 * @author Alexey Romanchuk
 * @created Jun 15, 2022
 */
public class ModelUtilsTest extends BaseParent {

    private static class NestedModel {
        String id;
    }

    private static class UpperModel {
        NestedModel nested;
    }


    @Test
    public void test() {

        UpperModel m = new UpperModel();

        // 1. The source value is null
        Assertions.assertNull(ModelUtils.extractProperty(m.nested, n -> n.id, id -> "@" + id));

        // 2. The child value of the source value is null
        m.nested = new NestedModel();
        Assertions.assertNull(ModelUtils.extractProperty(m.nested, n -> n.id, id -> "@" + id));

        // 3. The child value is not null
        m.nested = new NestedModel();
        m.nested.id = "SG";
        Assertions.assertEquals("@SG", ModelUtils.extractProperty(m.nested, c -> c.id, id -> "@" + id));
    }

    private enum TestEnum {
        ONE, TWO;
    }

    @Test
    public void testEnum() {
        Assertions.assertEquals(list("ONE", "TWO"), ModelUtils.enumToStr(list(TestEnum.ONE, TestEnum.TWO)));
    }
}
