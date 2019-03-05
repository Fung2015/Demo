package com.fang.web.servlet;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.beanutils.BeanUtils;

import com.fang.domain.Cart;
import com.fang.domain.CartItem;
import com.fang.domain.Category;
import com.fang.domain.Order;
import com.fang.domain.OrderItem;
import com.fang.domain.PageBean;
import com.fang.domain.Product;
import com.fang.domain.User;
import com.fang.service.ProductService;
import com.fang.utils.CommonsUtils;
import com.fang.utils.PaymentUtil;
import com.google.gson.Gson;

public class ProductServlet extends BaseServlet {
	

	//获得我的项目
	public void myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		HttpSession session = request.getSession();

		//判断用户是否已经登录 未登录下面代码不执行
		User user = (User) session.getAttribute("user");
		if(user==null){
			//没有登录
			response.sendRedirect(request.getContextPath()+"/login.jsp");
			return;
		}
		
		ProductService service = new ProductService();
		//查询该用户的所有的项目信息(单表查询orders表)
		//集合中的每一个Order对象的数据是不完整的 缺少List<OrderItem> orderItems数据
		List<Order> orderList = service.findAllOrders(user.getUid());
		//循环所有的项目 为每个项目填选择项集合信息
		if(orderList!=null){
			for(Order order : orderList){
				//获得每一个项目的oid
				String oid = order.getOid();
				//查询该项目的所有的选择项---mapList封装的是多个选择项和该选择项中的作品的信息
				List<Map<String, Object>> mapList = service.findAllOrderItemByOid(oid);
				//将mapList转换成List<OrderItem> orderItems 
				for(Map<String,Object> map : mapList){
					
					try {
						//从map中取出count subtotal 封装到OrderItem中
						OrderItem item = new OrderItem();
						//item.setCount(Integer.parseInt(map.get("count").toString()));
						BeanUtils.populate(item, map);
						//从map中取出pimage pname shop_price 封装到Product中
						Product product = new Product();
						BeanUtils.populate(product, map);
						//将product封装到OrderItem
						item.setProduct(product);
						//将orderitem封装到order中的orderItemList中
						order.getOrderItems().add(item);
					} catch (IllegalAccessException | InvocationTargetException e) {
						e.printStackTrace();
					}
					
					
				}

			}
		}
		
		
		//orderList封装完整了
		request.setAttribute("orderList", orderList);
		
		request.getRequestDispatcher("/order_list.jsp").forward(request, response);
		
		

		
	}
	
	//确认项目---更新收获人信息+在线支付
	public void confirmOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//1、更新收货人信息
		Map<String, String[]> properties = request.getParameterMap();
		Order order = new Order();
		try {
			BeanUtils.populate(order, properties);
		} catch (IllegalAccessException | InvocationTargetException e) {
			e.printStackTrace();
		}

		ProductService service = new ProductService();
		service.updateOrderAdrr(order);

		//2、在线支付
		/*if(pd_FrpId.equals("ABC-NET-B2C")){
			//接入农行的接口
		}else if(pd_FrpId.equals("ICBC-NET-B2C")){
			//接入工行的接口
		}*/
		//.......

		//只接入一个接口，这个接口已经集成所有的银行接口了  ，这个接口是第三方支付平台提供的
		//接入的是易宝支付
		// 获得 支付必须基本数据
		String orderid = request.getParameter("oid");
		//String money = order.getTotal()+"";//支付金额
		String money = "0.01";//支付金额
		// 银行
		String pd_FrpId = request.getParameter("pd_FrpId");

		// 发给支付公司需要哪些数据
		String p0_Cmd = "Buy";
		String p1_MerId = ResourceBundle.getBundle("merchantInfo").getString("p1_MerId");
		String p2_Order = orderid;
		String p3_Amt = money;
		String p4_Cur = "CNY";
		String p5_Pid = "";
		String p6_Pcat = "";
		String p7_Pdesc = "";
		// 支付成功回调地址 ---- 第三方支付公司会访问、用户访问
		// 第三方支付可以访问网址
		String p8_Url = ResourceBundle.getBundle("merchantInfo").getString("callback");
		String p9_SAF = "";
		String pa_MP = "";
		String pr_NeedResponse = "1";
		// 加密hmac 需要密钥
		String keyValue = ResourceBundle.getBundle("merchantInfo").getString(
				"keyValue");
		String hmac = PaymentUtil.buildHmac(p0_Cmd, p1_MerId, p2_Order, p3_Amt,
				p4_Cur, p5_Pid, p6_Pcat, p7_Pdesc, p8_Url, p9_SAF, pa_MP,
				pd_FrpId, pr_NeedResponse, keyValue);


		String url = "https://www.yeepay.com/app-merchant-proxy/node?pd_FrpId="+pd_FrpId+
				"&p0_Cmd="+p0_Cmd+
				"&p1_MerId="+p1_MerId+
				"&p2_Order="+p2_Order+
				"&p3_Amt="+p3_Amt+
				"&p4_Cur="+p4_Cur+
				"&p5_Pid="+p5_Pid+
				"&p6_Pcat="+p6_Pcat+
				"&p7_Pdesc="+p7_Pdesc+
				"&p8_Url="+p8_Url+
				"&p9_SAF="+p9_SAF+
				"&pa_MP="+pa_MP+
				"&pr_NeedResponse="+pr_NeedResponse+
				"&hmac="+hmac;

		//重定向到第三方支付平台
		response.sendRedirect(url);


	}
	
	//提交项目
	public void submitOrder(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
			
		//判断用户是否已经登录 未登录下面代码不执行
		User user = (User) session.getAttribute("user");
		if(user==null){
			//没有登录
			response.sendRedirect(request.getContextPath()+"/login.jsp");
			return;
		}

		//目的：封装好一个Order对象 传递给service层
		Order order = new Order();

		//1、private String oid;//该订单的订单号
		String oid = CommonsUtils.getUUID();
		order.setOid(oid);

		//2、private Date ordertime;//下单时间
		order.setOrdertime(new Date());

		//3、private double total;//该订单的总金额
		//获得session中的用户选择
		Cart cart = (Cart) session.getAttribute("cart");
		double total = cart.getTotal();
		order.setTotal(total);

		//4、private int state;//订单支付状态 1代表已付款 0代表未付款
		order.setState(0);

		//5、private String address;//地址
		order.setAddress(null);

		//6、private String name;//联系人
		order.setName(null);

		//7、private String telephone;//联系人电话
		order.setTelephone(null);

		//8、private User user;//该项目属于哪个用户
		order.setUser(user);
		
		//9、private String remark;//该项目属于哪个用户
		order.setRemark(null);

		//10、该订单中有多少订单项List<OrderItem> orderItems = new ArrayList<OrderItem>();
		//获得我的选择中的选择项的集合map
		Map<String, CartItem> cartItems = cart.getCartItems();
		for(Map.Entry<String, CartItem> entry : cartItems.entrySet()){
			//取出每一个选择项
			CartItem cartItem = entry.getValue();
			//创建新的选择项
			OrderItem orderItem = new OrderItem();
			//1)private String itemid;//选择项的id
			orderItem.setItemid(CommonsUtils.getUUID());
			//2)private int count;//选择项内作品的采购数量
			orderItem.setCount(cartItem.getBuyNum());
			//3)private double subtotal;//选择项小计
			orderItem.setSubtotal(cartItem.getSubtotal());
			//4)private Product product;//选择项内部的作品
			orderItem.setProduct(cartItem.getProduct());
			//5)private Order order;//该选择项属于哪个项目
			orderItem.setOrder(order);

			//将该选择项添加到项目的选择项集合中
			order.getOrderItems().add(orderItem);
		}


		//order对象封装完毕
		//传递数据到service层
		ProductService service = new ProductService();
		service.submitOrder(order);


		session.setAttribute("order", order);

		//页面跳转
		response.sendRedirect(request.getContextPath()+"/order_info.jsp");


	}
	
	//清空我的选择
	public void clearCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		session.removeAttribute("cart");

		//跳转回cart.jsp
		response.sendRedirect(request.getContextPath()+"/cart.jsp");

	}

	//删除单一作品
	public void delProFromCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//获得要删除的item的pid
		String pid = request.getParameter("pid");
		//删除session中我的选择中的选择项集合中的item
		HttpSession session = request.getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart!=null){
			Map<String, CartItem> cartItems = cart.getCartItems();
			//需要修改总价
			cart.setTotal(cart.getTotal()-cartItems.get(pid).getSubtotal());
			//删除
			cartItems.remove(pid);
			cart.setCartItems(cartItems);

		}

		session.setAttribute("cart", cart);

		//跳转回cart.jsp
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	//将作品添加到我的选择
	public void addProductToCart(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();

		ProductService service = new ProductService();


		//获得要放到我的选择的作品的pid
		String pid = request.getParameter("pid");
		//获得该作品的购买数量
		int buyNum = Integer.parseInt(request.getParameter("buyNum"));

		//获得product对象
		Product product = service.findProductByPid(pid);
		//计算小计
		double subtotal = product.getShop_price()*buyNum;
		//封装CartItem
		CartItem item = new CartItem();
		item.setProduct(product);
		item.setBuyNum(buyNum);
		item.setSubtotal(subtotal);

		//获得我的选择---判断是否在session中已经存在用户的选择
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart==null){
			cart = new Cart();
		}

		//将选择项放到车中---key是pid
		//先判断我的选择中是否已将包含此选择项了 ----- 判断key是否已经存在
		//如果我的选择中已经存在该作品----将现在买的数量与原有的数量进行相加操作
		Map<String, CartItem> cartItems = cart.getCartItems();

		double newsubtotal = 0.0;

		if(cartItems.containsKey(pid)){
			//取出原有作品的数量
			CartItem cartItem = cartItems.get(pid);
			int oldBuyNum = cartItem.getBuyNum();
			oldBuyNum+=buyNum;
			cartItem.setBuyNum(oldBuyNum);
			cart.setCartItems(cartItems);
			//修改小计
			//原来该作品的小计
			double oldsubtotal = cartItem.getSubtotal();
			//新买的作品的小计
			newsubtotal = buyNum*product.getShop_price();
			cartItem.setSubtotal(oldsubtotal+newsubtotal);

		}else{
			//如果车中没有该作品
			cart.getCartItems().put(product.getPid(), item);
			newsubtotal = buyNum*product.getShop_price();
		}

		//计算总计
		double total = cart.getTotal()+newsubtotal;
		cart.setTotal(total);


		//再次访问session
		session.setAttribute("cart", cart);

		//直接跳转到我的选作页面
		response.sendRedirect(request.getContextPath()+"/cart.jsp");
	}
	
	//显示作品的类别的的功能
	public void categoryList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();

		//准备分类数据
		List<Category> categoryList = service.findAllCategory();
				
		Gson gson = new Gson();
		String json = gson.toJson(categoryList);
				
		response.setContentType("text/html;charset=UTF-8");
		response.getWriter().write(json);
	}
	
	//显示首页的功能
	//显示作品的类别的的功能
	public void index(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductService service = new ProductService();

		//准备热门作品---List<Product>
		List<Product> hotProductList = service.findHotProductList();

		//准备最新作品---List<Product>
		List<Product> newProductList = service.findNewProductList();

		//准备分类数据
		//List<Category> categoryList = service.findAllCategory();

		//request.setAttribute("categoryList", categoryList);
		request.setAttribute("hotProductList", hotProductList);
		request.setAttribute("newProductList", newProductList);

		request.getRequestDispatcher("/index.jsp").forward(request, response);

	}

	//显示作品的详细信息功能
	public void productInfo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//获得当前页
		String currentPage = request.getParameter("currentPage");
		//获得作品类别
		String cid = request.getParameter("cid");

		//获得要查询的作品的pid
		String pid = request.getParameter("pid");

		ProductService service = new ProductService();
		Product product = service.findProductByPid(pid);

		request.setAttribute("product", product);
		request.setAttribute("currentPage", currentPage);
		request.setAttribute("cid", cid);


		//获得客户端携带cookie---获得名字是pids的cookie
		String pids = pid;
		Cookie[] cookies = request.getCookies();
		if(cookies!=null){
			for(Cookie cookie : cookies){
				if("pids".equals(cookie.getName())){
					pids = cookie.getValue();
					//1-3-2 本次访问商品pid是8----->8-1-3-2
					//1-3-2 本次访问商品pid是3----->3-1-2
					//1-3-2 本次访问商品pid是2----->2-1-3
					//将pids拆成一个数组
					String[] split = pids.split("-");//{3,1,2}
					List<String> asList = Arrays.asList(split);//[3,1,2]
					LinkedList<String> list = new LinkedList<String>(asList);//[3,1,2]
					//判断集合中是否存在当前pid
					if(list.contains(pid)){
						//包含当前查看作品的pid
						list.remove(pid);
						list.addFirst(pid);
					}else{
						//不包含当前查看作品的pid 直接将该pid放到头上
						list.addFirst(pid);
					}
					//将[3,1,2]转成3-1-2字符串
					StringBuffer sb = new StringBuffer();
					for(int i=0;i<list.size()&&i<7;i++){
						sb.append(list.get(i));
						sb.append("-");//3-1-2-
					}
					//去掉3-1-2-后的-
					pids = sb.substring(0, sb.length()-1);
				}
			}
		}


		Cookie cookie_pids = new Cookie("pids",pids);
		response.addCookie(cookie_pids);

		request.getRequestDispatcher("/product_info.jsp").forward(request, response);

	}

	//根据作品的类别获得作品的列表
	public void productList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		//获得cid
		String cid = request.getParameter("cid");

		String currentPageStr = request.getParameter("currentPage");
		if(currentPageStr==null) currentPageStr="1";
		int currentPage = Integer.parseInt(currentPageStr);
		int currentCount = 12;

		ProductService service = new ProductService();
		PageBean pageBean = service.findProductListByCid(cid,currentPage,currentCount);

		request.setAttribute("pageBean", pageBean);
		request.setAttribute("cid", cid);

		//定义一个记录历史作品信息的集合
		List<Product> historyProductList = new ArrayList<Product>();

		//获得客户端携带名字叫pids的cookie
		Cookie[] cookies = request.getCookies();
		if(cookies!=null){
			for(Cookie cookie:cookies){
				if("pids".equals(cookie.getName())){
					String pids = cookie.getValue();//3-2-1
					String[] split = pids.split("-");
					for(String pid : split){
						Product pro = service.findProductByPid(pid);
						historyProductList.add(pro);
					}
				}
			}
		}

		//将历史记录的集合放到域中
		request.setAttribute("historyProductList", historyProductList);

		request.getRequestDispatcher("/product_list.jsp").forward(request, response);


	}

}
