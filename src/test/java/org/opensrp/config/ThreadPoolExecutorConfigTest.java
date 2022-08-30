package org.opensrp.config;

import org.junit.Before;
import org.junit.Test;
import org.powermock.reflect.internal.WhiteboxImpl;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ThreadPoolExecutorConfigTest {

    private ThreadPoolExecutorConfig poolExecutorConfig;

    @Before
    public void setUp() {
        poolExecutorConfig = new ThreadPoolExecutorConfig();
    }

    @Test
    public void testTaskExecutorShouldReturnTaskExecutor() {
        int threadPoolCoreSize = 3;
        int threadPoolMaxSize = 4;
        WhiteboxImpl.setInternalState(poolExecutorConfig, "threadPoolCoreSize", threadPoolCoreSize);
        WhiteboxImpl.setInternalState(poolExecutorConfig, "threadPoolMaxSize", threadPoolMaxSize);

        ThreadPoolTaskExecutor taskExecutor = poolExecutorConfig.taskExecutor();
        assertNotNull(taskExecutor);
        assertEquals(threadPoolCoreSize, taskExecutor.getCorePoolSize());
        assertEquals(threadPoolMaxSize, taskExecutor.getMaxPoolSize());
    }
}
