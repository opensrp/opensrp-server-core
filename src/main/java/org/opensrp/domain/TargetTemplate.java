package org.opensrp.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.AbstractMap;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TargetTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    private String measure;
    private TargetTemplate.Detail detail;
    private String due;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class DetailCodableConcept {
        private String text;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class MeasureRange implements Serializable {
        private TargetTemplate.Measure high;
        private TargetTemplate.Measure low;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Measure implements Serializable {
        private float value;
        private String comparator;
        private String unit;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class Detail implements Serializable {
        private TargetTemplate.Measure detailQuantity;
        private TargetTemplate.MeasureRange detailRange;
        private AbstractMap.SimpleEntry<String, String> detailCodableConcept;

    }

}
