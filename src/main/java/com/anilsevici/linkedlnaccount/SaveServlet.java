package com.anilsevici.linkedlnaccount;

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
import com.mongodb.DBObject;

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
		String userid = request.getParameter("userid");
		String ilanid = request.getParameter("ilanid");

		BasicDBObject blacklistquery = new BasicDBObject()
				.append("_id", userid).append(
						"basvurular",
						new BasicDBObject().append("$elemMatch",
								new BasicDBObject().append("ilanno", ilanid)
										.append("statu", "blacklist")));

		BasicDBObject duplicatequery = new BasicDBObject()
				.append("_id", userid).append("basvurular.ilanno", ilanid);

		DBObject b = usercollection.findOne(blacklistquery);
		DBObject d = usercollection.findOne(duplicatequery);

		if (b != null)
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		else if (d != null)
			response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		else {

			String content = "Basvurunuz isleme alinmistir.En kisa zamanda geri donus yapilacaktir.";

			BasicDBObject obj = new BasicDBObject("ilanno", ilanid).append(
					"statu", "bekleme");
			BasicDBObject listItem = new BasicDBObject("basvurular", obj);

			BasicDBObject searchQuery = new BasicDBObject("_id", userid);
			BasicDBObject updateQuery = new BasicDBObject("$push", listItem);

			DBObject person = usercollection.findOne(searchQuery);
			usercollection.update(searchQuery, updateQuery);

			SendEmailUtil.generateAndSendEmail(person.get("emailAddress")
					.toString(), content);
		}

	}
}
