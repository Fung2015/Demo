<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<!-- 登录 注册 收藏库... -->
<div class="container-fluid">
	<div class="col-md-4">
		<img src="img/logo.jpg" />
	</div>
	<div class="col-md-5">
		<img src="img/logo2.png" />
	</div>
	<div  style="padding-top:30px">
		<ol class="list-inline">
			<c:if test="${empty user }">
				<li><a href="login.jsp">登录</a></li>
				<li><a href="register.jsp">注册</a></li>
			</c:if>
			<c:if test="${!empty user }">
				<li style="color:red">${user.username }</li>
				<li><a href="${pageContext.request.contextPath }/user?method=logout">注销</a></li>
			</c:if>
			<li><a href="cart.jsp">我的选择</a></li>
			<li><a href="${pageContext.request.contextPath }/product?method=myOrders">采购项目</a></li>
		</ol>
	</div>
</div>

<!-- 导航条 -->
<div class="container-fluid">
	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="default.jsp">首页</a>
			</div>

			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav" id="categoryUl">
				
					<%-- <c:forEach items="${categoryList }" var="category">
						<li><a href="#">${category.cname }</a></li>
					</c:forEach> --%>
					
				</ul>
				
			</div>
		</div>
		
		<script type="text/javascript">
			//header.jsp加载完毕后 去服务器端获得所有的category数据
			$(function(){
				var content = "";
				$.post(
					"${pageContext.request.contextPath}/product?method=categoryList",
					function(data){
						//[{"cid":"xxx","cname":"xxxx"},{},{}]
						//动态创建<li><a href="#">${category.cname }</a></li>
						for(var i=0;i<data.length;i++){
							content+="<li><a href='${pageContext.request.contextPath}/product?method=productList&cid="+data[i].cid+"'>"+data[i].cname+"</a></li>";
						}
						
						//将拼接好的li放置到ul中
						$("#categoryUl").html(content);
					},
					"json"
				);
			});
		</script>
		
	</nav>
</div>