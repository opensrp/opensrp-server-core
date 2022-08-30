package org.opensrp.repository;

import java.util.List;

import org.opensrp.domain.ErrorTrace;

public interface ErrorTraceRepository extends BaseRepository<ErrorTrace> {

    ErrorTrace findById(String _id);

    boolean exists(String id);

    List<ErrorTrace> findAllErrors();

    List<ErrorTrace> findAllUnSolvedErrors();

    List<ErrorTrace> findAllSolvedErrors();

}
