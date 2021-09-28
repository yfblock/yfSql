package Test;

import lombok.Data;

@Data
public class CartView {
    private Integer id;                 // 购物车记录编号
    private Integer goodId;             // 商品编号
    private Integer userId;             // 用户编号
    private Integer number;             // 购买数量
    private String cover;               // 商品封面
    private String name;                // 商品名称
    private Double price;               // 商品单价
}
