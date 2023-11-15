package de.am.analyze.parser.java.listener;

import de.am.analyze.common.component.Component;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.List;

import static de.am.analyze.common.component.type.ComponentType.JAVA_METHOD;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * JUnit test cases of {@link JavaTypeListener} class.
 *
 * @author Martin Absmeier
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class JavaParamListenerTest extends AbstractListenerTest {

    @Disabled
    void checkNumberOfMethods() {
        Component component = application.findComponentByUniqueCoordinate("java.structure.OakTree");
        List<Component> methods = component.findChildrenByType(JAVA_METHOD);
        assertEquals(5, methods.size(), "We expect five methods.");
    }
}