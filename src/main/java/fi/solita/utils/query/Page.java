package fi.solita.utils.query;

import java.io.Serializable;


/**
 * Indexing is zero-based
 *
 * @author Jyri-Matti Lähteenmäki / Solita Oy
 *
 */
public final class Page implements Serializable {
    public static final int DEFAULT_PAGE_SIZE = 10;

    private final int pageNumber;
    private final int pageSize;

    public static final Page FIRST = Page.of(0);

    /**
     * @param pageNumber
     *            Zero-based
     */
    public static Page of(int pageNumber) {
        return of(pageNumber, DEFAULT_PAGE_SIZE);
    }

    /**
     * @param pageNumber
     *            Zero-based
     */
    public static Page of(int pageNumber, int pageSize) {
        return new Page(pageNumber, pageSize);
    }

    public Page withPageNumber(int pageNumber) {
        return of(pageNumber, pageSize);
    }

    public Page withSize(int pageSize) {
        return of(pageNumber, pageSize);
    }

    private Page(int pageNumber, int pageSize) {
        if (pageNumber < 0 || pageSize < 0) {
            throw new IllegalArgumentException("arguments must be nonnegative");
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public Page nextPage() {
        return new Page(pageNumber + 1, pageSize);
    }

    public int getFirstResult() {
        return pageNumber * pageSize;
    }

    public int getMaxResults() {
        return pageSize;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + pageNumber;
        result = prime * result + pageSize;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Page)) {
            return false;
        }
        Page other = (Page) obj;
        if (pageNumber != other.pageNumber) {
            return false;
        }
        if (pageSize != other.pageSize) {
            return false;
        }
        return true;
    }
}
