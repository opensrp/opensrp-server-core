package org.opensrp.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opensrp.domain.Campaign;
import org.opensrp.repository.CampaignRepository;
import org.powermock.modules.junit4.PowerMockRunner;
import org.smartregister.domain.Period;
import org.smartregister.domain.Task.TaskStatus;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
public class CampaignServiceTest {

    private CampaignService campaignService;

    private CampaignRepository campaignRepository;

    @Before
    public void setUp() {
        campaignRepository = mock(CampaignRepository.class);
        campaignService = new CampaignService();
        campaignService.setCampaignRepository(campaignRepository);
    }

    @Test
    public void testGetAllCampaigns() {
        List<Campaign> expected = new ArrayList<>();
        Campaign campaign = new Campaign();
        campaign.setIdentifier("ITN_2018_S1");
        campaign.setDescription("ITN for 2018 Season 1");
        expected.add(campaign);
        when(campaignRepository.getAll()).thenReturn(expected);
        List<Campaign> campaigns = campaignService.getAllCampaigns();
        verify(campaignRepository).getAll();
        assertEquals(1, campaigns.size());
        assertEquals("ITN_2018_S1", campaigns.get(0).getIdentifier());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testaAddOrUpdateCampaignWithoutIdentifier() {
        Campaign campaign = new Campaign();
        campaignService.addOrUpdateCampaign(campaign);
    }

    @Test
    public void testExistingCampaignAddOrUpdateCampaignShouldUpdate() {
        Campaign campaign = getCampaign();
        when(campaignRepository.get("ITN_2018_S1")).thenReturn(campaign);
        campaignService.addOrUpdateCampaign(campaign);
        verify(campaignRepository).update(campaign);

    }

    @Test
    public void testNonExistingCampaignAddOrUpdateCampaignShouldAdd() {
        Campaign campaign = getCampaign();
        when(campaignRepository.get("ITN_2018_S1")).thenReturn(null);
        campaignService.addOrUpdateCampaign(campaign);
        verify(campaignRepository).add(campaign);

    }

    @Test
    public void testaAddCampaign() {
        Campaign campaign = getCampaign();
        campaignService.addCampaign(campaign);
        verify(campaignRepository).add(campaign);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddCampaignWithoutIdentifier() {
        Campaign campaign = new Campaign();
        campaignService.addCampaign(campaign);
    }

    @Test
    public void testUpdateCampaign() {
        Campaign campaign = getCampaign();
        campaignService.updateCampaign(campaign);
        verify(campaignRepository).update(campaign);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testUpdateCampaignWithoutIdentifier() {
        Campaign campaign = new Campaign();
        campaignService.updateCampaign(campaign);

    }

    @Test
    public void testGetCampaign() {
        when(campaignRepository.get("ITN_2018_S1")).thenReturn(getCampaign());
        Campaign campaign = campaignService.getCampaign("ITN_2018_S1");
        verify(campaignRepository).get("ITN_2018_S1");
        assertEquals("ITN for 2018 Season 1", campaign.getDescription());
        assertEquals(TaskStatus.READY, campaign.getStatus());
        assertEquals(1542115657234l, campaign.getServerVersion());

    }

    @Test
    public void testGetCampaignWithNoIdentifier() {
        Campaign campaign = campaignService.getCampaign("");
        verify(campaignRepository, never()).get(anyString());
        assertNull(campaign);

    }

    @Test
    public void testGetCampaigns() {
        List<Campaign> expected = new ArrayList<>();
        expected.add(getCampaign());
        when(campaignRepository.getCampaignsByServerVersion(1542115657234l)).thenReturn(expected);
        List<Campaign> campaigns = campaignService.getCampaignsByServerVersion(1542115657234l);
        verify(campaignRepository).getCampaignsByServerVersion(1542115657234l);
        assertEquals(1, campaigns.size());
        assertEquals("ITN_2018_S1", campaigns.get(0).getIdentifier());
        assertEquals("ITN for 2018 Season 1", campaigns.get(0).getDescription());
        assertEquals(TaskStatus.READY, campaigns.get(0).getStatus());
        assertEquals(1542115657234l, campaigns.get(0).getServerVersion());

    }

    private Campaign getCampaign() {
        Campaign campaign = new Campaign();
        campaign.setIdentifier("ITN_2018_S1");
        campaign.setDescription("ITN for 2018 Season 1");
        campaign.setStatus(TaskStatus.READY);
        Period executionPeriod = new Period();
        executionPeriod.setStart(new LocalDate("2018-01-01").toDateTimeAtStartOfDay());
        executionPeriod.setEnd(new LocalDate("2018-03-31").toDateTimeAtStartOfDay());
        campaign.setExecutionPeriod(executionPeriod);
        campaign.setOwner("superAdmin23");
        campaign.setServerVersion(1542115657234l);
        return campaign;

    }
}
