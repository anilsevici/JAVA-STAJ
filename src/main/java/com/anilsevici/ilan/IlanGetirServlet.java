package com.anilsevici.ilan;

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
 * Servlet implementation class IlanGetirServlet
 */
@WebServlet("/IlanGetirServlet")
public class IlanGetirServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection ilancollection;
	private final DBCollection hrcollection;

	/**
	 * @throws UnknownHostException
	 * @see HttpServlet#HttpServlet()
	 */
	public IlanGetirServlet() throws UnknownHostException {
		super();
		ilancollection = MongoDbUtils.getIlanCollection();
		hrcollection = MongoDbUtils.getHrIlanCollection();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		try {
			String hrid = request.getParameter("hrid");

			BasicDBObject projectionquery = new BasicDBObject().append("_id",
					hrid);
			BasicDBObject includekeys = new BasicDBObject()
					.append("ilanref", 1).append("_id", 0);

			DBObject obj = hrcollection.findOne(projectionquery, includekeys);
			String bas = obj.get("ilanref").toString();

			Gson gson = new Gson();
			List<String> ilanlar = gson.fromJson(bas,
					new TypeToken<List<String>>() {
					}.getType());

			JsonArray array = new JsonArray();

			for (String i : ilanlar) {

				DBObject ilan = ilancollection.findOne(new BasicDBObject()
						.append("_id", i));

				array.add(new JsonParser().parse(ilan.toString()));

			}

			writeResponse(response, array.toString());
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
