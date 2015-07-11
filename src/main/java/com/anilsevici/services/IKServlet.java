package com.anilsevici.services;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Servlet implementation class IKServlet
 */
@WebServlet("/IKServlet")
public class IKServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IKServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String data = request.getParameter("user");
		JsonObject object=new JsonObject();

		JsonParser parser = new JsonParser();
		JsonObject joUser = parser.parse(data).getAsJsonObject();

		String name = joUser.get("username").getAsString();
		String password = joUser.get("password").getAsString();
		// object.addProperty("username", name);

		LdapAccounting con = new LdapAccounting(name, password);
		try {
			con.connect();
			object.addProperty("username", name);
			object.addProperty("auth", true);
			Cookie ck = new Cookie("name", name);
			response.addCookie(ck);
			writeResponse(response, object.toString());
		} catch (Exception e) {
			// object.addProperty("auth", false);
			// writeResponse(response, object.toString());
		}
	}

	private void writeResponse(HttpServletResponse response, String result)
			throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.getWriter().write(result);
		response.getWriter().flush();
	}

}
