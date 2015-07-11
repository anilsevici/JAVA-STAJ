package com.anilsevici.services;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class FetchJob implements Runnable {

	public DBCollection ilancollection;

	public FetchJob() throws UnknownHostException {

		MongoClient mongoClient = new MongoClient(new MongoClientURI(
				"mongodb://localhost"));
		DB ilanDatabase = mongoClient.getDB("kariyer");
		this.ilancollection = ilanDatabase.getCollection("ilan");

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		List<DBObject> ilanlar = findByDates();
		Iterator<DBObject> ilaniterator = ilanlar.iterator();
		LocalDateTime localNow = LocalDateTime.now();

		while (ilaniterator.hasNext()) {
			DBObject ilan = ilaniterator.next();
			String date = ilan.get("aktif").toString();

			DateTimeFormatter f = DateTimeFormatter
					.ofPattern("dd.MM.yyyy HH:mm:ss");
			LocalDateTime dateTime = LocalDateTime.from(f.parse(date));

			if (localNow.compareTo(dateTime) >= 0) {
				BasicDBObject newDocument = new BasicDBObject();
				newDocument.append("$set",
						new BasicDBObject().append("statu", true));

				BasicDBObject searchQuery = new BasicDBObject().append("aktif",
						date);

				ilancollection.update(searchQuery, newDocument);
			}
		}

	}

	public List<DBObject> findByDates() {

		List<DBObject> ilanlar;
		DBCursor cursor = ilancollection.find();
		try {
			ilanlar = cursor.toArray();
		} finally {
			cursor.close();
		}
		return ilanlar;
	}

}
