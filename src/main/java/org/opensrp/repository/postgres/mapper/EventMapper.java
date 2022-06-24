package org.opensrp.repository.postgres.mapper;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Event;
import org.opensrp.domain.postgres.EventExample;

import java.util.List;

public interface EventMapper {

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    long countByExample(EventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int deleteByExample(EventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int insert(Event record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int insertSelective(Event record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    List<Event> selectByExample(EventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    Event selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int updateByExampleSelective(@Param("record") Event record, @Param("example") EventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int updateByExample(@Param("record") Event record, @Param("example") EventExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int updateByPrimaryKeySelective(Event record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.event
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int updateByPrimaryKey(Event record);
}
