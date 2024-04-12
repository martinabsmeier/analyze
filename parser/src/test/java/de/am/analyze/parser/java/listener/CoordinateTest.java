package de.am.analyze.parser.java.listener;

import de.am.analyze.common.component.Component;
import org.junit.jupiter.api.Test;

import java.util.List;

import static de.am.analyze.common.AnalyzeConstants.COMMON.SEPARATOR;
import static de.am.analyze.common.component.type.ComponentType.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * JUnit test cases to check if the coordinate is correct.
 *
 * @author Martin Absmeier
 */
class CoordinateTest extends AbstractListenerTest {

    @Test
    void checkClassCoordinate() {
        System.out.println(SEPARATOR);
        System.out.println("Classes");
        System.out.println(SEPARATOR);
        List<Component> classes = application.findAllComponentsByType(JAVA_CLASS);
        classes.stream()
                .map(Component::getUniqueCoordinate)
                .forEach(System.out::println);
        assertTrue(true);
    }

    @Test
    void checkInterfaceCoordinate() {
        System.out.println(SEPARATOR);
        System.out.println("Interfaces");
        System.out.println(SEPARATOR);
        List<Component> interfaces = application.findAllComponentsByType(JAVA_INTERFACE);
        interfaces.stream()
                .map(Component::getUniqueCoordinate)
                .forEach(System.out::println);
        assertTrue(true);
    }

    @Test
    void checkEnumCoordinate() {
        System.out.println(SEPARATOR);
        System.out.println("Enumerations");
        System.out.println(SEPARATOR);
        List<Component> enumerations = application.findAllComponentsByType(JAVA_ENUM);
        enumerations.stream()
                .map(Component::getUniqueCoordinate)
                .forEach(System.out::println);
        assertTrue(true);
    }
}
