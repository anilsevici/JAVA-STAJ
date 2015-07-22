package com.anilsevici.saveuser;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anilsevici.elasticsearch.ElasticSearchUtils;
import com.anilsevici.mongodb.MongoDbUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.MongoException.DuplicateKey;

/**
 * Servlet implementation class UserSaveServlet
 */
@WebServlet("/UserSaveServlet")
public class UserSaveServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection usercollection;

	/**
	 * @throws UnknownHostException
	 * @see HttpServlet#HttpServlet()
	 */
	public UserSaveServlet() throws UnknownHostException {
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
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String data = request.getParameter("userprofile");
		System.out.println(data);

		JsonElement parser = new JsonParser().parse(data);
		JsonObject joUser = parser.getAsJsonObject();

		joUser.addProperty("_id", joUser.get("id").getAsString());
		joUser.remove("id");

		Gson gson = new Gson();
		BasicDBObject user = gson.fromJson(joUser, BasicDBObject.class);

		try {
			System.out.println(joUser);
			user.append("basvurular", new ArrayList<String>());

			new Thread(new Runnable() {
				@Override
				public void run() {

					ElasticSearchUtils.putUserDocument(joUser);
				}

			}).start();
			usercollection.insert(user);
			// ElasticSearchUtils.putUserDocument(joUser);

		} catch (DuplicateKey e) {

			user.remove("basvurular");
			BasicDBObject newdoc = new BasicDBObject().append("$set", user);
			BasicDBObject searchquery = new BasicDBObject().append("_id",
					joUser.get("_id").getAsString());

			usercollection.update(searchquery, newdoc);
			System.out.println("Bu adamý kaydetmiþiz");

		} finally {
			Cookie ck = new Cookie("linkedlnid", joUser.get("_id")
					.getAsString());
			response.addCookie(ck);
		}
	}

}
