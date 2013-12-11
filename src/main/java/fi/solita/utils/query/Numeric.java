package fi.solita.utils.query;

/**
 * Types that are representable as a Number 
 *
 * @author Jyri-Matti Lähteenmäki / Solita Oy
 *
 */
public interface Numeric {
    
    /** Since the type can be numeric in many ways, the returned Number doesn't
     * have to accurately represent the actual value. That is, this is a
     * conversion that most likely loses some relevant information.
     */
    Number toNumber();
}
