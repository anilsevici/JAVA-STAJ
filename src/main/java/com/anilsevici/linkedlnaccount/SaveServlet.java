package com.anilsevici.linkedlnaccount;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException.DuplicateKey;

/**
 * Servlet implementation class SaveServlet
 */
@WebServlet("/SaveServlet")
public class SaveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection usercollection;

	/**
	 * @throws UnknownHostException
	 * @see HttpServlet#HttpServlet()
	 */
	public SaveServlet() throws UnknownHostException {
		super();
		// TODO Auto-generated constructor stub
		final MongoClient mongoClient = new MongoClient(new MongoClientURI(
				"mongodb://localhost"));
		final DB ilanDatabase = mongoClient.getDB("kariyer");
		usercollection = ilanDatabase.getCollection("userprofile");

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
		String data = request.getParameter("userprofile");
		String data2 = request.getParameter("ilanid");
		System.out.println(data);

		JsonElement parser = new JsonParser().parse(data);
		JsonObject joUser = parser.getAsJsonObject();

		joUser.addProperty("_id", joUser.get("id").getAsString());
		joUser.remove("id");

		String id = joUser.get("_id").getAsString();

		Gson gson = new Gson();
		BasicDBObject user = gson.fromJson(joUser, BasicDBObject.class);

		try {
			BasicDBObject basvuru = new BasicDBObject("ilanno", data2).append(
					"statu", "bekleme");
			user.append("basvurular", Arrays.asList(basvuru));
			usercollection.insert(user);
		} catch (DuplicateKey e) {

			BasicDBObject obj = new BasicDBObject("ilanno", data2).append(
					"statu", "bekleme");
			BasicDBObject listItem = new BasicDBObject("basvurular", obj);

			BasicDBObject updateQuery = new BasicDBObject("$push", listItem);
			BasicDBObject searchQuery = new BasicDBObject("_id", id);
			usercollection.update(searchQuery, updateQuery);

		}

	}
}
