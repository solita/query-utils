package fi.solita.utils.query;

import static fi.solita.utils.functional.Option.Some;

import java.io.Serializable;

import fi.solita.utils.functional.Option;


/**
 * Indexing is zero-based
 *
 * @author Jyri-Matti Lähteenmäki / Solita Oy
 *
 */
public final class Page implements Serializable {
    public static final int DEFAULT_PAGE_SIZE = 10;

    private final int offset;
    private final int pageNumber;
    private final int pageSize;
    private final Option<Integer> fetchSizeHint;

    public static final Page FIRST = Page.of(0);

    public static final Page NoPaging = FIRST.withSize(Integer.MAX_VALUE);
    
    public static final Page SINGLE_ROW = Page.FIRST.withSize(1);
    
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
        Page ret = new Page(pageNumber, pageSize, Option.<Integer>None(), 0);
        return ret.equals(NoPaging) ? NoPaging : ret;
    }

    public Page withPageNumber(int pageNumber) {
        return of(pageNumber, pageSize);
    }

    public Page withSize(int pageSize) {
        return of(pageNumber, pageSize);
    }
    
    public Page withFetchSizeHint(int fetchSizeHint) {
        return new Page(pageNumber, pageSize, Some(fetchSizeHint), offset);
    }
    
    public Page withOffset(int offset) {
        return new Page(pageNumber, pageSize, fetchSizeHint, offset);
    }

    private Page(int pageNumber, int pageSize, Option<Integer> fetchSizeHint, int offset) {
        if (pageNumber < 0 || pageSize < 0 || offset < 0) {
            throw new IllegalArgumentException("arguments must be nonnegative");
        }
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.fetchSizeHint = fetchSizeHint;
        this.offset = offset;
    }

    public Page nextPage() {
        return new Page(pageNumber + 1, pageSize, fetchSizeHint, offset);
    }

    public int getFirstResult() {
        return offset + pageNumber * pageSize;
    }

    public int getMaxResults() {
        return pageSize;
    }
    
    public Option<Integer> getFetchSizeHint() {
        return fetchSizeHint;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((fetchSizeHint == null) ? 0 : fetchSizeHint.hashCode());
        result = prime * result + offset;
        result = prime * result + pageNumber;
        result = prime * result + pageSize;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Page other = (Page) obj;
        if (fetchSizeHint == null) {
            if (other.fetchSizeHint != null)
                return false;
        } else if (!fetchSizeHint.equals(other.fetchSizeHint))
            return false;
        if (offset != other.offset)
            return false;
        if (pageNumber != other.pageNumber)
            return false;
        if (pageSize != other.pageSize)
            return false;
        return true;
    }
}
