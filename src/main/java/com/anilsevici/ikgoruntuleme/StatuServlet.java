package com.anilsevici.ikgoruntuleme;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anilsevici.mongodb.MongoDbUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Servlet implementation class StatuServlet
 */
@WebServlet("/StatuServlet")
public class StatuServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection usercollection;

	/**
	 * @throws UnknownHostException
	 * @see HttpServlet#HttpServlet()
	 */
	public StatuServlet() throws UnknownHostException {
		super();
		usercollection = MongoDbUtils.getUserCollection();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String ilanid = request.getParameter("ilanid");
		String statu = request.getParameter("statu");

		BasicDBObject searchquery = new BasicDBObject().append("basvurular",
				new BasicDBObject().append("$elemMatch", new BasicDBObject()
						.append("statu", statu).append("ilanno", ilanid)));

		List<DBObject> users = usercollection.find(searchquery).toArray();

		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(users,
				new TypeToken<List<DBObject>>() {
				}.getType());
		JsonArray jsonArray = element.getAsJsonArray();

		writeResponse(response, jsonArray.toString());

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	private void writeResponse(HttpServletResponse response, String result)
			throws IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		response.getWriter().write(result);
		response.getWriter().flush();
	}

}
