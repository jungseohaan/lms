
package com.visang.aidt.lms.api.contents.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class _baseVO_save extends _baseContentsVO_request {

    public Boolean saveTypeEqualInsert;

    public String getSaveType() {
        if (saveTypeEqualInsert) return "insert";
        else return "update";
    }

    public void setSaveType(String str) {
        if (str.equalsIgnoreCase("insert"))
            this.saveTypeEqualInsert = true;
        else
            this.saveTypeEqualInsert = false;
    }
}
