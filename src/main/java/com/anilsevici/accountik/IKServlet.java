package com.anilsevici.accountik;

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

		JsonParser parser = new JsonParser();
		JsonObject joUser = parser.parse(data).getAsJsonObject();

		String name = joUser.get("username").getAsString();
		String password = joUser.get("password").getAsString();

		LdapAccounting con = new LdapAccounting(name, password);

		try {
			con.connect();
			Cookie ck = new Cookie("name", name);
			response.addCookie(ck);
		} catch (Exception e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}

	}

}
