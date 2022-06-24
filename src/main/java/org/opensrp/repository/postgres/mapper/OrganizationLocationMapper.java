package org.opensrp.repository.postgres.mapper;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.OrganizationLocation;
import org.opensrp.domain.postgres.OrganizationLocationExample;

import java.util.List;

public interface OrganizationLocationMapper {

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    long countByExample(OrganizationLocationExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    int deleteByExample(OrganizationLocationExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    int insert(OrganizationLocation record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    int insertSelective(OrganizationLocation record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    List<OrganizationLocation> selectByExample(OrganizationLocationExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    OrganizationLocation selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    int updateByExampleSelective(@Param("record") OrganizationLocation record,
                                 @Param("example") OrganizationLocationExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    int updateByExample(@Param("record") OrganizationLocation record, @Param("example") OrganizationLocationExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    int updateByPrimaryKeySelective(OrganizationLocation record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table team.organization_location
     *
     * @mbg.generated Fri Sep 25 10:18:05 EAT 2020
     */
    int updateByPrimaryKey(OrganizationLocation record);
}