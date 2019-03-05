package com.fang.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
	
	private String oid;//该项目的项目号
	private Date ordertime;//采购时间
	private double total;//该项目的总金额
	private int state;//项目支付状态 1代表已付款 0代表未付款
	
	private String address;//地址
	private String name;//联系人
	private String telephone;//联系人电话
	private String remark;//备注信息
	
	private User user;//该项目属于哪个用户
	
	//该项目中有多少采购项
	List<OrderItem> orderItems = new ArrayList<OrderItem>();
	
	
	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}

	public Date getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(Date ordertime) {
		this.ordertime = ordertime;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
	public String getRemark() {
		return address;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<OrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(List<OrderItem> orderItems) {
		this.orderItems = orderItems;
	}

}
