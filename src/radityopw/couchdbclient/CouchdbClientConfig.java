package radityopw.couchdbclient;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.Data;
import lombok.ToString;

@Data public class CouchdbClientConfig{
	private String curl = "curl"; 
	private String protocol = "http";
	private String host = "127.0.0.1";
	private String port = "5984";
	private String database;
	private String username = "admin";
	private String password = "admin123";
	
}