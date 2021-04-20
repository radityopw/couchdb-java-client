package radityopw.couchdbclient;

import java.io.*;
import java.util.logging.*;
import org.json.*;

public class CouchdbClient{
	
	private static final Logger LOGGER = Logger.getLogger( CouchdbClient.class.getName() );

	private final CouchdbClientConfig config;
	private final String urlServer;
	private final String urlDatabase;
	private final String cmdTemplateServer;
	private final String cmdTemplateDatabase;
	private final String OS = System.getProperty("os.name").toLowerCase();
	
	/**
	*
	*menginisiasi couchdbclient 
	*@param config CouchdbClientConfig merupakan POJO class yang berisi attribut
	*String curl
	*String protocol
	*String host
	*String port
	*String database
	*String username
	*String password
	*untuk mengakses attribut tersebut, dapat menggunakan setter dan getter
	*/
	
	public CouchdbClient(CouchdbClientConfig config){
		this.config = config;
				
		this.urlServer = config.getProtocol()+"://"+config.getUsername()+":"+config.getPassword()+"@"+config.getHost()+":"+config.getPort();
		this.urlDatabase = urlServer+"/"+config.getDatabase();
		
		
		if(this.isWindows()){
			this.cmdTemplateServer = config.getCurl()+" -S -s -X {MODE} \""+urlServer+"/{OPERATION}\"";
			this.cmdTemplateDatabase = config.getCurl()+" -S -s -X {MODE} \""+urlDatabase+"/{OPERATION}\" {OPTIONAL}";
		}else{
			this.cmdTemplateServer = config.getCurl()+" -S -s -X {MODE} '"+urlServer+"/{OPERATION}'";
			this.cmdTemplateDatabase = config.getCurl()+" -S -s -X {MODE} '"+urlDatabase+"/{OPERATION}' {OPTIONAL}";
		}
		
		LOGGER.log( Level.INFO, "CouchdbClient Created", this );
	}
	
	private boolean isWindows() {
        return (OS.indexOf("win") >= 0);
    }

    private boolean isMac() {
        return (OS.indexOf("mac") >= 0);
    }

    private boolean isUnix() {
        return (OS.indexOf("nix") >= 0
                || OS.indexOf("nux") >= 0
                || OS.indexOf("aix") > 0);
    }

    private boolean isSolaris() {
        return (OS.indexOf("sunos") >= 0);
    }
	
	private String generateServerCommand(String mode,String operation){
		String cmd = cmdTemplateServer.replace("{MODE}",mode);
		cmd = cmd.replace("{OPERATION}",operation);
		return cmd;
	}
	private String generateDatabaseCommand(String mode,String operation){
		return generateDatabaseCommand(mode,operation,"");
	}
	private String generateDatabaseCommand(String mode,String operation,String optional){
		
		if(isWindows()){
			operation = operation.replace("\"","\\\"");
			optional = optional.replace("\"","\\\"");
		}
		String cmd = cmdTemplateDatabase.replace("{MODE}",mode);
		cmd = cmd.replace("{OPERATION}",operation);
		cmd = cmd.replace("{OPTIONAL}",optional);
		return cmd;
	}
	
	private JSONObject doCommand(String command) throws Exception{
		LOGGER.log( Level.INFO, "Running Command "+command);
		Process process = Runtime.getRuntime().exec(command);
		InputStream inputStream = process.getInputStream();
		
		InputStreamReader isReader = new InputStreamReader(inputStream);
		
		BufferedReader reader = new BufferedReader(isReader);
		StringBuffer sb = new StringBuffer();
		String str;
		while((str = reader.readLine())!= null){
			sb.append(str);
		}
		
		str = null;
		
		boolean isError = false;
		StringBuffer sbErr = new StringBuffer();
		InputStream errorStream = process.getErrorStream();
		InputStreamReader isReaderErr = new InputStreamReader(errorStream);
		BufferedReader readerError = new BufferedReader(isReaderErr);
		while((str = readerError.readLine())!= null){
			isError = true;
			sbErr.append(str);
		}
		
		if(isError){
			LOGGER.log( Level.SEVERE, sbErr.toString());
			LOGGER.log( Level.SEVERE, sb.toString());
			throw new Exception(sbErr.toString());
		}
		
		process.destroy();
		
		String cmd = sb.toString();
		
		return new JSONObject(cmd);
	}
	
	/**
	*mengembalikan JSONObject dari dokumen yang dihasilkan _all_docs
	*@return JSONObject yang merupakan dokumen json dari _all_docs
	*@throws Exception untuk semua error yang terjadi
	*@see <a href="https://docs.couchdb.org/en/stable/json-structure.html#all-database-documents">All Database Document</a>
	*/
	
	public JSONObject allDocs() throws Exception{
		
		JSONObject response = this.allDocs("include_docs=true");
		
		return response;
	}
	
	/**
	*mengembalikan JSONObject dari dokumen yang dihasilkan _all_docs
	*@param param , contoh parameter "include_docs=true"
	*@return JSONObject yang merupakan dokumen json dari _all_docs
	*@throws Exception untuk semua error yang terjadi
	*@see <a href="https://docs.couchdb.org/en/stable/json-structure.html#all-database-documents">All Database Document</a>
	*/
	
	public JSONObject allDocs(String param) throws Exception{
		String command = generateDatabaseCommand("GET","_all_docs?"+param);
		return this.doCommand(command);
	}
	
	/**
	*mengembalikan JSONObject dari dokumen yang dihasilkan _all_docs
	*dari sebuah partisi data
	*@param name dari partisi tersebut
	*@return JSONObject yang merupakan dokumen json dari _all_docs
	*@throws Exception untuk semua error yang terjadi
	*@see <a href="https://docs.couchdb.org/en/stable/partitioned-dbs/index.html">partition document</a>
	*/
	
	public JSONObject partition(String name) throws Exception{
		return this.partition(name,"include_docs=true");
	}
	
	/**
	*mengembalikan JSONObject dari dokumen yang dihasilkan _all_docs
	*dari sebuah partisi data
	*@param name dari partisi tersebut
	*@param param, contoh param
	tambahan "include_docs=true"
	*@return JSONObject yang merupakan dokumen json dari _all_docs
	*@throws Exception untuk semua error yang terjadi
	*@see <a href="https://docs.couchdb.org/en/stable/partitioned-dbs/index.html">partition document</a>
	*/
	
	public JSONObject partition(String name,String param)throws Exception{
		String command = generateDatabaseCommand("GET","_partition/"+name+"/_all_docs?"+param);
		return this.doCommand(command);
	}
	
	/**
	*mengembalikan JSONObject dari dokumen yang dihasilkan 
	*dari proses UpSert (Update Insert) document 
	*@param docId , jika dia menggunakan partisi pastikan key partisi ada di docId contoh "user:admin" dengan partisi user dan key admin 
	*@param doc, dokumen JSON yang dibagung menggunakan JSONObject
	*@return JSONObject yang merupakan dokumen json dari _all_docs
	*@throws Exception untuk semua error yang terjadi
	*@see <a href="https://docs.couchdb.org/en/stable/api/basics.html#api-basics">API Basic</a>
	*/
	
	public JSONObject setDoc(String docId,JSONObject doc) throws Exception{
		File file = File.createTempFile("couchdb_java_client",null);
		
		FileOutputStream fos = new FileOutputStream(file);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		//convert string to byte array
		byte[] bytes = doc.toString().getBytes();
		//write byte array to file
		bos.write(bytes);
		bos.close();
		fos.close();
		
		String command = generateDatabaseCommand("PUT","/"+docId," -d @"+file.getAbsolutePath());
		JSONObject result = doCommand(command);
		file.delete();
		return result;
	}
	
	/**
	*mengembalikan JSONObject dari dokumen yang dihasilkan 
	*dari proses penghapusan document 
	*@param docId , jika dia menggunakan partisi pastikan key partisi ada di docId contoh "user:admin" dengan partisi user dan key admin 
	*@return JSONObject yang merupakan dokumen json dari _all_docs
	*@throws Exception untuk semua error yang terjadi
	*@see <a href="https://docs.couchdb.org/en/stable/api/basics.html#api-basics">API Basic</a>
	*/
	
	public JSONObject delDoc(String docId) throws Exception{
		JSONObject doc = this.getDoc(docId);
		String rev = doc.getString("_rev");
		
		String command = generateDatabaseCommand("DELETE","/"+docId+"?rev="+rev);
		//System.out.println(command);
		return doCommand(command);
	}
	
	/**
	*mengembalikan JSONObject dari dokumen yang dihasilkan 
	*dari proses pengambilan 1  document 
	*@param docId , jika dia menggunakan partisi pastikan key partisi ada di docId contoh "user:admin" dengan partisi user dan key admin 
	*@return JSONObject yang merupakan dokumen json 
	*@throws Exception untuk semua error yang terjadi
	*@see <a href="https://docs.couchdb.org/en/stable/api/basics.html#api-basics">API Basic</a>
	*/
	
	public JSONObject getDoc(String docId) throws Exception{
		String command = generateDatabaseCommand("GET","/"+docId);
		return doCommand(command);
	}
}