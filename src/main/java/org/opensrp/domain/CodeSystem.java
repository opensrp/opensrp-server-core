/**
 *
 */
package org.opensrp.domain;

import java.io.Serializable;
import java.util.List;

/**
 * @author Samuel Githengi created on 08/30/19
 */
public class CodeSystem implements Serializable {

    private static final long serialVersionUID = -2587903025581183298L;

    private List<Code> coding;

    public List<Code> getCoding() {
        return coding;
    }

    public void setCoding(List<Code> coding) {
        this.coding = coding;
    }

}
