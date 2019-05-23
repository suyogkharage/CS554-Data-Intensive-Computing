import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndUpdateOptions;

public class mongoClass {
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {

		MongoClient mongoClient = new MongoClient("localhost", 27017);

		MongoDatabase database = mongoClient.getDatabase("patientDB");
		MongoCollection<org.bson.Document> collection = database.getCollection("patientTable");
		// create documents and insert
		String excelFilePath = "SamplePatientData_130000.xlsx";
		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

		Workbook workbook = new XSSFWorkbook(inputStream);
		org.apache.poi.ss.usermodel.Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();

		double patientId = 0;
		String patientName = null;
		long startInsertTime = System.currentTimeMillis();
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();

			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();

				switch (cell.getCellType()) {

				case Cell.CELL_TYPE_NUMERIC:
					patientId = cell.getNumericCellValue();
					break;

				case Cell.CELL_TYPE_STRING:
					patientName = cell.getStringCellValue();
					break;

				}
			}
			Document document = new Document().append("patientId", (int) patientId).append("patientName", patientName);
			collection.insertOne(document);

		}
		long stopInsertTime = System.currentTimeMillis();

		System.out.println("Time for inserting the records: " + (stopInsertTime - startInsertTime) + " milliseconds");
		long startFetchTime = System.currentTimeMillis();
		FindIterable<org.bson.Document> iterDoc = collection.find();
		int i = 1;
		Iterator it = iterDoc.iterator();
		while (it.hasNext()) {
			it.next();
			i++;
		}
		long stopFetchTime = System.currentTimeMillis();
		System.out.println(
				"Time for fetching the " + i + " records: " + (stopFetchTime - startFetchTime) + " milliseconds");

		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("patientId", 9978);
		long startSearchTime = System.currentTimeMillis();
		iterDoc = collection.find(whereQuery);
		it = iterDoc.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
		long stopSearchTime = System.currentTimeMillis();
		System.out.println("Time for searching the  record: " + (stopSearchTime - startSearchTime) + " milliseconds");

		Document NewDocument = new Document().append("patientId", 999999999).append("patientName", "newName999999999");

		long startUpdateTime = System.currentTimeMillis();
		BasicDBObject query = new BasicDBObject();
		query.append("patientId", 9978);
		BasicDBObject doc = BasicDBObject.parse(NewDocument.toJson());
		Bson newDocument = new Document("$set", doc);
		Document updatedDoc = collection.findOneAndUpdate(query, newDocument,
				(new FindOneAndUpdateOptions()).upsert(true));
		long stopUpdateTime = System.currentTimeMillis();
		System.out.println("Updated Doc: " + updatedDoc);
		System.out.println("Time for updating the  record: " + (stopUpdateTime - startUpdateTime) + " milliseconds");

		long startDeleteTime = System.currentTimeMillis();
		Document deletedDoc = collection.findOneAndDelete(new Document().append("patientId", 999999999));
		long stopDeleteTime = System.currentTimeMillis();
		System.out.println("Deleted Doc" + deletedDoc);
		System.out.println("Time for deleting the  record: " + (stopDeleteTime - startDeleteTime) + " milliseconds");
		
		workbook.close();
		mongoClient.close();

	}
}
