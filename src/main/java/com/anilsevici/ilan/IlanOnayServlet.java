package com.anilsevici.ilan;

import java.io.IOException;
import java.net.UnknownHostException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.anilsevici.mongodb.MongoDbUtils;
import com.anilsevici.sendemail.SendEmailUtil;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * Servlet implementation class IlanOnayServlet
 */
@WebServlet("/IlanOnayServlet")
public class IlanOnayServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private final DBCollection usercollection;

	/**
	 * @throws UnknownHostException
	 * @see HttpServlet#HttpServlet()
	 */
	public IlanOnayServlet() throws UnknownHostException {
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
		String statu = request.getParameter("durum");
		String ilanid = request.getParameter("ilanid");
		String email = request.getParameter("email");

		String content = "Size suan olumlu yanýt veremiyoruz";
		String content2 = "Basvurunuz olumlu sonuclanmýstýr";

		BasicDBObject searchquery = new BasicDBObject().append("_id", userid)
				.append("basvurular.ilanno", ilanid);

		if (statu.equals("true")) {

			BasicDBObject updatequery = new BasicDBObject().append("$set",
					new BasicDBObject().append("basvurular.$.statu", "kabul"));
			SendEmailUtil.generateAndSendEmail(email, content2);

			usercollection.update(searchquery, updatequery);
		} else {

			BasicDBObject updatequery = new BasicDBObject().append("$set",
					new BasicDBObject().append("basvurular.$.statu", "red"));

			SendEmailUtil.generateAndSendEmail(email, content);

			usercollection.update(searchquery, updatequery);
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
