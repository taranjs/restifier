package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * REST resource for retrieving list of prime numbers upto n.
 * 
 * Request format: http://localhost:16999/prime/10
 * 
 * Result format: {"initial": 10, "primes": [2,3,5,7], "processingTime": 0,
 * "cacheSize": 2}
 * 
 * @author taranjeet
 *
 */
public class Prime extends ServerResource {

	/**
	 * Cache for improving performance of subsequent requests.
	 */
	private static Map<Integer, String> cache = new HashMap<Integer, String>();

	/**
	 * Template for JSON response.
	 */
	private String responseJson = "{" + "\"initial\": _initial_, "
			+ "\"primes\": [_primes_], " + "\"processingTime\": _time_, "
			+ "\"cacheSize\": _cachesize_" + "}";

	/**
	 * Returns JSON response.
	 * 
	 * @return
	 */
	@Get("json")
	public String represent() {
		try {
			return getPrimeListWithMetadata(Integer.parseInt(getReference()
					.getLastSegment()));
		} catch (NumberFormatException nfe) {
			return nfe.toString();
		}
	}

	/**
	 * Tells whether a given number is prime or not.
	 * 
	 * @param num
	 * @return
	 */
	private boolean isPrime(int num) {
		int maxDivisor = num / 2;
		for (int i = 2; i <= maxDivisor; i++) {
			if (num % i == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns the list of prime numbers along with metadata in JSONized string.
	 * 
	 * @param primesUpto
	 * @return
	 */
	private String getPrimeListWithMetadata(int primesUpto) {
		long start = new Date().getTime();
		StringBuffer listOfPrimes = new StringBuffer();
		String result = "";

		if (cache.get(primesUpto) != null) {
			listOfPrimes = new StringBuffer(cache.get(primesUpto));
			result = listOfPrimes.toString();
		} else {

			int startFrom = 2;
			List<Integer> keys = new ArrayList<Integer>(cache.keySet());
			Collections.sort(keys);
			Collections.reverse(keys);

			for (int k : keys) {
				if (k < primesUpto) {
					// partial list of primes from cache
					listOfPrimes = new StringBuffer(cache.get(k));
					listOfPrimes.append(",");
					startFrom = k + 1;
					break;
				}
			}

			for (int i = startFrom; i <= primesUpto; i++) {
				if (isPrime(i)) {
					listOfPrimes.append(i + ",");
				}
			}
			listOfPrimes.deleteCharAt(listOfPrimes.length() - 1);
			cache.put(primesUpto, listOfPrimes.toString());
			result = listOfPrimes.toString();
		}
		
		// Total time taken
		long total = new Date().getTime() - start;

		return responseJson.replaceFirst("_initial_", primesUpto + "")
				.replaceFirst("_primes_", result)
				.replaceFirst("_time_", total + "")
				.replaceFirst("_cachesize_", cache.keySet().size() + "");
	}
}
