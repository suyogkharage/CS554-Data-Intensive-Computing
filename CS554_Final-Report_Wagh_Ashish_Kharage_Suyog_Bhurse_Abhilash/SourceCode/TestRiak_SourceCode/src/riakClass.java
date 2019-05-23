import java.io.File;
import java.io.FileInputStream;
import java.net.UnknownHostException;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.basho.riak.client.api.RiakClient;
import com.basho.riak.client.api.commands.kv.DeleteValue;
import com.basho.riak.client.api.commands.kv.FetchValue;
import com.basho.riak.client.api.commands.kv.StoreValue;
import com.basho.riak.client.api.commands.kv.UpdateValue;
import com.basho.riak.client.core.RiakCluster;
import com.basho.riak.client.core.RiakNode;
import com.basho.riak.client.core.query.Location;
import com.basho.riak.client.core.query.Namespace;

public class riakClass {
	public static class PatientUpdate extends UpdateValue.Update<Patient> {
		private final Patient update;

		public PatientUpdate(Patient update) {
			this.update = update;
		}

		@Override
		public Patient apply(Patient t) {
			if (t == null) {
				t = new Patient();
			}

			t.patientId = update.patientId;
			t.patientName = update.patientName;
			return t;
		}
	}

	public static void main(String[] args) throws Exception {

		// And now we can use our setUpCluster() function to create a cluster
		// object which we can then use to create a client object and then
		// execute our storage operation
		RiakCluster cluster = setUpCluster();
		RiakClient client = new RiakClient(cluster);

		Namespace patientBucket = new Namespace("patientDB");
		// Location patientLocation = new Location(patientBucket,"patientTable");
		Location patientLocation = null;
		System.out.println("Client object successfully created");

		String excelFilePath = "SamplePatientData_10000.xlsx";
		FileInputStream inputStream = new FileInputStream(new File(excelFilePath));

		Workbook workbook = new XSSFWorkbook(inputStream);
		org.apache.poi.ss.usermodel.Sheet firstSheet = workbook.getSheetAt(0);
		Iterator<Row> iterator = firstSheet.iterator();

		long startInsertTime = System.currentTimeMillis();
		int i = 1;
		while (iterator.hasNext()) {
			Row nextRow = iterator.next();
			Iterator<Cell> cellIterator = nextRow.cellIterator();
			Patient patient = new Patient();
			while (cellIterator.hasNext()) {
				Cell cell = cellIterator.next();
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_NUMERIC:
					patient.setPatientId(cell.getNumericCellValue());
					break;
				case Cell.CELL_TYPE_STRING:
					patient.setPatientName(cell.getStringCellValue());
					break;
				}
			}
			patientLocation = new Location(patientBucket, "key"+i+patient.patientName);
			StoreValue patientBucketOp = new StoreValue.Builder(patient).withLocation(patientLocation).build();
			client.execute(patientBucketOp);
			i++;
		}
		long stopInsertTime = System.currentTimeMillis();
		System.out.println("Time for inserting the records: " + (stopInsertTime - startInsertTime) + " milliseconds");


		long startSearchTime = System.currentTimeMillis();
		patientLocation = new Location(patientBucket, "key"+9978+"patient9978");
		FetchValue fetch = new FetchValue.Builder(patientLocation).build();
		Patient obj = client.execute(fetch).getValue(Patient.class);
		long stopSearchTime = System.currentTimeMillis();
		System.out.println(obj.patientName);
		System.out.println("Time for searching the record: " + (stopSearchTime - startSearchTime) + " milliseconds");

		long startUpdateTime = System.currentTimeMillis();
		obj.setPatientName("new9978");
		PatientUpdate updatedBook = new PatientUpdate(obj);
		UpdateValue updateValue = new UpdateValue.Builder(patientLocation).withUpdate(updatedBook).build();
		client.execute(updateValue);
		long stopUpdateTime = System.currentTimeMillis();
		System.out.println("Time for updating the record: " + (stopUpdateTime - startUpdateTime) + " milliseconds");

		long startDeleteTime = System.currentTimeMillis();
		DeleteValue deleteOp = new DeleteValue.Builder(patientLocation).build();
		client.execute(deleteOp);
		long stopDeleteTime = System.currentTimeMillis();
		System.out.println("Time for deleting the record: " + (stopDeleteTime - startDeleteTime) + " milliseconds");
	}

	private static RiakCluster setUpCluster() throws UnknownHostException {
		// This example will use only one node listening on localhost:10017
		RiakNode node = new RiakNode.Builder().withRemoteAddress("127.0.0.1").withRemotePort(8087).build();
		// This cluster object takes our one node as an argument
		RiakCluster cluster = new RiakCluster.Builder(node).build();
		// The cluster must be started to work, otherwise you will see errors
		cluster.start();
		return cluster;
	}

}
