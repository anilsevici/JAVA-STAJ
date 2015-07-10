package com.anilsevici.services;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * Servlet implementation class IlanGorServlet
 */
@WebServlet("/IlanGorServlet")
public class IlanGorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public IlanGorServlet() {
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
		MongoClient mongoClient = new MongoClient(new MongoClientURI(
				"mongodb://localhost"));
		DB ilanDatabase = mongoClient.getDB("kariyer");
		DBCollection ilancollection = ilanDatabase.getCollection("ilan");

		BasicDBObject query = new BasicDBObject("statu", true);
		DBCursor cursor = ilancollection.find(query);
		List<DBObject> ilanlar = cursor.toArray();

		Gson gson = new Gson();
		JsonElement element = gson.toJsonTree(ilanlar,
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
