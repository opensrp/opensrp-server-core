package org.opensrp.search;

import org.apache.commons.lang3.tuple.Pair;
import org.smartregister.domain.Client;
import org.smartregister.domain.Event;

import java.util.ArrayList;
import java.util.List;

public class UploadValidationBean {
    private Integer totalRows = 0;
    private Integer headerColumns = 0;
    private Integer rowsToUpdate = 0;
    private Integer rowsToCreate = 0;
    private List<String> errors;
    private List<Pair<Client, Event>> analyzedData;

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public Integer getHeaderColumns() {
        return headerColumns;
    }

    public void setHeaderColumns(Integer headerColumns) {
        this.headerColumns = headerColumns;
    }

    public Integer getRowsToUpdate() {
        return rowsToUpdate;
    }

    public void setRowsToUpdate(Integer rowsToUpdate) {
        this.rowsToUpdate = rowsToUpdate;
    }

    public Integer getRowsToCreate() {
        return rowsToCreate;
    }

    public void setRowsToCreate(Integer rowsToCreate) {
        this.rowsToCreate = rowsToCreate;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public void addError(String error) {
        if(this.errors == null)
            this.errors = new ArrayList<>();

        this.errors.add(error);
    }

    public List<Pair<Client, Event>> getAnalyzedData() {
        return analyzedData;
    }

    public void setAnalyzedData(List<Pair<Client, Event>> analyzedData) {
        this.analyzedData = analyzedData;
    }
}
