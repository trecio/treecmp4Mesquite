/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package treecmp.test.util;

import pal.misc.IdGroup;
import pal.misc.Identifier;
import pal.misc.SimpleIdGroup;

/**
 *
 * @author Damian
 */
public class TestUtils {

    public static IdGroup genIdGroup(int n) {
        IdGroup idGroup = new SimpleIdGroup(n);
        for (int i = 1; i <= n; i++) {
            idGroup.setIdentifier(i - 1, new Identifier(String.valueOf(i)));
        }
        return idGroup;
    }
}
