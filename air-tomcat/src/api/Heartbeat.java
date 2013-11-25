package api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import util.Constants;

@Path("q1")
public class Heartbeat {
	/**
	 * Heartbeat request
	 * 
	 * @return
	 */
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String heartBeat() {
		return Constants.time;
	}
}
