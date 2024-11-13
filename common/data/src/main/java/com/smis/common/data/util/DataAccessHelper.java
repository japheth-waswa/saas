package com.smis.common.data.util;

import com.smis.common.core.exception.DomainException;
import com.smis.common.core.exception.DuplicateRecord;
import com.smis.common.core.util.SortDirection;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;

import static com.smis.common.core.util.Helpers.DEFAULT_ERROR_MESSAGE;
import static com.smis.common.core.util.Helpers.ERROR_DUPLICATE_RECORD_MESSAGE;

@Slf4j
public class DataAccessHelper {
    private DataAccessHelper() {
    }

    public static Pageable buildPageable(int pageNumber, int pageSize) {
        return buildPageable(pageNumber, pageSize, new ArrayList<>());
    }

    public static Pageable buildPageable(int pageNumber, int pageSize, List<Sort.Order> orders) {
        if (!orders.isEmpty()) {
            return PageRequest.of(pageNumber, pageSize, Sort.by(orders));
        } else {
            return PageRequest.of(pageNumber, pageSize);
        }
    }

    public static Sort.Direction parseSortDirection(SortDirection sortDirection) {
        return sortDirection.equals(SortDirection.ASC) ? Sort.Direction.ASC : Sort.Direction.DESC;
    }

    public static void mapDataAccessErrorToDomainExceptionError(Exception exception, String duplicateRecordMessage) {
        log.error("An error occurred in data access layer", exception);
        if (exception instanceof DataIntegrityViolationException dataIntegrityViolationException) {
            if (dataIntegrityViolationException.getCause() instanceof ConstraintViolationException) {
                throw new DuplicateRecord(duplicateRecordMessage != null && !duplicateRecordMessage.isBlank() ?
                        duplicateRecordMessage : ERROR_DUPLICATE_RECORD_MESSAGE);
            }
        } else if (exception instanceof DomainException de) {
            throw new DomainException(de.getMessage());
        }
        throw new DomainException(DEFAULT_ERROR_MESSAGE);
    }
}
