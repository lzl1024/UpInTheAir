package api;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
		//byte[] result = null;
		ResultSet rs;
        try {
            Statement st = Constants.conn.createStatement();

            rs = st.executeQuery(Constants.queryPrefix + time + "\"");
            while (rs.next())
            {
                //Blob b = rs.getString(1);
                //result = b.getBytes(1, (int) b.length());
                builder.append(rs.getString(1));
            }
            //rs.close();
            //st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

		return builder.toString();
	}
}
