/**
 *
 */
package org.opensrp.domain.postgres;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Samuel Githengi created on 09/25/20
 */
@Data
@AllArgsConstructor
public class DateRange {

    private Date fromdate;

    private Date toDate;
}
