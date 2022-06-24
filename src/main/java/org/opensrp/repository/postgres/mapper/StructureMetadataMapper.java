package org.opensrp.repository.postgres.mapper;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.StructureMetadata;
import org.opensrp.domain.postgres.StructureMetadataExample;

import java.util.List;

public interface StructureMetadataMapper {

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    long countByExample(StructureMetadataExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    int deleteByExample(StructureMetadataExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    int insert(StructureMetadata record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    int insertSelective(StructureMetadata record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    List<StructureMetadata> selectByExample(StructureMetadataExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    StructureMetadata selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    int updateByExampleSelective(@Param("record") StructureMetadata record,
                                 @Param("example") StructureMetadataExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    int updateByExample(@Param("record") StructureMetadata record, @Param("example") StructureMetadataExample example);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    int updateByPrimaryKeySelective(StructureMetadata record);

    /**
     * This method was generated by MyBatis Generator. This method corresponds to the database table
     * core.structure_metadata
     *
     * @mbg.generated Wed Sep 30 15:22:34 EAT 2020
     */
    int updateByPrimaryKey(StructureMetadata record);
}
