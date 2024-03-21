package com.centent.data.division.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@JsonInclude(Include.NON_EMPTY) // 空值不参与序列化
@EqualsAndHashCode(of = "code")
public class Region {

    private Integer code;

    private String name;

    @JsonIgnore
    private Region parent;

    private Set<Region> children = new LinkedHashSet<>();

    public Region(Integer code) {
        this.code = code;
    }
}
