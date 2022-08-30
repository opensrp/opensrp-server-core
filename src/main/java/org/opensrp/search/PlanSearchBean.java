package org.opensrp.search;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.smartregister.domain.PlanDefinition;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanSearchBean {

    private Integer pageNumber = 0;

    ;
    private Integer pageSize = 0;

    ;
    private OrderByType orderByType = OrderByType.DESC;
    private FieldName orderByFieldName = FieldName.id;
    private PlanDefinition.PlanStatus planStatus;
    private Map<String, String> useContexts;
    private boolean isExperimental;

    public enum OrderByType {
        ASC, DESC
    }

    public enum FieldName {
        id
    }

}
