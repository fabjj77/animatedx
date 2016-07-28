package com.cs.report;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Hadi Movaghar
 */
public class ReportData implements Serializable {
    private static final long serialVersionUID = 1L;

    private Date startTime;
    private String[] headers;
    private final List<Object[]> data;

    public ReportData() {
        startTime = new Date();
        data = new ArrayList<>();
    }

    public String[] getHeaders() {
        return headers;
    }

    public void setHeaders(final String... headers) {
        this.headers = headers;
    }

    public void addRow(final Object... objects) {
        data.add(objects);
    }

    public List<Object[]> getData() {
        return data;
    }

    public Date getStartTime() {
        return startTime;
    }
}
