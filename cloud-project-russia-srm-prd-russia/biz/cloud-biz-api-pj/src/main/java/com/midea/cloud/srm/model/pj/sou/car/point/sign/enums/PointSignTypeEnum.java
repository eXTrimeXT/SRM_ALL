package com.midea.cloud.srm.model.pj.sou.car.point.sign.enums;

/**
 * <pre>
 *  寻源-定点会签类型
 *  字典：
 * </pre>
 *
 * @author zhaoming1.kuang@meicloud.com
 * @version 1.00.00
 *
 * <pre>
 *  修改记录
 *  修改后版本:
 *  修改人:
 *  修改日期: 2022/10/10 16:13
 *  修改内容:
 * </pre>
 */
public enum PointSignTypeEnum {
    /**
     * 零部件定点会签
     */
    SIGN_PARTS,
    /*零部件沿用会签 */
    SIGN_CONTINUS_PARTS,
    /*研发非生产会签 */
    SIGN_RD,
    /*研发非生产沿用会签 */
    SIGN_CONTINUS_RD,
    /*研发非生产会签终止 */
    SIGN_RD_END
}
