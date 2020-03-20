package org.opensrp.repository;

import org.opensrp.domain.AppStateToken;

import java.util.List;

public interface AppStateTokensRepository extends BaseRepository<AppStateToken> {
	
	List<AppStateToken> findByName(String name);
	
	void update(AppStateToken entity);
	
	void add(AppStateToken entity);
	
}
