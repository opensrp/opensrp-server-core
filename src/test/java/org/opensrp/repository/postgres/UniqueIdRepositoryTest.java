package org.opensrp.repository.postgres;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.opensrp.repository.UniqueIdRepository;
import org.opensrp.domain.UniqueId;
import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

public class UniqueIdRepositoryTest extends BaseRepositoryTest{

	@Autowired
	private UniqueIdRepository uniqueIdRepository;

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("unique_id.sql");
		return scripts;
	}

	@Test
	public void testFindByIdentifierSourceOrderByIdDesc() {
		UniqueId uniqueId = uniqueIdRepository.findByIdentifierSourceOrderByIdDesc(1l);
		assertEquals(2l,(long)uniqueId.getId());
	}

	@Test
	public void testFindReservedIdentifiers() {
		Set<String> reservedIdentifiers = uniqueIdRepository.findReservedIdentifiers();
		assertEquals(1,reservedIdentifiers.size());
	}

	@Test(expected = Test.None.class)
	public void testUpdate() {
    UniqueId uniqueId = new UniqueId();
    uniqueId.setId(2l);
    uniqueId.setOpenmrsId("krdai");
    uniqueId.setIdentifier("AAAA-1");
    uniqueId.setReserved(Boolean.TRUE);
    uniqueIdRepository.update(uniqueId);

	}
}
