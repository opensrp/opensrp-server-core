package org.opensrp.repository.postgres;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.opensrp.domain.Campaign;
import org.opensrp.domain.ExecutionPeriod;
import org.opensrp.domain.Task;
import org.opensrp.repository.CampaignRepository;
import org.springframework.beans.factory.annotation.Autowired;

public class CampaignRepositoryTest extends BaseRepositoryTest {

	@Autowired
	private CampaignRepository campaignRepository;

	@Override
	protected Set<String> getDatabaseScripts() {
		Set<String> scripts = new HashSet<String>();
		scripts.add("campaign.sql");
		return scripts;
	}

	@Test
	public void testGet() {

		Campaign campaign = campaignRepository.get("IRS_2018_S1");
		assertEquals("2019 IRS Season 1", campaign.getTitle());
		assertEquals(
				"This is the 2010 IRS Spray Campaign for Zambia for the first spray season dated 1 Jan 2019 - 31 Mar 2019.",
				campaign.getDescription());
		assertEquals("2019 IRS Season 1", campaign.getTitle());
		assertEquals("2019-01-01", campaign.getExecutionPeriod().getStart().toString("yyyy-MM-dd"));
		assertEquals("2019-03-31", campaign.getExecutionPeriod().getEnd().toString("yyyy-MM-dd"));
		assertEquals(Task.TaskStatus.IN_PROGRESS, campaign.getStatus());
		assertEquals("2018-10-01T0900", campaign.getAuthoredOn().toString("yyyy-MM-dd'T'HHmm"));
		assertEquals("2018-11-12T1149", campaign.getLastModified().toString("yyyy-MM-dd'T'HHmm"));
		assertEquals("jdoe", campaign.getOwner());

	}

	@Test
	public void testSafeRemove() {
		assertEquals(2, campaignRepository.getAll().size());
		Campaign campaign = campaignRepository.get("IRS_2018_S1");
		campaignRepository.safeRemove(campaign);
		assertEquals(1, campaignRepository.getAll().size());
		assertNull(campaignRepository.get("IRS_2018_S1"));

	}

	@Test
	public void testAdd() {
		Campaign campaign = new Campaign();
		campaign.setIdentifier("ITN_2018_S1");
		campaign.setDescription("ITN for 2018 Season 1");
		ExecutionPeriod executionPeriod = new ExecutionPeriod();
		executionPeriod.setStart(new LocalDate("2018-01-01"));
		executionPeriod.setStart(new LocalDate("2018-03-31"));
		campaign.setExecutionPeriod(executionPeriod);
		campaign.setOwner("superAdmin");
		campaignRepository.add(campaign);
		assertEquals(3, campaignRepository.getAll().size());
		Campaign addedCampaign = campaignRepository.get("ITN_2018_S1");
		assertNotNull(addedCampaign);
		assertEquals("ITN for 2018 Season 1", addedCampaign.getDescription());

	}

	@Test
	public void testEdit() {
		Campaign campaign = campaignRepository.get("IRS_2018_S2");
		campaign.setDescription("Sprap season 2 2018");
		ExecutionPeriod executionPeriod = new ExecutionPeriod();
		executionPeriod.setStart(new LocalDate("2018-04-01"));
		executionPeriod.setEnd(new LocalDate("2018-06-30"));
		campaign.setExecutionPeriod(executionPeriod);
		campaignRepository.update(campaign);

		Campaign updatedCampaign = campaignRepository.get("IRS_2018_S2");
		assertEquals("Sprap season 2 2018", updatedCampaign.getDescription());
		assertEquals("2018-04-01", updatedCampaign.getExecutionPeriod().getStart().toString("yyyy-MM-dd"));
		assertEquals("2018-06-30", updatedCampaign.getExecutionPeriod().getEnd().toString("yyyy-MM-dd"));

	}

	@Test
	public void testGetCampaignsByServerVersion() {
		List<Campaign> campaigns = campaignRepository.getCampaignsByServerVersion(0);
		assertEquals(2, campaigns.size());

		assertEquals(2, campaignRepository.getCampaignsByServerVersion(1542012540782l).size());

		campaigns = campaignRepository.getCampaignsByServerVersion(1542012540783l);

		assertEquals(1, campaigns.size());

		assertEquals("IRS_2018_S2", campaigns.get(0).getIdentifier());

		assertTrue(campaignRepository.getCampaignsByServerVersion(System.currentTimeMillis()).isEmpty());

	}

}
