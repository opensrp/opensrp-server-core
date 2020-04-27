package org.opensrp.repository.postgres.mapper.custom;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Settings;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.domain.postgres.SettingsMetadataExample;
import org.opensrp.repository.postgres.mapper.SettingsMetadataMapper;

public interface CustomSettingMetadataMapper extends SettingsMetadataMapper {
	
	List<SettingsAndSettingsMetadataJoined> selectMany(@Param("example") SettingsMetadataExample settingsExample, @Param("offset") int offset,
	                          @Param("limit") int limit);

	SettingsAndSettingsMetadataJoined selectByDocumentId(String documentId);

	int insertMany(List<SettingsMetadata> settingsMetadata);

	int updateMany(List<SettingsMetadata> settingsMetadata);
}

