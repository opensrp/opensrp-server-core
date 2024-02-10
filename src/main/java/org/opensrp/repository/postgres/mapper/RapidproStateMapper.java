package org.opensrp.repository.postgres.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.RapidproState;
import org.opensrp.domain.postgres.RapidproStateExample;

public interface RapidproStateMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    long countByExample(RapidproStateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    int deleteByExample(RapidproStateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    int insert(RapidproState row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    int insertSelective(RapidproState row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    List<RapidproState> selectByExample(RapidproStateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    RapidproState selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    int updateByExampleSelective(@Param("row") RapidproState row, @Param("example") RapidproStateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    int updateByExample(@Param("row") RapidproState row, @Param("example") RapidproStateExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    int updateByPrimaryKeySelective(RapidproState row);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.rapidpro_state
     *
     * @mbg.generated Fri Jul 16 21:40:27 EAT 2021
     */
    int updateByPrimaryKey(RapidproState row);
}