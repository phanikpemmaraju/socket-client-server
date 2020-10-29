package br.com.boavista.multiscore.config;

import nl.cornerstone.programstructure.CodeUnitTranslator;
import nl.cornerstone.programstructure.MethodImporterContext;
import nl.cornerstone.programstructure.methodimporter.MethodImporterProvider;
import nl.cornerstone.utils.facade.FacadeInterfaceScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration which is used for the {@link MethodImporterProvider} and
 * {@link FacadeInterfaceScanner} (see {@link MethodScannerConfig}).
 */
@Configuration
public class MethodImporterContextConfig {

   /**
    * Exposes the {@link MethodImporterContext} configuration bean, which does not
    * translate any code units.
    * 
    * @return the MethodImporterContext
    */
   @Bean
   public MethodImporterContext getMethodImporterContext() {
      return new MethodImporterContextImpl();
   }

   /**
    * A {@link MethodImporterContext} that provides an identity {@link CodeUnitTranslator}.
    */
   private class MethodImporterContextImpl implements MethodImporterContext {

      private CodeUnitTranslator codeUnitTranslator;
      
      private MethodImporterContextImpl() {
         codeUnitTranslator = CodeUnitTranslator.IdentityTranslator.INSTANCE;
      }
      
      @Override
      public CodeUnitTranslator getCodeUnitTranslator() {
         return codeUnitTranslator;
      }
   }
}
