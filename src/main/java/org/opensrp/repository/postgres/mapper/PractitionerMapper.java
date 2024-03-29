package org.opensrp.repository.postgres.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Practitioner;
import org.opensrp.domain.postgres.PractitionerExample;

public interface PractitionerMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    long countByExample(PractitionerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    int deleteByExample(PractitionerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    int insert(Practitioner record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    int insertSelective(Practitioner record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    List<Practitioner> selectByExample(PractitionerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    Practitioner selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    int updateByExampleSelective(@Param("record") Practitioner record, @Param("example") PractitionerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    int updateByExample(@Param("record") Practitioner record, @Param("example") PractitionerExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    int updateByPrimaryKeySelective(Practitioner record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table team.practitioner
     *
     * @mbg.generated Mon Dec 06 14:53:20 EAT 2021
     */
    int updateByPrimaryKey(Practitioner record);
}