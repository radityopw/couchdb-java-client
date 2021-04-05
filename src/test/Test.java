package test;

import radityopw.couchdbclient.*;
import java.io.*;
import java.util.logging.*;
import org.json.*;

public class Test{
	
	public static CouchdbClientConfig getConfig(){
		CouchdbClientConfig config = new CouchdbClientConfig();
		config.setDatabase("shared");
		
		return config;
	}
	
	public static void testUpsertData() throws Exception{
		
		boolean isError = false;
		String errorMessage = null;
		
		CouchdbClient client = new CouchdbClient(getConfig());
		JSONObject doc1 = new JSONObject("{\"_id\":\"user:coba\",\"username\":\"admin\",\"password\":\"0192023a7bbd73250516f069df18b500\",\"role\":\"admin\"}");
		JSONObject result1 = client.setDoc("user:coba",doc1);
		
		if(!result1.getString("id").equals("user:coba")){
			isError = true;
			errorMessage = "Test Upsert Data : id tidak sama";
		}
		
		if(!result1.getBoolean("ok")){
			isError = true;
			errorMessage = "Test Upsert Data : result tidak OK";
		}
		
		try{
			// error atau tidak, coba hapus saja data yang diinput tadi
			client.delDoc("user:coba");
			
		}catch(Exception e){}
		
		if(isError){
			throw new Exception(errorMessage);
		}
	}
	
	public static void testListSemuaData() throws Exception{
		boolean isError = false;
		String errorMessage = null;
		
		CouchdbClient client = new CouchdbClient(getConfig());
		
		JSONObject data = client.allDocs();
		
		if(data.getInt("total_rows") != 1){
			isError = true;
			errorMessage = "Test List Semua Data : jumlah baris tidak 1";
		}
		
		if(isError){
			throw new Exception(errorMessage);
		}
	}
	
	public static void testListPartition() throws Exception{
		boolean isError = false;
		String errorMessage = null;
		
		CouchdbClient client = new CouchdbClient(getConfig());
		
		JSONObject doc1 = new JSONObject("{\"_id\":\"user:coba\",\"username\":\"admin\",\"password\":\"0192023a7bbd73250516f069df18b500\",\"role\":\"admin\"}");
		JSONObject result1 = client.setDoc("user:coba",doc1);
		
		if(!result1.getString("id").equals("user:coba")){
			isError = true;
			errorMessage = "Test List Partition : id tidak sama";
		}
		
		if(!result1.getBoolean("ok")){
			isError = true;
			errorMessage = "Test List Partition : result tidak OK";
		}
		
		JSONObject data = client.partition("user");
		
		//System.out.println(data);
		
		if(data.getInt("total_rows") != 2){
			isError = true;
			errorMessage = "Test List Partition : jumlah baris tidak 2";
		}
		
		try{
			// error atau tidak, coba hapus saja data yang diinput tadi
			client.delDoc("user:coba");
			
		}catch(Exception e){}
		
		if(isError){
			throw new Exception(errorMessage);
		}
	}
	
	public static void testDeleteData() throws Exception{
		
		boolean isError = false;
		String errorMessage = null;
		
		CouchdbClient client = new CouchdbClient(getConfig());
		JSONObject doc1 = new JSONObject("{\"_id\":\"user:coba\",\"username\":\"admin\",\"password\":\"0192023a7bbd73250516f069df18b500\",\"role\":\"admin\"}");
		
		client.setDoc("user:coba",doc1);
		
		JSONObject result1 = client.delDoc("user:coba");
			
		if(!result1.getString("id").equals("user:coba")){
			isError = true;
			errorMessage = "Test Delete Data : id tidak sama";
		}
		
		if(!result1.getBoolean("ok")){
			isError = true;
			errorMessage = "Test Delete Data : result tidak OK";
		}
		
		if(isError){
			throw new Exception(errorMessage);
		}
	}
	
	public static void main(String[] a){
		
		try{
			
			testUpsertData();
			testListSemuaData();
			testListPartition();
			testDeleteData();
			
			System.out.println("OK tidak ada error");
			
		}catch(Exception e){
			System.out.println(e);
		}	
		
		/*
		
		try{
			
			
			
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
		
		*/
	}
}