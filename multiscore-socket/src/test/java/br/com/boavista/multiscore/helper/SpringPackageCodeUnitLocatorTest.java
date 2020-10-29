package br.com.boavista.multiscore.helper;

import nl.cornerstone.programstructure.methodimporter.CodeUnitLocator;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class SpringPackageCodeUnitLocatorTest {

    private static final String PROCEDURES_PACKAGE_NAME = "br.com.boavista";
    private static final String MODULES_PACKAGE_NAME = "nl.cornerstone.utils";

    private CodeUnitLocator procedureCodeUnitLocator = SpringPackageCodeUnitLocator.ofPackage(PROCEDURES_PACKAGE_NAME);
    private CodeUnitLocator modulesCodeUnitLocator = SpringPackageCodeUnitLocator.ofPackage(MODULES_PACKAGE_NAME);

    @Disabled
    @Test
    public void find() {
        final Stream proceduresStream = procedureCodeUnitLocator.find();
        assertNotNull(proceduresStream);

        final Stream modulesStream = modulesCodeUnitLocator.find();
        assertNotNull(modulesStream);
    }

    @Disabled
    @Test
    public void procedures() {
        final CodeUnitLocator procedures = procedureCodeUnitLocator.procedures();
        assertNotNull(procedures);
        assertTrue(procedures.find().anyMatch(procedure -> procedure.toString().contains(PROCEDURES_PACKAGE_NAME + ".Sci806")));
    }

    @Test
    public void modules() {
        final CodeUnitLocator modules = modulesCodeUnitLocator.modules();
        assertNotNull(modules);
        assertTrue(modules.find().anyMatch(module -> module.toString().contains(MODULES_PACKAGE_NAME + ".JobModule")));
    }

}
