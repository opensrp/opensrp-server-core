package org.opensrp.repository.postgres.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.LocationTagMap;
import org.opensrp.domain.postgres.LocationTagMapExample;

public interface LocationTagMapMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.location_tag_map
     *
     * @mbggenerated Sun Mar 22 10:04:15 BDT 2020
     */
    int countByExample(LocationTagMapExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.location_tag_map
     *
     * @mbggenerated Sun Mar 22 10:04:15 BDT 2020
     */
    int deleteByExample(LocationTagMapExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.location_tag_map
     *
     * @mbggenerated Sun Mar 22 10:04:15 BDT 2020
     */
    int insert(LocationTagMap record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.location_tag_map
     *
     * @mbggenerated Sun Mar 22 10:04:15 BDT 2020
     */
    int insertSelective(LocationTagMap record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.location_tag_map
     *
     * @mbggenerated Sun Mar 22 10:04:15 BDT 2020
     */
    List<LocationTagMap> selectByExample(LocationTagMapExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.location_tag_map
     *
     * @mbggenerated Sun Mar 22 10:04:15 BDT 2020
     */
    int updateByExampleSelective(@Param("record") LocationTagMap record, @Param("example") LocationTagMapExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.location_tag_map
     *
     * @mbggenerated Sun Mar 22 10:04:15 BDT 2020
     */
    int updateByExample(@Param("record") LocationTagMap record, @Param("example") LocationTagMapExample example);
}