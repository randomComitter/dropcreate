# dropcreate
Example how to reproduce issue with database initialization in Spring Boot 3.4.3.

Related bug report: https://github.com/spring-projects/spring-boot/issues/44516

This project has 2 databases: 1 embedded (H2) and 1 external (MySQL).  
There is no explicit configuration for `spring.jpa.hibernate.ddl-auto` in the application.properties.  
When this project is using Spring Boot 3.3.5, embedded and external databases are not initialized.  
The problem with the Spring Boot 3.4.3 is that after updating to it from 3.3.5, the default `ddl-auto` `create-drop` will be applied to the non-embedded database.  
This results in the data loss in the external database.

