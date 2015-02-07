package co;

/**
 * Represent the load generator which determines when new requests arrive.
 */
public interface Load {

	/**
	  * Returns the relative time of the next arrival (in nanoseconds precision), that is the time
	  * between two consecutive requests.
	  */
	public long nextRelativeTimeNs();
}