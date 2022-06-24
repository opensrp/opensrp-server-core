package org.opensrp.repository.postgres.mapper;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.ViewConfiguration;
import org.opensrp.domain.postgres.ViewConfigurationExample;

import java.util.List;

public interface ViewConfigurationMapper {

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    long countByExample(ViewConfigurationExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int deleteByExample(ViewConfigurationExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int insert(ViewConfiguration record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int insertSelective(ViewConfiguration record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    List<ViewConfiguration> selectByExample(ViewConfigurationExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    ViewConfiguration selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int updateByExampleSelective(@Param("record") ViewConfiguration record,
                                 @Param("example") ViewConfigurationExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int updateByExample(@Param("record") ViewConfiguration record, @Param("example") ViewConfigurationExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int updateByPrimaryKeySelective(ViewConfiguration record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table core.view_configuration
     *
     * @mbg.generated Wed Nov 25 14:17:23 EAT 2020
     */
    int updateByPrimaryKey(ViewConfiguration record);
}
