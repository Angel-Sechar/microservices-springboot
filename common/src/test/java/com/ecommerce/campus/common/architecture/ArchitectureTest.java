package com.ecommerce.campus.common.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

/**
 * Architecture tests to enforce hexagonal architecture principles.
 */
class ArchitectureTest {

    private JavaClasses classes;

    @BeforeEach
    void setUp() {
        classes = new ClassFileImporter().importPackages("com.ecommerce.campus.common");
    }

    @Test
    void domainLayerShouldNotDependOnOtherLayers() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..application..", "..infrastructure..", "..adapter..");

        rule.check(classes);
    }

    @Test
    void applicationLayerShouldNotDependOnInfrastructure() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..");

        rule.check(classes);
    }

    @Test
    void applicationLayerShouldNotDependOnAdapters() {
        ArchRule rule = noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..adapter..");

        rule.check(classes);
    }

    @Test
    void portsShouldBeInterfaces() {
        ArchRule rule = classes()
                .that().resideInAPackage("..port..")
                .should().beInterfaces();

        rule.check(classes);
    }

    @Test
    void layeredArchitectureShouldBeRespected() {
        ArchRule rule = layeredArchitecture()
                .consideringOnlyDependenciesInLayers()
                .layer("Domain").definedBy("..domain..")
                .layer("Application").definedBy("..application..")
                .layer("Infrastructure").definedBy("..infrastructure..")
                .layer("Adapter").definedBy("..adapter..")
                .whereLayer("Application").mayOnlyBeAccessedByLayers("Infrastructure", "Adapter")
                .whereLayer("Domain").mayOnlyBeAccessedByLayers("Application", "Infrastructure", "Adapter");

        rule.check(classes);
    }
}