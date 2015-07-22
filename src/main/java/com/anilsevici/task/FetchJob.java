package com.anilsevici.task;

import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;

import com.anilsevici.mongodb.MongoDbUtils;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class FetchJob implements Runnable {

	public DBCollection ilancollection;

	public FetchJob() throws UnknownHostException {

		this.ilancollection = MongoDbUtils.getIlanCollection();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub

		List<DBObject> ilanlar = findByDates();
		Iterator<DBObject> ilaniterator = ilanlar.iterator();
		LocalDateTime localNow = LocalDateTime.now();

		while (ilaniterator.hasNext()) {
			DBObject ilan = ilaniterator.next();
			boolean publish = (Boolean) ilan.get("publish");

			if (publish) {

				String date = ilan.get("aktif").toString();
				String date2 = ilan.get("pasif").toString();

				DateTimeFormatter f = DateTimeFormatter
						.ofPattern("dd.MM.yyyy HH:mm:ss");
				LocalDateTime dateTime = LocalDateTime.from(f.parse(date));
				LocalDateTime dateTime2 = LocalDateTime.from(f.parse(date2));

				if (localNow.compareTo(dateTime) >= 0) {
					BasicDBObject newDocument = new BasicDBObject();
					newDocument.append("$set",
							new BasicDBObject().append("statu", true));

					BasicDBObject searchQuery = new BasicDBObject().append(
							"aktif", date);

					ilancollection.update(searchQuery, newDocument);
				}

				if (localNow.compareTo(dateTime2) >= 0) {
					BasicDBObject newDocument = new BasicDBObject();
					BasicDBObject fieldset = new BasicDBObject();

					fieldset.append("statu", false);
					fieldset.append("publish", false);

					newDocument.append("$set", fieldset);

					BasicDBObject searchQuery = new BasicDBObject().append(
							"pasif", date2);

					ilancollection.update(searchQuery, newDocument);
				}
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
