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
import com.anilsevici.sendemail.SendEmailUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Servlet implementation class BlackListServlet
 */
@WebServlet("/BlackListServlet")
public class BlackListServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection hrcollection;
	private final DBCollection usercollection;
	private final DBCollection blacklistcollection;

	/**
	 * @throws UnknownHostException
	 * @see HttpServlet#HttpServlet()
	 */
	public BlackListServlet() throws UnknownHostException {
		super();
		// TODO Auto-generated constructor stub

		hrcollection = MongoDbUtils.getHrIlanCollection();
		usercollection = MongoDbUtils.getUserCollection();
		blacklistcollection = MongoDbUtils.getBlackCollection();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String hrid = request.getParameter("hrid");
		String userid = request.getParameter("userid");
		String content = request.getParameter("content");
		String email = request.getParameter("email");

		BasicDBObject projectionquery = new BasicDBObject().append("_id", hrid);
		BasicDBObject includekeys = new BasicDBObject().append("ilanref", 1)
				.append("_id", 0);

		DBObject obj = hrcollection.findOne(projectionquery, includekeys);
		String bas = obj.get("ilanref").toString();

		Gson gson = new Gson();
		List<String> hrilanlar = gson.fromJson(bas,
				new TypeToken<List<String>>() {
				}.getType());

		for (String ilan : hrilanlar) {

			BasicDBObject searchquery = new BasicDBObject().append("_id",
					userid).append("basvurular.ilanno", ilan);

			BasicDBObject updatequery = new BasicDBObject().append("$set",
					new BasicDBObject().append("basvurular.$.statu",
							"blacklist"));

			usercollection.update(searchquery, updatequery);

		}

		BasicDBObject black = new BasicDBObject().append("_id", userid).append(
				"content", content);
		blacklistcollection.insert(black);

		new Thread(new Runnable() {
			@Override
			public void run() {

				SendEmailUtil.generateAndSendEmail(email, content);
			}

		}).start();

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
