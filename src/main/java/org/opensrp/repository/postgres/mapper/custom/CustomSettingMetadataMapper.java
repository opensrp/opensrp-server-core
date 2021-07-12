package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.SettingsAndSettingsMetadataJoined;
import org.opensrp.domain.postgres.SettingsMetadata;
import org.opensrp.domain.postgres.SettingsMetadataExample;
import org.opensrp.repository.postgres.mapper.SettingsMetadataMapper;

import java.util.List;

public interface CustomSettingMetadataMapper extends SettingsMetadataMapper {

	List<SettingsAndSettingsMetadataJoined> selectMany(@Param("example") SettingsMetadataExample settingsExample,
			@Param("offset") int offset,
			@Param("limit") int limit);

	SettingsAndSettingsMetadataJoined selectByDocumentId(@Param("documentId") String documentId);

	int insertMany(@Param("settingsMetadata") List<SettingsMetadata> settingsMetadata);

	int updateMany(@Param("settingsMetadata") List<SettingsMetadata> settingsMetadata);

	List<SettingsMetadata> selectManySettingsMetadata(@Param("example") SettingsMetadataExample settingsExample,
													   @Param("offset") int offset,
													   @Param("limit") int limit);
}

