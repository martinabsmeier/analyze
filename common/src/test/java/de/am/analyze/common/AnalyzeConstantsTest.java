package de.am.analyze.common;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class AnalyzeConstantsTest {

    @Test
    void COMMON_SEPARATOR_ShouldBeCorrect() {
        assertEquals("--------------------------------------------------------------------------------", AnalyzeConstants.COMMON.SEPARATOR);
    }

    @Test
    void COMMON_USER_DIR_ShouldNotBeNull() {
        assertNotNull(AnalyzeConstants.COMMON.USER_DIR);
    }

    @Test
    void COMMON_HOME_DIR_ShouldNotBeNull() {
        assertNotNull(AnalyzeConstants.COMMON.HOME_DIR);
    }

    @Test
    void DEFAULT_ALGORITHM_ShouldBeSHA512() {
        assertEquals("SHA-512", AnalyzeConstants.DEFAULT.ALGORITHM);
    }

    @Test
    void DEFAULT_ENCODING_ShouldBeUTF8() {
        assertEquals(StandardCharsets.UTF_8, AnalyzeConstants.DEFAULT.ENCODING);
    }

    @Test
    void DEFAULT_REVISION_ID_ShouldBeV001() {
        assertEquals("v-0.0.1", AnalyzeConstants.DEFAULT.REVISION_ID);
    }

    @Test
    void JAVA_DELIMITER_ShouldBeDot() {
        assertEquals(".", AnalyzeConstants.JAVA.DELIMITER);
    }

    @Test
    void JAVA_DELIMITER_REGEX_ShouldBeDotRegex() {
        assertEquals("[.]", AnalyzeConstants.JAVA.DELIMITER_REGEX);
    }

    @Test
    void JAVA_DEFAULT_PACKAGE_ShouldBeDefault() {
        assertEquals("default", AnalyzeConstants.JAVA.DEFAULT_PACKAGE);
    }

    @Test
    void JAVA_LANG_PACKAGE_ShouldBeJavaLang() {
        assertEquals("java.lang", AnalyzeConstants.JAVA.LANG_PACKAGE);
    }

    @Test
    void JAVA_IO_PACKAGE_ShouldBeJavaIo() {
        assertEquals("java.io", AnalyzeConstants.JAVA.IO_PACKAGE);
    }

    @Test
    void JAVA_MODIFIER_PUBLIC_ShouldBePublic() {
        assertEquals("public", AnalyzeConstants.JAVA.MODIFIER_PUBLIC);
    }

    @Test
    void JAVA_MODIFIER_ABSTRACT_ShouldBeAbstract() {
        assertEquals("abstract", AnalyzeConstants.JAVA.MODIFIER_ABSTRACT);
    }

    @Test
    void JAVA_MODIFIER_PROTECTED_ShouldBeProtected() {
        assertEquals("protected", AnalyzeConstants.JAVA.MODIFIER_PROTECTED);
    }

    @Test
    void JAVA_MODIFIER_PRIVATE_ShouldBePrivate() {
        assertEquals("private", AnalyzeConstants.JAVA.MODIFIER_PRIVATE);
    }

    @Test
    void JAVA_MODIFIER_STATIC_ShouldBeStatic() {
        assertEquals("static", AnalyzeConstants.JAVA.MODIFIER_STATIC);
    }

    @Test
    void JAVA_MODIFIER_FINAL_ShouldBeFinal() {
        assertEquals("final", AnalyzeConstants.JAVA.MODIFIER_FINAL);
    }

    @Test
    void JAVA_MODIFIER_NATIVE_ShouldBeNative() {
        assertEquals("native", AnalyzeConstants.JAVA.MODIFIER_NATIVE);
    }

    @Test
    void JAVA_MODIFIER_DEFAULT_ShouldBeDefault() {
        assertEquals("default", AnalyzeConstants.JAVA.MODIFIER_DEFAULT);
    }
}
