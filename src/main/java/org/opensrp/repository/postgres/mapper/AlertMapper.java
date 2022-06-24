package org.opensrp.repository.postgres.mapper;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Alert;
import org.opensrp.domain.postgres.AlertExample;

import java.util.List;

public interface AlertMapper {

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    long countByExample(AlertExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    int deleteByExample(AlertExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    int insert(Alert record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    int insertSelective(Alert record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    List<Alert> selectByExample(AlertExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    Alert selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    int updateByExampleSelective(@Param("record") Alert record, @Param("example") AlertExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    int updateByExample(@Param("record") Alert record, @Param("example") AlertExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    int updateByPrimaryKeySelective(Alert record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.alert
     *
     * @mbg.generated Thu Mar 22 17:10:48 EAT 2018
     */
    int updateByPrimaryKey(Alert record);
}
