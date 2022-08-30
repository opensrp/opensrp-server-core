package org.opensrp.migrations;

import org.apache.ibatis.migration.options.DatabaseOperationOption;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(MockitoJUnitRunner.class)
public class MybatisMigrationTest {

    private MybatisMigration mybatisMigration;

    @Before
    public void setUp() {
        mybatisMigration = Mockito.spy(new MybatisMigration());
    }

    @Test
    public void testInitializeMybatisMigrationShouldInvokeRunMigrationOperation() throws IOException {
        Mockito.doNothing().when(mybatisMigration).runMigrationOperation(Mockito.any(Properties.class), Mockito.any(
                DatabaseOperationOption.class));

        mybatisMigration.initializeMybatisMigration();

        ArgumentCaptor<Properties> propertiesArgumentCaptor = ArgumentCaptor.forClass(Properties.class);
        ArgumentCaptor<DatabaseOperationOption> databaseOperationOptionArgumentCaptor = ArgumentCaptor.forClass(
                DatabaseOperationOption.class);

        Mockito.verify(mybatisMigration).runMigrationOperation(propertiesArgumentCaptor.capture(),
                databaseOperationOptionArgumentCaptor.capture());

        assertNotNull(databaseOperationOptionArgumentCaptor.getValue());
        assertNotNull(propertiesArgumentCaptor.getValue());

        // Has to be set to false
        assertFalse(databaseOperationOptionArgumentCaptor.getValue().isAutoCommit());
    }
}
