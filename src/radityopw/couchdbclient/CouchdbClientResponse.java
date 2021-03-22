package radityopw.couchdbclient;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.Data;
import lombok.ToString;
import org.json.*;

@Data public class CouchdbClientResponse{
	private JSONObject bodyJson;
	private String body;
	private String header;
	private int statusCode;
}