package api;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import util.Constants;

import com.sun.jersey.spi.resource.Singleton;


@Path("q2")
@Singleton
public class TextofTweets {

	/**
	 * TextofTweets request
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String tweets(@QueryParam("time") String time) {

		StringBuilder builder = new StringBuilder(Constants.ANS_TITLE);
		if (time != null) {
    		ResultSet rs;
            try {
            	if (Constants.conn.isClosed()) {
            		Constants.conn = DriverManager.getConnection(Constants.url, Constants.username,
            				Constants.password);
            	}	
            	
            	Statement st = Constants.conn.createStatement();
            	
                rs = st.executeQuery(Constants.queryPrefix + time + "\"");
                while (rs.next())
                {
                    builder.append(rs.getString(1));
                }
                rs.close();
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
		}

		return builder.toString();
	}
}
