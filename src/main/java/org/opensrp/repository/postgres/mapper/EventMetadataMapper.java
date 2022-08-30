package org.opensrp.repository.postgres.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.EventMetadata;
import org.opensrp.domain.postgres.EventMetadataExample;

public interface EventMetadataMapper {

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    long countByExample(EventMetadataExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    int deleteByExample(EventMetadataExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    int insert(EventMetadata record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    int insertSelective(EventMetadata record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    List<EventMetadata> selectByExample(EventMetadataExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    EventMetadata selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    int updateByExampleSelective(@Param("record") EventMetadata record, @Param("example") EventMetadataExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    int updateByExample(@Param("record") EventMetadata record, @Param("example") EventMetadataExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    int updateByPrimaryKeySelective(EventMetadata record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.event_metadata
     *
     * @mbg.generated Fri Apr 06 12:20:41 EAT 2018
     */
    int updateByPrimaryKey(EventMetadata record);
}
