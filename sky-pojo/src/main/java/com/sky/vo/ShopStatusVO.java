package com.sky.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 店铺营业状态VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopStatusVO implements Serializable {

    private static final long serialVersionUID = 1L;

    // 店铺营业状态：1为营业，0为打烊
    private Integer status;
}
