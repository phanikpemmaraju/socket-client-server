package br.com.boavista.multiscore.services;

import br.com.boavista.techsupport.JobRunUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.cornerstone.processor.annotations.Language;
import nl.cornerstone.programstructure.JobAutoClose;
import nl.cornerstone.programstructure.JobBuilder;
import nl.cornerstone.programstructure.Procedure;
import nl.cornerstone.programstructure.methodimporter.MethodExportSelector;
import nl.cornerstone.programstructure.methodimporter.MethodImport;
import nl.cornerstone.programstructure.methodimporter.MethodResolverProvider;
import nl.cornerstone.sql.DbConnectors;
import nl.cornerstone.utils.PropertyUtils;
import nl.cornerstone.utils.conversational.Conversation;
import nl.cornerstone.utils.facade.FacadeInterfaceScanner;
import nl.cornerstone.utils.request.ServiceTask;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
@Service
public class ProgramFactory {

  @Value("${language:cobol}")
  private String language;

  private final FacadeInterfaceScanner scanner;

  public Runnable getProgram(String userId, String programName, BlockingQueue<ServiceTask> requestQueue,
                             BlockingQueue<ServiceTask> responseQueue, Connection connection) {
    PropertyUtils.setLanguage(Language.of(language.toLowerCase()));
    final Conversation conversation = new Conversation(programName, requestQueue, responseQueue);
    conversation.setUserId(userId);
    return runProgram(conversation, connection);
  }

  private Runnable runProgram(Conversation conversation,Connection connection) {
    long startTime = System.nanoTime();
    final Class<? extends Procedure> procedure = scanner.getProcedure(conversation.getProgramName());
    log.info("procedure: {} ", procedure);
    final String pgmClassName = procedure.getSimpleName();

    return () -> {
      try {
        log.debug("START runJobProgram, clz={}", pgmClassName);
        final JobBuilder jobBuilder = JobRunUtils.newJobBuilder()
                .with(MethodResolverProvider.anyExplicitProcedure()
                        .orAnyExplicitMethod().with(MethodExportSelector.unique()))
                .with(DbConnectors.rememberExisting(DbConnectors.constant(connection)))
                .with(JobAutoClose.DATABASE);

        jobBuilder.runJobProgram(MethodImport.byProcedure(procedure), conversation);
        long estimatedTime = System.nanoTime() - startTime;
        log.info("Estimated time to start the job the request in ProgramFactory: "+ TimeUnit.MILLISECONDS.convert(estimatedTime, TimeUnit.NANOSECONDS));
        log.debug("STOP runJobProgram, clz={}", pgmClassName);
      } catch (Exception e) {
        e.printStackTrace();
        log.error("Cannot start " + conversation.getProgramName()
                + ", class " + pgmClassName + ": " + e, e);
      } finally{
        try{
          if(Objects.nonNull(connection)) {
            log.info("Closing connection from Program Factory");
            connection.close();
          }
        } catch (Exception ex){
          log.error("Cannot Close the database connection" );
        }
      }
    };
  }
}
