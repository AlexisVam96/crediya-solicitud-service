package co.com.crediya.r2dbc.config;

import co.com.crediya.r2dbc.config.PostgreSQLConnectionPool;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

class PostgreSQLConnectionPoolTest {

    // TODO: change four you own tests
    @Test
    void getConnectionConfig() throws Exception {
        PostgreSQLConnectionPool pool = new PostgreSQLConnectionPool();

        // Set dbHost via reflection since it's private and normally injected by Spring
        Field dbHostField = PostgreSQLConnectionPool.class.getDeclaredField("dbHost");
        dbHostField.setAccessible(true);
        dbHostField.set(pool, "localhost");

        Assertions.assertNotNull(pool.getConnectionConfig());
    }
}
