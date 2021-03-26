package org.opensrp.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.Set;


public class StructureDetailsTest {

	private StructureDetails structureDetails;

	@Before
	public void setUp() throws Exception {
		structureDetails = Mockito.mock(StructureDetails.class, Mockito.CALLS_REAL_METHODS);//new StructureDetails("structure-id", "structure-parent-id", "structure");
	}

	@Test
	public void getStructureId() {
		String structureId = "structure-id";

		structureDetails.setStructureId(structureId);
		Assert.assertEquals(structureId, structureDetails.getStructureId());
	}

	@Test
	public void setStructureId() {
		String structureId = "structure-id";

		structureDetails.setStructureId(structureId);
		Assert.assertEquals(structureId, structureDetails.getStructureId());
	}

	@Test
	public void getStructureParentId() {
		String structureParentId = "structure-parent-id";

		structureDetails.setStructureParentId(structureParentId);
		Assert.assertEquals(structureParentId, structureDetails.getStructureParentId());
	}

	@Test
	public void setStructureParentId() {
		String structureParentId = "structure-parent-id";

		structureDetails.setStructureParentId(structureParentId);
		Assert.assertEquals(structureParentId, structureDetails.getStructureParentId());
	}

	@Test
	public void getStructureType() {
		String structureType = "multi-point";

		structureDetails.setStructureType(structureType);
		Assert.assertEquals(structureType, structureDetails.getStructureType());
	}

	@Test
	public void setStructureType() {
		String structureType = "multi-point";

		structureDetails.setStructureType(structureType);
		Assert.assertEquals(structureType, structureDetails.getStructureType());
	}

	@Test
	public void getFamilyId() {
		String familyId = "family-id";

		structureDetails.setFamilyId(familyId);
		Assert.assertEquals(familyId, structureDetails.getFamilyId());
	}

	@Test
	public void setFamilyId() {
		String familyId = "family-id";

		structureDetails.setFamilyId(familyId);
		Assert.assertEquals(familyId, structureDetails.getFamilyId());
	}

	@Test
	public void getFamilyMembers() {
		Set<String> familyMembers = new HashSet<>();
		familyMembers.add("son-junior");
		familyMembers.add("dad-abel");

		structureDetails.setFamilyMembers(familyMembers);
		Assert.assertEquals(familyMembers, structureDetails.getFamilyMembers());
	}

	@Test
	public void setFamilyMembers() {
		Set<String> familyMembers = new HashSet<>();
		familyMembers.add("son-junior");
		familyMembers.add("dad-abel");

		structureDetails.setFamilyMembers(familyMembers);
		Assert.assertEquals(familyMembers, structureDetails.getFamilyMembers());
	}

	@Test
	public void testEquals() {
		String structureId = "structure-id";

		StructureDetails structureDetails2 = new StructureDetails(structureId, "structure-parent-id", "structure");
		structureDetails = new StructureDetails(structureId, "structure-parent-id", "structure");
		
		Assert.assertEquals(structureDetails, structureDetails2);
	}
}
