package xyz.yuanjin.project.pojo;

import lombok.Data;

@Data
public class PageParam<T> {
    private String sortName;
    private String sortOrder;
    private Integer pageSize;
    private Integer pageNumber;

    private T queryObj;
}
