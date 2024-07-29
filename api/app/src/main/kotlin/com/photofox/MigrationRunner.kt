package com.photofox

import org.flywaydb.core.Flyway
import javax.sql.DataSource

object MigrationRunner {
    private val logger = logger<MigrationRunner>()

    fun run(dataSource: DataSource) {
        logger.info("running migrations...")

        val flyway = Flyway.configure()
            .dataSource(dataSource)
            .baselineOnMigrate(true)
            .validateMigrationNaming(true)
            .load()

        flyway.migrate()

        logger.info("done")
    }
}
