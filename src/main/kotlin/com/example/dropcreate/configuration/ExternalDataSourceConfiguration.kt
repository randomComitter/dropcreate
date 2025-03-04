package com.example.dropcreate.configuration

import com.zaxxer.hikari.HikariDataSource
import jakarta.persistence.EntityManagerFactory
import org.hibernate.boot.model.naming.ImplicitNamingStrategyLegacyJpaImpl
import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.JpaTransactionManager
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.transaction.PlatformTransactionManager
import javax.sql.DataSource

@Configuration
@EnableJpaRepositories(
    basePackages = ["com.example.dropcreate.externalentity"],
    entityManagerFactoryRef = "externalEntityManagerFactory",
    transactionManagerRef = "externalTransactionManager"
)
class ExternalDataSourceConfiguration {

    @Bean
    @ConfigurationProperties("external.datasource")
    fun externalDataSourceProperties(): DataSourceProperties {
        return DataSourceProperties()
    }

    @Bean
    fun externalServerDataSource(
        @Qualifier("externalDataSourceProperties") dataSourceProperties: DataSourceProperties
    ): HikariDataSource {
        return dataSourceProperties
            .initializeDataSourceBuilder()
            .type(HikariDataSource::class.java)
            .build().also {
                it.poolName = "externalDataSourcePool"
            }
    }

    @Bean
    fun externalEntityManagerFactory(
        builder: EntityManagerFactoryBuilder,
        @Qualifier("externalServerDataSource") dataSource: DataSource
    ): LocalContainerEntityManagerFactoryBean {
        return builder
            .dataSource(dataSource)
            .packages("com.example.dropcreate.externalentity")
            .persistenceUnit("externalPersistenceUnit")
            .properties(jpaProperties())
            .build()
    }

    private fun jpaProperties(): Map<String, String> {
        return mapOf(
            "hibernate.physical_naming_strategy" to PhysicalNamingStrategyStandardImpl::class.java.name,
            "hibernate.implicit_naming_strategy" to ImplicitNamingStrategyLegacyJpaImpl::class.java.name,
            // Without this with Spring Boot 3.4.3 the external database is initialized with "create-drop" if there is also embedded database
//            "hibernate.hbm2ddl.auto" to "none"
        )
    }

    @Bean
    fun externalTransactionManager(
        @Qualifier("externalEntityManagerFactory") entityManagerFactory: EntityManagerFactory
    ): PlatformTransactionManager {
        return JpaTransactionManager(entityManagerFactory)
    }
}
