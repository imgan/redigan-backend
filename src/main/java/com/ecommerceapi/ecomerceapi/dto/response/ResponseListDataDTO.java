package com.ecommerceapi.ecomerceapi.dto.response;

import java.util.ArrayList;
import java.util.List;

public class ResponseListDataDTO<Any> {

    private Integer limit = 15;

    private Integer page = 1;

    private Integer total = 0;

    private String search = "";

    private ArrayList<Any> data = new ArrayList<Any>();

    /** Constructor */
    public ResponseListDataDTO(Integer limit, Integer page, Integer total, String search, ArrayList<Any> data) {
        this.limit = limit;
        this.page = page;
        this.total = total;
        this.search = search;
        this.data = data;
    }

    /** Getter Setter */
    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public ArrayList<Any> getData() {
        return data;
    }

    public void setData(ArrayList<Any> data) {
        this.data = data;
    }
    /** End Getter Setter */
}
