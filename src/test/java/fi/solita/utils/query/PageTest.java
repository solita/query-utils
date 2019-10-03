package fi.solita.utils.query;

import static org.junit.Assert.*;

import org.junit.Test;

public class PageTest {

    @Test
    public void maxResults_equalWithDifferentOffset() {
        assertEquals(Page.of(42).getMaxResults(), Page.of(42).withOffset(10).getMaxResults());
    }
    
    @Test
    public void firstResult_increasedByOffset() {
        assertEquals(Page.of(42).getFirstResult()+10, Page.of(42).withOffset(10).getFirstResult());
    }
    
    @Test
    public void nextPageIncreasesByPageSize() {
        assertEquals(Page.of(42, 10).getFirstResult()+10, Page.of(42, 10).nextPage().getFirstResult());
    }
    
    @Test
    public void nextPageKeepsMaxResults() {
        assertEquals(Page.of(42, 10).getMaxResults(), Page.of(42, 10).nextPage().getMaxResults());
    }
}
