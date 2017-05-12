package anson.std.medical.dealer.web.api.impl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by anson on 17-5-12.
 */

public class DoctorFilter {

    private List<String> includeList;

    public DoctorFilter() {
        this.includeList = new ArrayList<>();
    }

    public boolean doFilter(String str){
        return includeList.contains(str);
    }

    public void add(String str){
        includeList.add(str);
    }
}
