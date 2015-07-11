package com.anilsevici.ilan;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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

		if (data != null) {

			Parser parse = new Parser(data);
			BasicDBObject ilan = parse.addObject();

			ilancollection.insert(ilan);
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
