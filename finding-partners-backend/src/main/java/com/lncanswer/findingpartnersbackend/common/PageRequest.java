package com.lncanswer.findingpartnersbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/28 11:06
 */
@Data
public class PageRequest implements Serializable {


    /**
     * 页面大小
     */
    protected int pageSize = 10;

    /**
     * 当前是第几页
     */
    protected int pageNum = 1;
}
