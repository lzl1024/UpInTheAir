package api;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import util.Constants;

@Path("q2")
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
		
		ResultSet rs;
        try {
            rs = Constants.st.executeQuery(Constants.queryPrefix + time + "\"");
            while (rs.next())
            {
              builder.append(rs.getString(Constants.cellName));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

		return builder.toString();
	}
}
