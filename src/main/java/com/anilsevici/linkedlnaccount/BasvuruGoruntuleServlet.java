package com.anilsevici.linkedlnaccount;

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
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Servlet implementation class BasvuruGoruntuleServlet
 */
@WebServlet("/BasvuruGoruntuleServlet")
public class BasvuruGoruntuleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection ilancollection;
	private final DBCollection usercollection;

	/**
	 * @throws UnknownHostException
	 * @see HttpServlet#HttpServlet()
	 */
	public BasvuruGoruntuleServlet() throws UnknownHostException {
		super();

		ilancollection = MongoDbUtils.getIlanCollection();
		usercollection = MongoDbUtils.getUserCollection();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userid = request.getParameter("id");
		String tempid = request.getParameter("tempid");

		if (userid == null)
			userid = tempid;

		BasicDBObject projectionquery = new BasicDBObject().append("_id",
				userid);
		BasicDBObject includekeys = new BasicDBObject().append("basvurular", 1)
				.append("_id", 0);

		DBObject obj = usercollection.findOne(projectionquery, includekeys);
		String bas = obj.get("basvurular").toString();

		Gson gson = new Gson();
		List<BasicDBObject> basvurular = gson.fromJson(bas,
				new TypeToken<List<BasicDBObject>>() {
				}.getType());

		JsonArray array = new JsonArray();

		for (BasicDBObject basvuru : basvurular) {

			String status = basvuru.get("statu").toString();
			String ilano = basvuru.get("ilanno").toString();

			DBObject ilan = ilancollection.findOne(new BasicDBObject().append(
					"_id", ilano));
			ilan.put("statu", status);
			array.add(new JsonParser().parse(ilan.toString()));

		}

		writeResponse(response, array.toString());

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
