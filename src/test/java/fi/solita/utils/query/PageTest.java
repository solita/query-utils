package fi.solita.utils.query;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

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
    
    // Factory methods tests
    @Test
    public void of_withPageNumber_usesDefaultPageSize() {
        Page page = Page.of(5);
        assertEquals(5, page.getPageNumber());
        assertEquals(Page.DEFAULT_PAGE_SIZE, page.getMaxResults());
    }
    
    @Test
    public void of_withPageNumberAndSize_createsPageCorrectly() {
        Page page = Page.of(3, 20);
        assertEquals(3, page.getPageNumber());
        assertEquals(20, page.getMaxResults());
    }
    
    @Test
    public void of_firstResult_calculatedFromPageNumberAndSize() {
        Page page = Page.of(2, 10);
        assertEquals(20, page.getFirstResult());
    }
    
    // Constants tests
    @Test
    public void FIRST_isPageZero() {
        assertEquals(0, Page.FIRST.getPageNumber());
        assertEquals(Page.DEFAULT_PAGE_SIZE, Page.FIRST.getMaxResults());
        assertEquals(0, Page.FIRST.getFirstResult());
    }
    
    @Test
    public void NoPaging_hasMaxIntSize() {
        assertEquals(Integer.MAX_VALUE, Page.NoPaging.getMaxResults());
        assertEquals(0, Page.NoPaging.getPageNumber());
    }
    
    @Test
    public void SINGLE_ROW_hasSizeOne() {
        assertEquals(1, Page.SINGLE_ROW.getMaxResults());
        assertEquals(0, Page.SINGLE_ROW.getPageNumber());
    }
    
    @Test
    public void SINGLE_ROW_hasFetchSizeHintOfTwo() {
        assertTrue(Page.SINGLE_ROW.getFetchSizeHint().isDefined());
        assertEquals(2, Page.SINGLE_ROW.getFetchSizeHint().get().intValue());
    }
    
    // withPageNumber tests
    @Test
    public void withPageNumber_createsNewPageWithUpdatedPageNumber() {
        Page original = Page.of(1, 15);
        Page updated = original.withPageNumber(3);
        
        assertEquals(3, updated.getPageNumber());
        assertEquals(15, updated.getMaxResults());
        assertEquals(1, original.getPageNumber()); // original unchanged
    }
    
    // withSize tests
    @Test
    public void withSize_createsNewPageWithUpdatedSize() {
        Page original = Page.of(2, 10);
        Page updated = original.withSize(25);
        
        assertEquals(2, updated.getPageNumber());
        assertEquals(25, updated.getMaxResults());
        assertEquals(10, original.getMaxResults()); // original unchanged
    }
    
    // withFetchSizeHint tests
    @Test
    public void withFetchSizeHint_setsFetchSizeHint() {
        Page page = Page.of(0).withFetchSizeHint(100);
        
        assertTrue(page.getFetchSizeHint().isDefined());
        assertEquals(100, page.getFetchSizeHint().get().intValue());
    }
    
    @Test
    public void withFetchSizeHint_preservesOtherProperties() {
        Page original = Page.of(3, 20);
        Page withHint = original.withFetchSizeHint(50);
        
        assertEquals(3, withHint.getPageNumber());
        assertEquals(20, withHint.getMaxResults());
    }
    
    @Test
    public void getFetchSizeHint_notDefinedByDefault() {
        Page page = Page.of(0);
        assertFalse(page.getFetchSizeHint().isDefined());
    }
    
    // Error cases tests
    @Test(expected = IllegalArgumentException.class)
    public void of_negativePageNumber_throwsException() {
        Page.of(-1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void of_negativePageSize_throwsException() {
        Page.of(0, -1);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void withOffset_negativeOffset_throwsException() {
        Page.of(0).withOffset(-5);
    }
    
    // equals and hashCode tests
    @Test
    public void equals_samePageNumberAndSize_areEqual() {
        Page page1 = Page.of(2, 15);
        Page page2 = Page.of(2, 15);
        
        assertEquals(page1, page2);
        assertEquals(page1.hashCode(), page2.hashCode());
    }
    
    @Test
    public void equals_differentPageNumber_notEqual() {
        Page page1 = Page.of(1, 15);
        Page page2 = Page.of(2, 15);
        
        assertNotEquals(page1, page2);
    }
    
    @Test
    public void equals_differentPageSize_notEqual() {
        Page page1 = Page.of(2, 10);
        Page page2 = Page.of(2, 15);
        
        assertNotEquals(page1, page2);
    }
    
    @Test
    public void equals_differentOffset_notEqual() {
        Page page1 = Page.of(2, 10);
        Page page2 = Page.of(2, 10).withOffset(5);
        
        assertNotEquals(page1, page2);
    }
    
    @Test
    public void equals_differentFetchSizeHint_notEqual() {
        Page page1 = Page.of(2, 10);
        Page page2 = Page.of(2, 10).withFetchSizeHint(50);
        
        assertNotEquals(page1, page2);
    }
    
    @Test
    public void equals_sameInstance_isEqual() {
        Page page = Page.of(2, 10);
        assertEquals(page, page);
    }
    
    @Test
    public void equals_null_notEqual() {
        Page page = Page.of(2, 10);
        assertNotEquals(page, null);
    }
    
    @Test
    public void equals_differentType_notEqual() {
        Page page = Page.of(2, 10);
        assertNotEquals(page, "not a page");
    }
    
    // getPageNumber tests
    @Test
    public void getPageNumber_returnsCorrectPageNumber() {
        assertEquals(0, Page.of(0).getPageNumber());
        assertEquals(5, Page.of(5).getPageNumber());
        assertEquals(100, Page.of(100).getPageNumber());
    }
    
    @Test
    public void nextPage_incrementsPageNumber() {
        Page page = Page.of(5, 10);
        Page next = page.nextPage();
        
        assertEquals(6, next.getPageNumber());
    }
    
    @Test
    public void nextPage_preservesFetchSizeHint() {
        Page page = Page.of(0, 10).withFetchSizeHint(50);
        Page next = page.nextPage();
        
        assertTrue(next.getFetchSizeHint().isDefined());
        assertEquals(50, next.getFetchSizeHint().get().intValue());
    }
    
    @Test
    public void of_returnsNoPagingConstantWhenApplicable() {
        Page page = Page.of(0, Integer.MAX_VALUE);
        assertTrue(page == Page.NoPaging); // same instance
    }
}
