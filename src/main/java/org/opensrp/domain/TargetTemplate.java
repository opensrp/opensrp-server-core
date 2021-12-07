package org.opensrp.domain;

import java.io.Serializable;
import java.util.AbstractMap;

public class TargetTemplate implements Serializable {
    private static final long serialVersionUID = 1L;
    private String measure;
    private TargetTemplate.Detail detail;
    private String due;

    public String getMeasure() {
        return this.measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getDue() {
        return this.due;
    }

    public void setDue(String due) {
        this.due = due;
    }

    public TargetTemplate.Detail getDetail() {
        return this.detail;
    }

    public void setDetail(TargetTemplate.Detail detail) {
        this.detail = detail;
    }

    static class DetailCodableConcept {
        private String text;

        public String getText() {
            return this.text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    static class MeasureRange implements Serializable {
        private TargetTemplate.Measure high;
        private TargetTemplate.Measure low;

        MeasureRange() {
        }

        public TargetTemplate.Measure getHigh() {
            return this.high;
        }

        public void setHigh(TargetTemplate.Measure high) {
            this.high = high;
        }

        public TargetTemplate.Measure getLow() {
            return this.low;
        }

        public void setLow(TargetTemplate.Measure low) {
            this.low = low;
        }
    }

    static class Measure implements Serializable {
        private float value;
        private String comparator;
        private String unit;

        Measure() {
        }

        public float getValue() {
            return this.value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public String getComparator() {
            return this.comparator;
        }

        public void setComparator(String comparator) {
            this.comparator = comparator;
        }

        public String getUnit() {
            return this.unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }

    static class Detail implements Serializable {
        private TargetTemplate.Measure detailQuantity;
        private TargetTemplate.MeasureRange detailRange;
        private AbstractMap.SimpleEntry<String, String> detailCodableConcept;

        public TargetTemplate.Measure getDetailQuantity() {
            return this.detailQuantity;
        }

        public void setDetailQuantity(TargetTemplate.Measure detailQuantity) {
            this.detailQuantity = detailQuantity;
        }

        public TargetTemplate.MeasureRange getDetailRange() {
            return this.detailRange;
        }

        public void setDetailRange(TargetTemplate.MeasureRange detailRange) {
            this.detailRange = detailRange;
        }

        public AbstractMap.SimpleEntry<String, String> getDetailCodableConcept() {
            return this.detailCodableConcept;
        }

        public void setDetailCodableConcept(AbstractMap.SimpleEntry<String, String> detailCodableConcept) {
            this.detailCodableConcept = detailCodableConcept;
        }
    }
}
