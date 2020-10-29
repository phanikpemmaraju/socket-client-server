package br.com.boavista.multiscore.config;

import nl.cornerstone.extensionpoints.database.DatabaseConnectionProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * Test configurations that completely mocks the database and uses an in-memory database, by mocking the
 * {@link DatabaseConnectionProvider} of the {@link DbPreconnector}.
 * 
 */
@Profile("test")
@Configuration
public class DatasourceConfigTest {

   @Bean
   @Profile("test")
   public DataSource testDataSource() {
      DriverManagerDataSource dataSource = new DriverManagerDataSource();
      dataSource.setDriverClassName("org.h2.Driver");
      dataSource.setUrl("jdbc:h2:mem:db;DB_CLOSE_DELAY=-1");
      dataSource.setUsername("sa");
      dataSource.setPassword("sa");
      return dataSource;
   }

}
