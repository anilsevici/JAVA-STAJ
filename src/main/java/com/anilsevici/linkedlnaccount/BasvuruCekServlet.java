package com.anilsevici.linkedlnaccount;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anilsevici.mongodb.MongoDbUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * Servlet implementation class BasvuruCekServlet
 */
@WebServlet("/BasvuruCekServlet")
public class BasvuruCekServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection usercollection;

	/**
	 * @throws UnknownHostException
	 * @see HttpServlet#HttpServlet()
	 */
	public BasvuruCekServlet() throws UnknownHostException {
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
		String userid = request.getParameter("userid");
		String ilanid = request.getParameter("ilanid");

		BasicDBObject search = new BasicDBObject().append("_id", userid);

		BasicDBObject idoc = new BasicDBObject().append("ilanno", ilanid);
		BasicDBObject odoc = new BasicDBObject().append("basvurular", idoc);
		BasicDBObject del = new BasicDBObject().append("$pull", odoc);

		usercollection.update(search, del);
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
