package com.enation.app.b2b2c.core.model.bill;

/**
 * 结算单状态
 * @author fenlongli
 * @date 2015-6-7 下午4:21:31
 */
public enum BillStatusEnum {

	NEW("已生成",1),COMPLETE("已确认",2),PASS("已审核",3),PAY("已付款",4);
	private String name;
    private int index;
    // 构造方法
    private BillStatusEnum(String name, int index) {
        this.name = name;
        this.index = index;
    }
    public static String getName(int index) {  
        for (BillStatusEnum b : BillStatusEnum.values()) {  
            if (b.getIndex() == index) {  
                return b.name;  
            }  
        }  
        return null;  
    } 
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
}
