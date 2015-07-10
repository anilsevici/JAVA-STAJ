package com.anilsevici.services;

<<<<<<< HEAD
import java.io.IOException;
=======
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Enumeration;

>>>>>>> 563ac6592ff9836ed1454ad2073e1e98bdde2ff0
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

<<<<<<< HEAD
=======
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
>>>>>>> 563ac6592ff9836ed1454ad2073e1e98bdde2ff0
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * Servlet implementation class IlanServlet
 */
@WebServlet("/IlanServlet")
public class IlanServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection ilancollection;

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

<<<<<<< HEAD
		Parser parse = new Parser(data);
		BasicDBObject ilan = parse.addObject();

		ilancollection.insert(ilan);
=======
		/*JsonParser parser = new JsonParser();
		JsonObject joUser = new JsonObject();

		joUser = parser.parse(data).getAsJsonObject();

		String ilan_id = joUser.get("ilanid").getAsString();
		String title = joUser.get("title").getAsString();
		String def = joUser.get("definition").getAsString();
		String des = joUser.get("description").getAsString();

		BasicDBObject ilan = new BasicDBObject();
		ilan.append("_id", ilan_id).append("title", title)
				.append("definition", def).append("description", des);

		ilancollection.insert(ilan);*/
>>>>>>> 563ac6592ff9836ed1454ad2073e1e98bdde2ff0
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
