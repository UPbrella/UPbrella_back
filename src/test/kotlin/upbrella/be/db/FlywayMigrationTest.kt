package upbrella.be.db

import org.assertj.core.api.Assertions.assertThat
import org.flywaydb.core.Flyway
import org.flywaydb.core.api.MigrationType
import org.junit.jupiter.api.Assertions.assertAll
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean
import org.springframework.core.io.FileSystemResource
import java.sql.DriverManager
import java.util.Properties

// CI(dev-pr-test.yml)가 띄운 MySQL에서 db/migration을 실제로 적용해 본다. FLYWAY_TEST_DB_URL이 없으면 건너뛴다
@EnabledIfEnvironmentVariable(named = "FLYWAY_TEST_DB_URL", matches = ".+")
class FlywayMigrationTest {

    private val serverUrl = System.getenv("FLYWAY_TEST_DB_URL")
    private val username = System.getenv("FLYWAY_TEST_DB_USERNAME")
    private val password = System.getenv("FLYWAY_TEST_DB_PASSWORD")

    // 앱과 같은 설정(src/main/resources/application.yml의 spring.flyway)으로 돌린다
    private val appConfig: Properties = YamlPropertiesFactoryBean()
        .apply { setResources(FileSystemResource("src/main/resources/application.yml")) }
        .getObject()!!

    @Test
    @DisplayName("빈 DB에는 V1부터 모든 마이그레이션을 적용한다")
    fun migrateEmptyDatabase() {
        // given
        val url = recreateDatabase("flyway_empty")
        val pendingCount = flyway(url).info().pending().size

        // when
        val result = flyway(url).migrate()

        // then
        assertAll(
            { assertThat(result.migrationsExecuted).isEqualTo(pendingCount) },
            { assertThat(flyway(url).info().pending()).isEmpty() },
            { assertThat(tables(url)).containsAll(V1_TABLES) }
        )
    }

    @Test
    @DisplayName("Flyway 도입 전 운영 DB는 V1을 기준으로 삼아 건너뛰고 그 뒤 마이그레이션만 적용한다")
    fun migrateExistingDatabase() {
        // given: V1 스키마만 있고 Flyway 이력은 없는 도입 전 운영 DB
        val url = recreateDatabase("flyway_existing")
        flyway(url, target = "1").migrate()
        execute(url, "DROP TABLE flyway_schema_history")

        // when
        flyway(url).migrate()

        // then
        val applied = flyway(url).info().applied()
        assertAll(
            { assertThat(applied.first().type).isEqualTo(MigrationType.BASELINE) },
            { assertThat(applied.first().version.version).isEqualTo("1") },
            { assertThat(applied.filter { it.type == MigrationType.SQL }.map { it.version.version }).doesNotContain("1") },
            { assertThat(flyway(url).info().pending()).isEmpty() }
        )
    }

    private fun flyway(url: String, target: String? = null): Flyway {
        val configuration = Flyway.configure()
            .dataSource(url, username, password)
            .locations("classpath:db/migration")
            .baselineOnMigrate(appConfig.getProperty("spring.flyway.baseline-on-migrate").toBoolean())
            .baselineVersion(appConfig.getProperty("spring.flyway.baseline-version"))
            .validateMigrationNaming(appConfig.getProperty("spring.flyway.validate-migration-naming").toBoolean())
        target?.let { configuration.target(it) }
        return configuration.load()
    }

    private fun recreateDatabase(name: String): String {
        execute(
            "$serverUrl/$CONNECTION_OPTIONS",
            "DROP DATABASE IF EXISTS $name",
            "CREATE DATABASE $name DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci"
        )
        return "$serverUrl/$name$CONNECTION_OPTIONS"
    }

    private fun tables(url: String): List<String> {
        DriverManager.getConnection(url, username, password).use { connection ->
            connection.createStatement().use { statement ->
                val resultSet = statement.executeQuery(
                    "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()"
                )
                return generateSequence { if (resultSet.next()) resultSet.getString(1) else null }.toList()
            }
        }
    }

    private fun execute(url: String, vararg sqls: String) {
        DriverManager.getConnection(url, username, password).use { connection ->
            connection.createStatement().use { statement ->
                sqls.forEach { statement.execute(it) }
            }
        }
    }

    companion object {
        private const val CONNECTION_OPTIONS = "?allowPublicKeyRetrieval=true&useSSL=false"

        private val V1_TABLES = listOf(
            "black_list", "business_hour", "classification", "condition_report", "history", "improvement_report",
            "locker", "store_detail", "store_image", "store_meta", "umbrella", "user"
        )
    }
}
