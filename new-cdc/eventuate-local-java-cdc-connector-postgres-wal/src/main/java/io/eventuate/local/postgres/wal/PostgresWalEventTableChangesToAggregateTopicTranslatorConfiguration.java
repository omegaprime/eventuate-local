package io.eventuate.local.postgres.wal;

import io.eventuate.local.common.EventuateConfigurationProperties;
import io.eventuate.local.common.PublishedEvent;
import io.eventuate.local.db.log.common.CommonReplicationEventTableChangesToAggregateTopicTranslatorConfiguration;
import io.eventuate.local.db.log.common.DbLogClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Import({CommonReplicationEventTableChangesToAggregateTopicTranslatorConfiguration.class})
@Profile("PostgresWal")
public class PostgresWalEventTableChangesToAggregateTopicTranslatorConfiguration {

  @Bean
  public DbLogClient<PublishedEvent> dbLogClient(@Value("${spring.datasource.url}") String dbUrl,
                                                 @Value("${spring.datasource.username}") String dbUserName,
                                                 @Value("${spring.datasource.password}") String dbPassword,
                                                 EventuateConfigurationProperties eventuateConfigurationProperties,
                                                 PostgresWalMessageParser<PublishedEvent> postgresWalMessageParser) {

    return new PostgresWalClient<>(postgresWalMessageParser,
            dbUrl,
            dbUserName,
            dbPassword,
            eventuateConfigurationProperties.getBinlogConnectionTimeoutInMilliseconds(),
            eventuateConfigurationProperties.getMaxAttemptsForBinlogConnection(),
            eventuateConfigurationProperties.getPostgresWalIntervalInMilliseconds(),
            eventuateConfigurationProperties.getPostgresReplicationStatusIntervalInMilliseconds(),
            eventuateConfigurationProperties.getPostgresReplicationSlotName());
  }

  @Bean
  public PostgresWalMessageParser<PublishedEvent> postgresReplicationMessageParser() {
    return new PostgresWalJsonMessageParser();
  }
}
