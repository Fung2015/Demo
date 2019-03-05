package com.fang.domain;

import java.util.HashMap;
import java.util.Map;

public class Cart {
	
	//该用户选择中存储的n个选择项
	private Map<String,CartItem> cartItems = new HashMap<String,CartItem>();
		
	//作品的总计
	private double total;

	public Map<String, CartItem> getCartItems() {
		return cartItems;
	}

	public void setCartItems(Map<String, CartItem> cartItems) {
		this.cartItems = cartItems;
	}

	public double getTotal() {
		return total;
	}

	public void setTotal(double total) {
		this.total = total;
	}

}
