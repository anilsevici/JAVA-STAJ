package com.anilsevici.ilan;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anilsevici.mongodb.MongoDbUtils;
import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException.DuplicateKey;

/**
 * Servlet implementation class IlanServlet
 */
@WebServlet("/IlanServlet")
public class IlanServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection ilancollection;
	private final DBCollection hrcollection;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IlanServlet() throws IOException {
		super();
		// TODO Auto-generated constructor stub

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
		String data = request.getParameter("ilan");
		String data2 = request.getParameter("edit");
		Parser parse = new Parser(data);

		if (data2.equals("true")) {

			BasicDBObject ilan = parse.addObject();

			try {
				ilancollection.insert(ilan);
				inserthrilan(request, parse, data);
			} catch (DuplicateKey e) {
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
			}

		} else {
			JsonObject ilan = parse.parseinit(data);

			BasicDBObject newdoc = new BasicDBObject();
			BasicDBObject fieldset = new BasicDBObject();

			fieldset.append("title", ilan.get("title").getAsString());
			fieldset.append("definition", ilan.get("definition").getAsString());
			fieldset.append("description", ilan.get("description")
					.getAsString());
			fieldset.append("aktif", ilan.get("aktif").getAsString());
			fieldset.append("pasif", ilan.get("pasif").getAsString());
			fieldset.append("tag", ilan.get("tag").getAsString());

			BasicDBObject searchQuery = new BasicDBObject().append("_id", ilan
					.get("_id").getAsString());

			if (request.getParameter("aktif").equals("true")) {
				fieldset.append("publish", true);
				fieldset.append("statu", false);
			}

			if (request.getParameter("pasif").equals("true")) {

				fieldset.append("publish", false);
				fieldset.append("statu", false);
			}

			newdoc.append("$set", fieldset);

			ilancollection.update(searchQuery, newdoc);
		}
	}

	private void inserthrilan(HttpServletRequest request, Parser parse,
			String data) {

		String userId = null;
		JsonObject ilan = parse.parseinit(data);
		String ilanid = ilan.get("_id").getAsString();

		Cookie[] cookies = request.getCookies();
		for (Cookie cookie : cookies) {
			if ("name".equals(cookie.getName()))
				userId = cookie.getValue();

		}

		BasicDBObject document = new BasicDBObject().append("_id", userId)
				.append("ilanref", Arrays.asList(ilanid));

		try {
			hrcollection.insert(document);
		} catch (DuplicateKey e) {
			BasicDBObject list = new BasicDBObject().append("ilanref", ilanid);

			BasicDBObject updateQuery = new BasicDBObject("$push", list);
			BasicDBObject searchQuery = new BasicDBObject("_id", userId);
			hrcollection.update(searchQuery, updateQuery);
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

}
