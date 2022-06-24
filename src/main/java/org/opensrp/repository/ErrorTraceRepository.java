package org.opensrp.repository;

import org.opensrp.domain.ErrorTrace;

import java.util.List;

public interface ErrorTraceRepository extends BaseRepository<ErrorTrace> {

    ErrorTrace findById(String _id);

    boolean exists(String id);

    List<ErrorTrace> findAllErrors();

    List<ErrorTrace> findAllUnSolvedErrors();

    List<ErrorTrace> findAllSolvedErrors();

}
