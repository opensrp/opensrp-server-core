package org.opensrp;

import org.junit.runner.RunWith;
import org.opensrp.util.TestResourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-opensrp.xml")
public class SpringApplicationContextProvider extends TestResourceLoader {

    public static ApplicationContext context;

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        SpringApplicationContextProvider.context = context;
    }

}
