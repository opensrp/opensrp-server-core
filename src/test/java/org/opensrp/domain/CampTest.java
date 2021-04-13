package org.opensrp.domain;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;


public class CampTest {

	private Camp camp;

	@Before
	public void setUp() throws Exception {
		camp = new Camp();
	}

	@Test
	public void getProviderName() {
		String providerName = "provider-name";
		camp.setProviderName(providerName);

		Assert.assertEquals(providerName, camp.getProviderName());
	}

	@Test
	public void setProviderName() {
		String providerName = "provider-name";
		camp.setProviderName(providerName);

		Assert.assertEquals(providerName, camp.getProviderName());
	}

	@Test
	public void getDate() {
		String date = "date";
		camp.setDate(date);

		Assert.assertEquals(date, camp.getDate());
	}

	@Test
	public void setDate() {
		String date = "date";
		camp.setDate(date);

		Assert.assertEquals(date, camp.getDate());
	}

	@Test
	public void getCampName() {
		String campName = "Camp Moses";
		camp.setCampName(campName);

		Assert.assertEquals(campName, camp.getCampName());
	}

	@Test
	public void setCampName() {
		String campName = "Camp Moses";
		camp.setCampName(campName);

		Assert.assertEquals(campName, camp.getCampName());
	}

	@Test
	public void isStatus() {
		boolean status = true;
		camp.setStatus(status);

		Assert.assertEquals(status, camp.isStatus());
	}

	@Test
	public void setStatus() {
		boolean status = true;
		camp.setStatus(status);

		Assert.assertEquals(status, camp.isStatus());
	}

	@Test
	public void getTimestamp() {
		long timestamp = 92389L;
		camp.setTimestamp(timestamp);

		Assert.assertEquals(timestamp, camp.getTimestamp());
	}

	@Test
	public void setTimestamp() {
		long timestamp = 92389L;
		camp.setTimestamp(timestamp);

		Assert.assertEquals(timestamp, camp.getTimestamp());
	}

	@Test
	public void getCreatedBy() {
		String createdBy = "Abel Maina";
		camp.setCreatedBy(createdBy);

		Assert.assertEquals(createdBy, camp.getCreatedBy());
	}

	@Test
	public void setCreatedBy() {
		String createdBy = "Abel Maina";
		camp.setCreatedBy(createdBy);

		Assert.assertEquals(createdBy, camp.getCreatedBy());
	}

	@Test
	public void getCreatedDate() {
		Date createdDate = new Date();
		camp.setCreatedDate(createdDate);

		Assert.assertEquals(createdDate, camp.getCreatedDate());
	}

	@Test
	public void setCreatedDate() {
		Date createdDate = new Date();
		camp.setCreatedDate(createdDate);

		Assert.assertEquals(createdDate, camp.getCreatedDate());
	}

	@Test
	public void getCenterName() {
		String centerName = "CBIIC";
		camp.setCenterName(centerName);

		Assert.assertEquals(centerName, camp.getCenterName());
	}

	@Test
	public void setCenterName() {
		String centerName = "CBIIC";
		camp.setCenterName(centerName);

		Assert.assertEquals(centerName, camp.getCenterName());
	}
}
