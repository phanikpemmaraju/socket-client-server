package br.com.boavista.multiscore.config;

import nl.cornerstone.field.IdentityRecordTranslator;
import nl.cornerstone.field.RecordTranslatorProvider;
import nl.cornerstone.programstructure.CodeUnitTranslatorProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class which exposes a {@link CodeUnitTranslatorProvider}, a
 * {@link RecordTranslatorProvider}, which do not translate anything at runtime.
 * 
 * Classes should have been transformed already at compile time!
 * 
 * Note: a method that exposes a {@link Bean} will be only called once, namely
 * when the application gets initialized.
 */
@Configuration
public class ByteBuddyTranslationConfig {

   /**
    * Exposes a {@link CodeUnitTranslatorProvider}. An identity CodeUnitTranslator
    * does not translate anything at runtime.
    * 
    * @return the {@link CodeUnitTranslatorProvider}
    */
   @Bean
   public CodeUnitTranslatorProvider getCodeUnitTranslatorProvider() {
      return CodeUnitTranslatorProvider.IDENTITY;
   }

   /**
    * Exposes a {@link RecordTranslatorProvider}. An identity RecordTranslator does
    * not translate anything at runtime.
    * 
    * @return the {@link RecordTranslatorProvider}
    */
   @Bean
   public RecordTranslatorProvider getRecordTranslatorProvider() {
      return IdentityRecordTranslator.PROVIDER;
   }
}
