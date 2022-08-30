package org.opensrp.service;

import java.util.List;

import org.joda.time.DateTime;
import org.opensrp.domain.ErrorTrace;
import org.opensrp.repository.ErrorTraceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author muhammad.ahmed@ihsinformatics.com Created on May 25, 2015
 */
@Service
public class ErrorTraceService {

    private final ErrorTraceRepository allErrorTrace;

    @Autowired
    public ErrorTraceService(ErrorTraceRepository allErrorTrace) {
        this.allErrorTrace = allErrorTrace;
    }

    /**
     * Saves logs to on database
     * This method has been disabled and logs are no longer saved on the database from 06/September/2018
     *
     * @param Error
     */
    public void addError(ErrorTrace entity) {
        //allErrorTrace.add(entity);
    }

    /**
     * @param errorType
     * @param documentType
     * @param recordId
     * @param stackTrace
     * @param retryURL     this method is used for logs and it should be called on Exception Catch .
     *                     retryURL should be given by developer, it is for resubmission or retry of that
     *                     particular record .
     */
    public void log(String errorType, String documentType, String recordId, String stackTrace, String retryURL) {
        ErrorTrace error = new ErrorTrace();
        error.setErrorType(errorType);
        error.setDocumentType(documentType);
        error.setRecordId(recordId);
        error.setStackTrace(stackTrace);
        error.setRetryUrl(retryURL);
        error.setDateOccurred(DateTime.now());
        addError(error);

    }

    /**
     * Update logs saved on database
     * This method has been disabled logs are no longer saved on the database from 06/September/2018
     *
     * @param Error
     */
    public void updateError(ErrorTrace entity) {
        //allErrorTrace.update(entity);
    }

    public List<ErrorTrace> getAllErrors() {

        List<ErrorTrace> allErrorList = allErrorTrace.findAllErrors();
        if (null == allErrorList || allErrorList.isEmpty()) {
            return null;

        }

        return allErrorList;

    }

    public List<ErrorTrace> getAllSolvedErrors() {

        List<ErrorTrace> allErrorList = allErrorTrace.findAllSolvedErrors();
        if (null == allErrorList || allErrorList.isEmpty()) {
            return null;

        }

        return allErrorList;

    }

    public List<ErrorTrace> getAllUnsolvedErrors() {

        List<ErrorTrace> allErrorList = allErrorTrace.findAllUnSolvedErrors();
        if (null == allErrorList || allErrorList.isEmpty()) {
            return null;

        }

        return allErrorList;

    }

    public ErrorTrace getError(String id) {

        return allErrorTrace.findById(id);

    }

}
