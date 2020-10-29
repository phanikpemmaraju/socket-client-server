package br.com.boavista.multiscore.config;

import br.com.boavista.multiscore.helper.*;
import br.com.boavista.techsupport.JobRunUtils;
import nl.cornerstone.programstructure.MethodImporterContext;
import nl.cornerstone.programstructure.Procedure;
import nl.cornerstone.programstructure.PrototypeInterface;
import nl.cornerstone.programstructure.methodimporter.CodeUnitLocator;
import nl.cornerstone.programstructure.methodimporter.MethodExportSelector;
import nl.cornerstone.programstructure.methodimporter.MethodImporter;
import nl.cornerstone.programstructure.methodimporter.MethodImporterProvider;
import nl.cornerstone.utils.facade.FacadeInterfaceScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Configuration, which exposes a {@link MethodImporterProvider} and
 * {@link FacadeInterfaceScanner} bean.
 * 
 */
@Configuration
public class MethodScannerConfig {

   /**
    * Exposes a {@link FacadeInterfaceScanner}.
    * 
    * @param methodImporterProvider the {@link MethodImporterProvider} that is used
    *                               to load the right {@link PrototypeInterface}
    *                               class.
    * 
    * @return the {@link FacadeInterfaceScanner}.
    */
   @Bean
   public FacadeInterfaceScanner getInterfaceScanner(MethodImporterProvider methodImporterProvider) {
      return new FacadeInterfaceScanner(methodImporterProvider.create(null));
   }

   /**
    * Exposes a {@link MethodImporterProvider}.
    * 
    * @param methodImporterContext the {@link MethodImporterContext} that defines
    *                              if classes have to be translated before being
    *                              loaded.
    *                          
    * Note: the parameters are automatically injected by Spring here.
    * 
    * @return the {@link MethodImporterProvider}.
    */
   @Bean
   public MethodImporterProvider getMethodImporterProvider(MethodImporterContext methodImporterContext) {
      return new MethodImporterProviderImpl(methodImporterContext);
   }

   /**
    * This implementation of {@link MethodImporterProvider} returns always the same
    * {@link MethodImporter} instance.
    */
   private static class MethodImporterProviderImpl implements MethodImporterProvider {

      private final MethodImporter methodImporter;

      public MethodImporterProviderImpl(final MethodImporterContext ctx) {
         methodImporter = JobRunUtils.newMethodResolverProvider(createProcedureLocator())
               .with(MethodExportSelector.first()).cachingWith(1, 10_000, 10, TimeUnit.MINUTES).create(ctx);
      }

      @Override
      public MethodImporter create(final MethodImporterContext ctx) {
         return methodImporter;
      }

      private static CodeUnitLocator<Procedure> createProcedureLocator() {
         return CodeUnitLocator
               .caching(SpringPackageCodeUnitLocator.ofPackage(JobRunUtils.REPLACEMENT_PACKAGE_NAME).procedures());
      }
   }
}
