package org.opensrp.repository.postgres.mapper.custom;

import org.apache.ibatis.annotations.Param;
import org.opensrp.domain.postgres.ProductCatalogue;
import org.opensrp.domain.postgres.ProductCatalogueExample;
import org.opensrp.repository.postgres.mapper.ProductCatalogueMapper;

import java.util.List;

public interface CustomProductCatalogueMapper extends ProductCatalogueMapper {

    ProductCatalogue selectOne(@Param("example") ProductCatalogueExample productCatalogueExample);

    List<ProductCatalogue> selectMany(@Param("example") ProductCatalogueExample productCatalogueExample, @Param("offset") int offset,
                                      @Param("limit") int limit);

    int insertSelectiveAndSetId(ProductCatalogue productCatalogue);

    Long selectServerVersionByPrimaryKey(Long uniqueId);

    int updateByPrimaryKeyAndGenerateServerVersion(ProductCatalogue productCatalogue);
}
