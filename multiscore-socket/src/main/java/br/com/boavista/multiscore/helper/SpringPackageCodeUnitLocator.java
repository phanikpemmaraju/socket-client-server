package br.com.boavista.multiscore.helper;

import nl.cornerstone.programstructure.CodeUnit;
import nl.cornerstone.programstructure.Module;
import nl.cornerstone.programstructure.Procedure;
import nl.cornerstone.programstructure.methodimporter.CodeUnitLocator;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.web.context.support.StandardServletEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;

public final class SpringPackageCodeUnitLocator implements CodeUnitLocator<CodeUnit> {
   private final String packageName;
   
   private SpringPackageCodeUnitLocator(String packageName) {
      this.packageName = checkNotNull(packageName);
   }
   
   @Override
   public Stream<Class<? extends CodeUnit>> find() {
      return SpringPackageCodeUnitLocator.find(CodeUnit.class, packageName);
   }
   
   public CodeUnitLocator<Module> modules() {
      return () ->
         SpringPackageCodeUnitLocator.find(Module.class, packageName);
   }
   
   public CodeUnitLocator<Procedure> procedures() {
      return () ->
         SpringPackageCodeUnitLocator.find(Procedure.class, packageName);
   }
   
   public static CodeUnitLocator<CodeUnit> ofPackage(String packageName) {
      return new SpringPackageCodeUnitLocator(packageName);
   }
   
   private static <T extends CodeUnit> Stream<Class<? extends T>> find(Class<T> type, String packageName) {
      ClassPathScanningCandidateComponentProvider compProvider
            = new ClassPathScanningCandidateComponentProvider(false, new StandardServletEnvironment());

      compProvider.addIncludeFilter(new AssignableTypeFilter(type));
      
      try {
         List<Class<? extends T>> classes = new ArrayList<>();
         for (BeanDefinition def : compProvider.findCandidateComponents(packageName)) {
            String className = def.getBeanClassName();
            Class<?> cls = Class.forName(className);
            classes.add(cls.asSubclass(type));
         }
         return classes.stream();
      } catch (ClassNotFoundException e) {
         throw new UnsupportedOperationException("Could not find expected class.", e);
      }
   }

}
