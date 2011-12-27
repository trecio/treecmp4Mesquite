/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.validators;

import java.io.File;
import java.util.List;
import java.util.ListIterator;
import org.apache.commons.cli2.validation.FileValidator;
import org.apache.commons.cli2.validation.InvalidArgumentException;

/**
 *
 * @author Damian
 */
public class FileValidatorEx extends FileValidator {

    public FileValidatorEx() {
    }

    /**
     * Returns a <code>FileValidator</code> for existing files/directories.
     *
     * @return a <code>FileValidator</code> for existing files/directories.
     */
    public static FileValidator getExistingInstance() {
        final FileValidator validator = new FileValidatorEx();
        validator.setExisting(true);
        return validator;
    }

     /**
     * Returns a <code>FileValidator</code> for existing files.
     *
     * @return a <code>FileValidator</code> for existing files.
     */
    public static FileValidator getExistingFileInstance() {
        final FileValidator validator = new FileValidatorEx();
        validator.setExisting(true);
        validator.setFile(true);
        return validator;
    }

        /**
     * Returns a <code>FileValidator</code> for existing directories.
     *
     * @return a <code>FileValidator</code> for existing directories.
     */
    public static FileValidator getExistingDirectoryInstance() {
        final FileValidator validator = new FileValidator();
        validator.setExisting(true);
        validator.setDirectory(true);
        return validator;
    }

    @Override
    public void validate(List values) throws InvalidArgumentException {
          for (final ListIterator i = values.listIterator(); i.hasNext();) {
            final String name = (String)i.next();
            final File f = new File(name);
                  
            if ((this.isExisting() && !f.exists())
                || (this.isFile() && !f.isFile())
                || (this.isDirectory() && !f.isDirectory())
                || (this.isHidden() && !f.isHidden())
                || (this.isReadable() && !f.canRead())
                || (this.isWritable() && !f.canWrite())) {

                throw new InvalidArgumentException(name);
            }

          }
    }

}
