package test;

import radityopw.couchdbclient.*;
import java.io.*;
import java.util.logging.*;
import org.json.*;

public class Test{
	public static void main(String[] a){
		
		try{
			
			Logger log = Logger.getLogger( CouchdbClient.class.getName() );
			ConsoleHandler handler = new ConsoleHandler();
			handler.setFormatter(new SimpleFormatter());
			handler.setLevel(Level.ALL);
			log.addHandler(handler);
			
			CouchdbClientConfig config = new CouchdbClientConfig();
			config.setDatabase("shared");
					
		
			CouchdbClient client = new CouchdbClient(config);
			
			System.out.println("semua data : ");
			
			JSONObject data = client.allDocs();
			
			System.out.println(data);
			
			System.out.println("memasukkan data : ");
			
			JSONObject docAdmin = client.getDoc("user:admin");
			System.out.println(docAdmin);
			
			JSONObject doc1 = new JSONObject("{\"_id\":\"user:coba\",\"username\":\"admin\",\"password\":\"0192023a7bbd73250516f069df18b500\",\"role\":\"admin\"}");
			System.out.println(doc1);
			
			JSONObject result1 = client.setDoc("user:coba",doc1);
			System.out.println(result1);
			
			System.out.println("melihat partisi");
			JSONObject resultPartition1 = client.partition("user");
			System.out.println(resultPartition1);
			
			System.out.println("menghapus data : ");
			
			JSONObject result2 = client.delDoc("user:coba");
			System.out.println(result2);
			
		}catch(Exception e){
			System.out.println("Error Terjadi!!");
			System.out.println(e);
		}
	}
}