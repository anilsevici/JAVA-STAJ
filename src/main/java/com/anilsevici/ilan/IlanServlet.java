package com.anilsevici.ilan;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
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
		final MongoClient mongoClient = new MongoClient(new MongoClientURI(
				"mongodb://localhost"));
		final DB ilanDatabase = mongoClient.getDB("kariyer");
		ilancollection = ilanDatabase.getCollection("ilan");
		hrcollection = ilanDatabase.getCollection("hrilan");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String data = request.getParameter("ilan");
		System.out.println(data);

		if (data != null) {

			Parser parse = new Parser(data);
			BasicDBObject ilan = parse.addObject();

			ilancollection.insert(ilan);
			inserthrilan(request, parse);

		}
	}

	private void inserthrilan(HttpServletRequest request, Parser parse) {

		String userId = null;
		String ilanid = parse.getIlan_id();

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
