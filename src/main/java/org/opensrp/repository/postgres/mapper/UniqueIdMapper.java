package org.opensrp.repository.postgres.mapper;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.UniqueId;
import org.opensrp.domain.postgres.UniqueIdExample;

import java.util.List;

public interface UniqueIdMapper {

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    long countByExample(UniqueIdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    int deleteByExample(UniqueIdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    int insert(UniqueId record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    int insertSelective(UniqueId record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    List<UniqueId> selectByExample(UniqueIdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    UniqueId selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    int updateByExampleSelective(@Param("record") UniqueId record, @Param("example") UniqueIdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    int updateByExample(@Param("record") UniqueId record, @Param("example") UniqueIdExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    int updateByPrimaryKeySelective(UniqueId record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.unique_id
     *
     * @mbg.generated Tue Mar 10 15:45:35 EAT 2020
     */
    int updateByPrimaryKey(UniqueId record);
}
