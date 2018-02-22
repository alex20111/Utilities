package home.misc;

import java.security.SecureRandom;

public class StringUtl {
	
	static final String str = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
	
	/**
	 * Compare 2 string. 
	 * 	If equal , return true
	 * 	If not equal , return false
	 * @param str1 - 1st string to compare
	 * @param str2 - 2nd string to compare.
	 * @return
	 */
	public static boolean compStr(String str1, String str2)
	{
		return (str1 == null || str2 == null ? false : str1.equals(str2));
	}

	public static boolean compStrIgnoreCase(String str1, String str2)
	{
		return (str1 == null || str2 == null ? false : str1.equalsIgnoreCase(str2));
	}
	/**
	 * verify that the string is not blank.
	 * 	String = ""; false
	 *  String = " "; false
	 *  String = " a "; true
	 *  String = null; false
	 * @param str
	 * @return
	 */
	public static boolean stringNotBlank(String str){

		if (str == null){
			return false;
		}
		if (str.trim().length() == 0){
			return false;			
		}
		if (str.trim().length() > 0){
			return true;
		}
		return false;
	}
	/** 
	 * <p>Compares two CharSequences, returning {@code true} if they represent 
	 * equal sequences of characters, ignoring case.</p> 
	 * 
	 * <p>{@code null}s are handled without exceptions. Two {@code null} 
	 * references are considered equal. Comparison is case insensitive.</p> 
	 * 
	 * <pre> 
	 * StringUtils.equalsIgnoreCase(null, null)   = true 
	 * StringUtils.equalsIgnoreCase(null, "abc")  = false 
	 * StringUtils.equalsIgnoreCase("abc", null)  = false 
	 * StringUtils.equalsIgnoreCase("abc", "abc") = true 
	 * StringUtils.equalsIgnoreCase("abc", "ABC") = true 
	 * </pre> 
	 * 
	 * @param str1  the first CharSequence, may be null 
	 * @param str2  the second CharSequence, may be null 
	 * @return {@code true} if the CharSequence are equal, case insensitive, or 
	 *  both {@code null} 
	 */ 

	public static boolean equalsIgnoreCase(final CharSequence str1, final CharSequence str2) { 
		if (str1 == null || str2 == null) { 
			return str1 == str2; 
		} else if (str1 == str2) { 
			return true; 
		} else if (str1.length() != str2.length()) { 
			return false; 
		} else { 
			return regionMatches(str1, true, 0, str2, 0, str1.length()); 
		} 
	} 
	/** 
	 * <p>Checks if CharSequence contains a search CharSequence irrespective of case, 
	 * handling {@code null}. Case-insensitivity is defined as by 
	 * {@link String#equalsIgnoreCase(String)}. 
	 * 
	 * <p>A {@code null} CharSequence will return {@code false}.</p> 
	 * 
	 * <pre> 
	 * StringUtils.contains(null, *) = false 
	 * StringUtils.contains(*, null) = false 
	 * StringUtils.contains("", "") = true 
	 * StringUtils.contains("abc", "") = true 
	 * StringUtils.contains("abc", "a") = true 
	 * StringUtils.contains("abc", "z") = false 
	 * StringUtils.contains("abc", "A") = true 
	 * StringUtils.contains("abc", "Z") = false 
	 * </pre> 
	 * 
	 * @param str  the CharSequence to check, may be null 
	 * @param searchStr  the CharSequence to find, may be null 
	 * @return true if the CharSequence contains the search CharSequence irrespective of 
	 * case or false if not or {@code null} string input 
	 */ 
	public static boolean containsIgnoreCase(final CharSequence str, final CharSequence searchStr) { 
		if (str == null || searchStr == null) { 
			return false; 
		} 
		final int len = searchStr.length(); 
		final int max = str.length() - len; 
		for (int i = 0; i <= max; i++) { 
			if (regionMatches(str, true, i, searchStr, 0, len)) { 
				return true; 
			} 
		} 
		return false; 
	} 
	/**
	 * Green implementation of regionMatches.
	 *
	 * @param cs the {@code CharSequence} to be processed
	 * @param ignoreCase whether or not to be case insensitive
	 * @param thisStart the index to start on the {@code cs} CharSequence
	 * @param substring the {@code CharSequence} to be looked for
	 * @param start the index to start on the {@code substring} CharSequence
	 * @param length character length of the region
	 * @return whether the region matched
	 */
	private static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart,
			final CharSequence substring, final int start, final int length)    {
		if (cs instanceof String && substring instanceof String) {
			return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
		}
		int index1 = thisStart;
		int index2 = start;
		int tmpLen = length;

		// Extract these first so we detect NPEs the same as the java.lang.String version
		final int srcLen = cs.length() - thisStart;
		final int otherLen = substring.length() - start;

		// Check for invalid parameters
		if (thisStart < 0 || start < 0 || length < 0) {
			return false;
		}

		// Check that the regions are long enough
		if (srcLen < length || otherLen < length) {
			return false;
		}

		while (tmpLen-- > 0) {
			final char c1 = cs.charAt(index1++);
			final char c2 = substring.charAt(index2++);

			if (c1 == c2) {
				continue;
			}

			if (!ignoreCase) {
				return false;
			}

			// The same check as in String.regionMatches():
			if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
					&& Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
				return false;
			}
		}
		return true;
	}
	/**
	 * generate a random String
	 * 
	 * @param length
	 * @return
	 */
	public static String randomString(int length){
		
		SecureRandom rnd = new SecureRandom();

		int strLen = str.length();

		   StringBuilder sb = new StringBuilder( length );
		   for( int i = 0; i < length; i++ ) 
		      sb.append( str.charAt( rnd.nextInt(strLen) ) );
		   return sb.toString();
	
	}
}