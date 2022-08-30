package org.opensrp.repository.postgres.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Manifest;
import org.opensrp.domain.postgres.ManifestExample;

public interface ManifestMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    long countByExample(ManifestExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    int deleteByExample(ManifestExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    int insert(Manifest record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    int insertSelective(Manifest record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    List<Manifest> selectByExample(ManifestExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    Manifest selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    int updateByExampleSelective(@Param("record") Manifest record, @Param("example") ManifestExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    int updateByExample(@Param("record") Manifest record, @Param("example") ManifestExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    int updateByPrimaryKeySelective(Manifest record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table core.manifest
     *
     * @mbg.generated Wed Apr 01 18:37:24 EAT 2020
     */
    int updateByPrimaryKey(Manifest record);
}