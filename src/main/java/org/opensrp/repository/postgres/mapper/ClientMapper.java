package org.opensrp.repository.postgres.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Client;
import org.opensrp.domain.postgres.ClientExample;

public interface ClientMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	long countByExample(ClientExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int deleteByExample(ClientExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int deleteByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int insert(Client record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int insertSelective(Client record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	List<Client> selectByExample(ClientExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	Client selectByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int updateByExampleSelective(@Param("record") Client record, @Param("example") ClientExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int updateByExample(@Param("record") Client record, @Param("example") ClientExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int updateByPrimaryKeySelective(Client record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.client
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int updateByPrimaryKey(Client record);
}
