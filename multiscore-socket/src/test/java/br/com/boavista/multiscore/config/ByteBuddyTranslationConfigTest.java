package br.com.boavista.multiscore.config;

import nl.cornerstone.field.LenientRecordTranslator;
import nl.cornerstone.field.RecordTranslatorProvider;
import nl.cornerstone.programstructure.CodeUnitTranslatorProvider;
import nl.cornerstone.programstructure.LenientByteBuddyCodeUnitTranslator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

/**
 * Test configuration which exposes a {@link CodeUnitTranslatorProvider} and a
 * {@link RecordTranslatorProvider}, which are byte-buddy transforming classes at
 * runtime.
 * 
 * Note, that this is only used for testing. In production, the classes should
 * have been translated already at compile time!
 */
@Profile("test")
@Configuration
public class ByteBuddyTranslationConfigTest {

   /**
    * Exposes a {@link CodeUnitTranslatorProvider}. For testing purposes a
    * {@link LenientByteBuddyCodeUnitTranslator} is used.
    */
   @Primary
   @Bean
   public CodeUnitTranslatorProvider getCodeUnitTranslatorProviderTest() {
      return LenientByteBuddyCodeUnitTranslator.provider();
   }

   /**
    * Exposes a {@link RecordTranslatorProvider}. For testing purposes a
    * {@link LenientRecordTranslator} is used.
    */
   @Primary
   @Bean
   public RecordTranslatorProvider getRecordTranslatorProviderTest() {
      return LenientRecordTranslator.provider();
   }
}
