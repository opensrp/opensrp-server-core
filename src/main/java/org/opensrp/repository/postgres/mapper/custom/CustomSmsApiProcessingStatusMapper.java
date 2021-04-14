package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.SmsApiProcessingStatus;
import org.opensrp.domain.postgres.SmsApiProcessingStatusExample;
import org.opensrp.repository.postgres.mapper.SmsApiProcessingStatusMapper;

import java.util.List;

public interface CustomSmsApiProcessingStatusMapper extends SmsApiProcessingStatusMapper {

	List<SmsApiProcessingStatus> selectMany(@Param("example")SmsApiProcessingStatusExample smsApiProcessingStatusExample,
			@Param("offset") int offset, @Param("limit") int limit);

	SmsApiProcessingStatus selectByRequestId(String requestId);
}
