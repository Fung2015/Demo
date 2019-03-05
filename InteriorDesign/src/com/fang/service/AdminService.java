package com.fang.service;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import com.fang.dao.AdminDao;
import com.fang.domain.Category;
import com.fang.domain.Order;
import com.fang.domain.Product;

public class AdminService {
	
	//查询所有的作品
	public List<Product> findAllProduct() throws SQLException {
		//因为没有复杂业务 直接传递请求到dao层
		AdminDao dao = new AdminDao();
		return dao.findAllProduct();
	}

	public List<Category> findAllCategory() {
		AdminDao dao = new AdminDao();
		try {
			return dao.findAllCategory();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public void saveProduct(Product product) throws SQLException {
		AdminDao dao = new AdminDao();
		dao.saveProduct(product);
	}
	
	//根据pid删除作品
	public void delProductByPid(String pid) throws SQLException {
		AdminDao dao = new AdminDao();
		dao.delProductByPid(pid);
	}
	
	//根据pid查询作品对象
	public Product findProductByPid(String pid) throws SQLException {
		AdminDao dao = new AdminDao();
		return dao.findProductByPid(pid);
	}

	public List<Order> findAllOrders() {
		AdminDao dao = new AdminDao();
		List<Order> ordersList = null;
		try {
			ordersList = dao.findAllOrders();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ordersList;
	}

	public List<Map<String, Object>> findOrderInfoByOid(String oid) {
		AdminDao dao = new AdminDao();
		List<Map<String, Object>> mapList = null;
		try {
			mapList = dao.findOrderInfoByOid(oid);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapList;
	}

}

