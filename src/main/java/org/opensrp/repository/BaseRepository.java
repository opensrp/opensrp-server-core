package org.opensrp.repository;

import java.util.List;

public interface BaseRepository<T> {
	
	/**
	 * Get the object using identifier
	 * @param id identifier
	 * @return entity
	 */
	T get(String id);
	
	/**
	 * Add entity to the database
	 * @param entity to add
	 */
	void add(T entity);
	
	/**
	 * Update entity 
	 * @param entity to update
	 */
	void update(T entity);
	
	/**
	 * Get's all entities on the database. By default 1000 entities will be returned
	 * @return
	 */
	List<T> getAll();
	
	/**
	 * Remove an object, can be actual deletion or setting a date deleted for events, clients, Plans, Organization, Practitioner 
	 * @param entity
	 */
	void safeRemove(T entity);
	
	/**
	 * Gets the next server version from the sequence configured for the table
	 * @return
	 */
	public long getNextServerVersion();
	
}
