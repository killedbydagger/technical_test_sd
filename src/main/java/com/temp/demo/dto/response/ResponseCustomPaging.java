package com.temp.demo.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
@NoArgsConstructor
public class ResponseCustomPaging<T> {

    private List<T> content;
    private long totalElements;
    private boolean first;
    private boolean last;
    private int totalPages;

    public ResponseCustomPaging(Page<T> all) {
        this.content = all.getContent();
        this.totalElements = all.getTotalElements();
        this.first = all.isFirst();
        this.last = all.isLast();
        this.totalPages = all.getTotalPages();
    }
}
