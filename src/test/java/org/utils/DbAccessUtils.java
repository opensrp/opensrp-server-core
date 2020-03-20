package org.utils;

import java.util.List;

import org.opensrp.domain.BaseDataEntity;
import org.opensrp.repository.BaseRepository;

public final class DbAccessUtils {
	

	private DbAccessUtils() {
		
	}
	
	public static <T extends BaseDataEntity, R extends BaseRepository<T>> void addObjectToRepository(List<T> objectList, R repository) {
		for (T object : objectList) {
			repository.add(object);
		}
	}
	
}
