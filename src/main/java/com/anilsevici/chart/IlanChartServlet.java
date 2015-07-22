package com.anilsevici.chart;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anilsevici.mongodb.MongoDbUtils;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * Servlet implementation class IlanChartServlet
 */
@WebServlet("/IlanChartServlet")
public class IlanChartServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection ilanlarcollection;

	/**
	 * @throws UnknownHostException
	 * @see HttpServlet#HttpServlet()
	 */
	public IlanChartServlet() throws UnknownHostException {
		super();
		ilanlarcollection = MongoDbUtils.getIlanCollection();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		try {
			BasicDBObject groupFields = new BasicDBObject("_id", "$tag");
			groupFields.append("count", new BasicDBObject("$sum", 1));

			BasicDBObject group = new BasicDBObject().append("$group",
					groupFields);

			BasicDBObject condition = new BasicDBObject().append("statu", true);
			BasicDBObject con = new BasicDBObject().append("$match", condition);

			AggregationOutput output = ilanlarcollection.aggregate(con, group);

			Gson gson = new Gson();
			JsonObject user = gson
					.fromJson(output.toString(), JsonObject.class);

			JsonArray el = user.get("result").getAsJsonArray();

			writeResponse(response, el.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

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
