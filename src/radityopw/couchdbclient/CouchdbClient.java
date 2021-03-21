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
	
	public CouchdbClient(CouchdbClientConfig config){
		this.config = config;
				
		this.urlServer = config.getProtocol()+"://"+config.getUsername()+":"+config.getPassword()+"@"+config.getHost()+":"+config.getPort();
		this.urlDatabase = urlServer+"/"+config.getDatabase();
		
		
		if(this.isWindows()){
			this.cmdTemplateServer = config.getCurl()+" -X {MODE} \""+urlServer+"/{OPERATION}\"";
			this.cmdTemplateDatabase = config.getCurl()+" -X {MODE} \""+urlDatabase+"/{OPERATION}\" {OPTIONAL}";
		}else{
			this.cmdTemplateServer = config.getCurl()+" -X {MODE} '"+urlServer+"/{OPERATION}'";
			this.cmdTemplateDatabase = config.getCurl()+" -X {MODE} '"+urlDatabase+"/{OPERATION}' {OPTIONAL}";
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
		String cmd = cmdTemplateDatabase.replace("{MODE}",mode);
		cmd = cmd.replace("{OPERATION}",operation);
		cmd = cmd.replace("{OPTIONAL}",optional);
		return cmd;
	}
	
	public JSONObject doCommand(String command) throws Exception{
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
		
		
		//int exitCode = process.exitValue();
		//System.out.println("exit value = "+exitCode);
		process.destroy();
		
		String cmd = sb.toString();
		
		return new JSONObject(cmd);
	}
	
	public JSONObject allDocs() throws Exception{
		
		JSONObject response = this.allDocs("include_docs=true");
		
		return response;
	}
	
	public JSONObject allDocs(String param) throws Exception{
		String command = generateDatabaseCommand("GET","_all_docs?"+param);
		return this.doCommand(command);
	}
	
	public JSONObject partition(String name) throws Exception{
		return this.partition(name,"include_docs=true");
	}
	
	public JSONObject partition(String name,String param)throws Exception{
		String command = generateDatabaseCommand("GET","_partition/"+name+"/_all_docs?"+param);
		return this.doCommand(command);
	}
	
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
	
	public JSONObject delDoc(String docId) throws Exception{
		JSONObject doc = this.getDoc(docId);
		String rev = doc.getString("_rev");
		
		String command = generateDatabaseCommand("DELETE","/"+docId+"?rev="+rev);
		//System.out.println(command);
		return doCommand(command);
	}
	
	public JSONObject getDoc(String docId) throws Exception{
		String command = generateDatabaseCommand("GET","/"+docId);
		return doCommand(command);
	}
	
	
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
			System.out.println(e);
		}
	}
	

}