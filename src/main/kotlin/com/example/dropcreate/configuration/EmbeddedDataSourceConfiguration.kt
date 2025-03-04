package com.example.dropcreate.configuration

import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.boot.orm.jpa.hibernate.SpringImplicitNamingStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

// If you remove this configuration, then external database will not be initialized with "create-drop" with Spring Boot 3.4.3
@Configuration
@EnableJpaRepositories(
    basePackages = [
        "com.example.dropcreate.embeddedentity",
    ],
    entityManagerFactoryRef = "embeddedEntityManagerFactory",
    transactionManagerRef = "embeddedTransactionManager"
)
class EmbeddedDataSourceConfiguration {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    fun embeddedDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Primary
    @Bean
    fun embeddedDataSource(dataSourceProperties: DataSourceProperties): DataSource {
        return dataSourceProperties
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build().also {
                it.poolName = "embeddedDataSourcePool"
            }
    }

    @Primary
    @Bean
    fun embeddedEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        embeddedDataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(embeddedDataSource)
            .packages("com.example.dropcreate.embeddedentity")
            .persistenceUnit("embeddedPersistenceUnit")
            .properties(jpaProperties())
            .build()
    }

    private fun jpaProperties(): Map<String, String> {
        return mapOf(
            "hibernate.physical_naming_strategy" to CamelCaseToUnderscoresNamingStrategy::class.java.name,
            "hibernate.implicit_naming_strategy" to SpringImplicitNamingStrategy::class.java.name
        )
    }

    @Primary
    @Bean
    fun embeddedTransactionManager(embeddedEntityManagerFactory: EntityManagerFactory): PlatformTransactionManager {
        return JpaTransactionManager(embeddedEntityManagerFactory)
    }
}
