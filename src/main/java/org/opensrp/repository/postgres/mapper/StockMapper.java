package org.opensrp.repository.postgres.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.Stock;
import org.opensrp.domain.postgres.StockExample;

public interface StockMapper {

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	long countByExample(StockExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int deleteByExample(StockExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int deleteByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int insert(Stock record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int insertSelective(Stock record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	List<Stock> selectByExample(StockExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	Stock selectByPrimaryKey(Long id);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int updateByExampleSelective(@Param("record") Stock record, @Param("example") StockExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int updateByExample(@Param("record") Stock record, @Param("example") StockExample example);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int updateByPrimaryKeySelective(Stock record);

	/**
	 * This method was generated by MyBatis Generator. This method corresponds to the database table core.stock
	 * @mbg.generated  Wed Nov 25 14:17:23 EAT 2020
	 */
	int updateByPrimaryKey(Stock record);
}
