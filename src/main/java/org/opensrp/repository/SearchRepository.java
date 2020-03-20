package org.opensrp.repository;

import org.opensrp.domain.Client;
import org.opensrp.search.ClientSearchBean;

import java.util.List;

public interface SearchRepository {
	
	List<Client> findByCriteria(ClientSearchBean clientSearchBean, String firstName, String middleName, String lastName,
	        Integer limit);
}
