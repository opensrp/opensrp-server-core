package org.opensrp.service;

import org.opensrp.domain.Client;
import org.opensrp.repository.SearchRepository;
import org.opensrp.search.ClientSearchBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {
	
	private final SearchRepository search;
	
	@Autowired
	public SearchService(SearchRepository search) {
		this.search = search;
	}
	
	public List<Client> searchClient(ClientSearchBean clientSearchBean, String firstName, String middleName, String lastName,
	        Integer limit) {
		return search.findByCriteria(clientSearchBean, firstName, middleName, lastName, limit);
	}
	
}
