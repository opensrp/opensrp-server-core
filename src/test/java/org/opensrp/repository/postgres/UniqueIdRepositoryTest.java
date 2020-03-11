package org.opensrp.repository.postgres;

import java.util.HashSet;
import java.util.Set;
import org.opensrp.domain.UniqueId;
import org.opensrp.repository.UniqueIdPostgresRepository;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class UniqueIdRepositoryTest extends BaseRepositoryTest {

    @Autowired
    UniqueIdPostgresRepository uniqueIdRepository;

    @Override
    protected Set<String> getDatabaseScripts() {
        Set<String> scripts = new HashSet<String>();
        scripts.add("unique_id.sql");
        return scripts;
    }

    public void testGet(){
        UniqueId uniqueId = uniqueIdRepository.get("12345-11");
        assertNotNull(uniqueId);
        assertEquals("Akros_1", uniqueId.getLocation());
        assertEquals("not_used", uniqueId.getStatus());
        assertEquals("date", uniqueId.getCreatedAt().toString());
    }
}
