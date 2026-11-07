package com.training.app;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

@AnalyzeClasses(packages = "com.training", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    @ArchTest
    static final ArchRule catalog_core_internals_must_not_leak =
            noClasses().that().resideInAnyPackage("..enrollments..", "..notifications..")
                    .should().dependOnClassesThat().resideInAPackage("..catalog.course..");

    @ArchTest
    static final ArchRule enrollments_core_internals_must_not_leak =
            noClasses().that().resideInAnyPackage("..catalog..", "..notifications..")
                    .should().dependOnClassesThat().resideInAPackage("..enrollments.enrollment..");

    @ArchTest
    static final ArchRule notifications_core_internals_must_not_leak =
            noClasses().that().resideInAnyPackage("..catalog..", "..enrollments..")
                    .should().dependOnClassesThat().resideInAPackage("..notifications.notification..")
                    .allowEmptyShould(true);

    @ArchTest
    static final ArchRule api_modules_must_be_pure_jdk =
            noClasses().that().resideInAPackage("..api..")
                    .should().dependOnClassesThat().resideInAnyPackage(
                            "org.springframework..",
                            "jakarta.persistence.."
                    );
}