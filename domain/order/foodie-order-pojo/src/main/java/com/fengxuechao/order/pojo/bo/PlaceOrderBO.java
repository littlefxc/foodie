package com.fengxuechao.order.pojo.bo;

import com.fengxuechao.cart.pojo.ShopcartBO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author fengxuechao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlaceOrderBO {

    private SubmitOrderBO order;

    private List<ShopcartBO> items;

}
