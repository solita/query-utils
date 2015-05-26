package fi.solita.utils.query.backend;

import java.util.List;

import javax.persistence.LockModeType;
import javax.persistence.criteria.CriteriaQuery;

import fi.solita.utils.query.Page;

public interface JpaCriteriaQueryExecutor {
    <T> T get(CriteriaQuery<T> query, LockModeType lock);
    <T> List<T> getMany(CriteriaQuery<T> query, Page page, LockModeType lock);
}