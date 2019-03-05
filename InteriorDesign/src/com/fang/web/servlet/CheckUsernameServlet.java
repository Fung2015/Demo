package com.fang.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fang.service.UserService;

/**
 * Servlet implementation class CheckUsernameServlet
 */
public class CheckUsernameServlet extends HttpServlet {
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		
		//获得用户名
		String username = request.getParameter("username");
		
		UserService service = new UserService();
		boolean isExist = service.checkUsername(username);
		
		String json = "{\"isExist\":"+isExist+"}";
		
		response.getWriter().write(json);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
