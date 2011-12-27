/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.validators;

import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.cli2.resource.ResourceConstants;
import org.apache.commons.cli2.resource.ResourceHelper;
import org.apache.commons.cli2.validation.InvalidArgumentException;
import org.apache.commons.cli2.validation.NumberValidator;

/**
 *
 * @author Damian
 */
public class NumberValidatorEx extends NumberValidator{


    /**
     * Creates a new NumberValidator based on the specified NumberFormat
     * @param format the format of numbers to accept
     */
    public NumberValidatorEx(final NumberFormat format) {
        super(format);
        setFormat(format);
    }


    /**
     * Returns a <code>NumberValidator</code> for a currency format
     * for the current default locale.
     * @return a <code>NumberValidator</code> for a currency format
     * for the current default locale.
     */
    public static NumberValidatorEx getCurrencyInstance() {
       return new NumberValidatorEx(NumberFormat.getCurrencyInstance());
    }

    /**
     * Returns a <code>NumberValidator</code> for an integer number format
     * for the current default locale.
     * @return a <code>NumberValidator</code> for an integer number format
     * for the current default locale.
     */
    public static NumberValidatorEx getIntegerInstance() {
        final NumberFormat format = NumberFormat.getNumberInstance();
        format.setParseIntegerOnly(true);

        return new NumberValidatorEx(format);
    }

    /**
     * Returns a <code>NumberValidator</code> for a percentage format
     * for the current default locale.
     * @return a <code>NumberValidator</code> for a percentage format
     * for the current default locale.
     */
    public static NumberValidatorEx getPercentInstance() {
        return new NumberValidatorEx(NumberFormat.getPercentInstance());
    }

    /**
     * Returns a <code>NumberValidator</code> for a general-purpose
     * number format for the current default locale.
     * @return a <code>NumberValidator</code> for a general-purpose
     * number format for the current default locale.
     */
    public static NumberValidatorEx getNumberInstance() {
        return new NumberValidatorEx(NumberFormat.getNumberInstance());
    }

    /**
     * Validate the list of values against the list of permitted values.
     * If a value is valid, replace the string in the <code>values</code>
     * {@link java.util.List} with the {@link java.lang.Number} instance.
     *
     * @see org.apache.commons.cli2.validation.Validator#validate(java.util.List)
     */
    @Override
    public void validate(final List values)
        throws InvalidArgumentException {
        for (final ListIterator i = values.listIterator(); i.hasNext();) {
            final String value = (String) i.next();

            final ParsePosition pp = new ParsePosition(0);
            final Number number = this.getFormat().parse(value, pp);

            if (pp.getIndex() < value.length()) {
                throw new InvalidArgumentException(value);
            }

            if (((this.getMinimum() != null) && (number.doubleValue() < this.getMinimum().doubleValue())) ||
                    ((this.getMaximum() != null) && (number.doubleValue() > this.getMaximum().doubleValue()))) {
                throw new InvalidArgumentException(ResourceHelper.getResourceHelper().getMessage(ResourceConstants.NUMBERVALIDATOR_NUMBER_OUTOFRANGE,
                                                                                                 new Object[] {
                                                                                                     value
                                                                                                 }));
            }

         }
    }
}
