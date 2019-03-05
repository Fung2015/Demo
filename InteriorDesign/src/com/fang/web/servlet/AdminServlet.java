package com.fang.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.naming.factory.BeanFactory;

import com.fang.domain.Category;
import com.fang.domain.Order;
import com.fang.service.AdminService;
import com.google.gson.Gson;

public class AdminServlet extends BaseServlet {
	
	//根据项目id查询采购项和作品信息
	public void findOrderInfoByOid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		try {
			Thread.sleep(1500);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		//获得oid
		String oid = request.getParameter("oid");
		
		AdminService service = new AdminService();
		
		List<Map<String,Object>> mapList = service.findOrderInfoByOid(oid);
		
		Gson gson = new Gson();
		String json = gson.toJson(mapList);
		//System.out.println(json);
		
		response.setContentType("text/html;charset=UTF-8");
		
		response.getWriter().write(json);
		
	}
	
	//获得所有的项目
	public void findAllOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获得所有的项目信息----List<Order>
		
		AdminService service = new AdminService();
		List<Order> orderList = service.findAllOrders();
		
		request.setAttribute("orderList", orderList);
		
		request.getRequestDispatcher("/admin/order/list.jsp").forward(request, response);
		
	}

	public void findAllCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//提供一个List<Category> 转成json字符串
		AdminService service = new AdminService();
		List<Category> categoryList = service.findAllCategory();
		
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
		
		response.setContentType("text/html;charset=UTF-8");
		
		response.getWriter().write(json);
		
	}

	
}
