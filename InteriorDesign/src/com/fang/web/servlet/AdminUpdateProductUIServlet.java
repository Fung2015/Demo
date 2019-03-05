package com.fang.web.servlet;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fang.domain.Category;
import com.fang.domain.Product;
import com.fang.service.AdminService;

public class AdminUpdateProductUIServlet extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//获得要查询Product的pid
		String pid = request.getParameter("pid");
		//传递pid查询作品信息
		AdminService service = new AdminService();
		Product product = null;
		try {
			product = service.findProductByPid(pid);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		//获得所有的作品的类别数据
		List<Category> categoryList = null;
		categoryList = service.findAllCategory();

		request.setAttribute("categoryList", categoryList);
		request.setAttribute("product", product);

		request.getRequestDispatcher("/admin/product/edit.jsp").forward(request, response);

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}