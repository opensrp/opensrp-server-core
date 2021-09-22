package org.opensrp.repository.postgres.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Template;
import org.opensrp.domain.postgres.TemplateExample;

public interface TemplateMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    long countByExample(TemplateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    int deleteByExample(TemplateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    int insert(Template record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    int insertSelective(Template record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    List<Template> selectByExample(TemplateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    Template selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    int updateByExampleSelective(@Param("record") Template record, @Param("example") TemplateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    int updateByExample(@Param("record") Template record, @Param("example") TemplateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    int updateByPrimaryKeySelective(Template record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.template
     *
     * @mbg.generated Mon Sep 06 08:33:43 EAT 2021
     */
    int updateByPrimaryKey(Template record);
}