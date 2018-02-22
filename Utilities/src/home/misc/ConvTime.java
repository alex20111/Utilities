package home.misc;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class ConvTime {

	/**
	 * Display time elapsed from a millisecond number
	 * example: Millisecond: 10000
	 * 			elapsed time: 00:00:10 (10 seconds)
	 * @param startDate	- the start date
	 * @param endDate	- the end date
	 * @return a string value of the time elapsed between the dates (00:00:00).
	 */
	public static String elapsedTime(long millisecond)
	{		
		if (millisecond > 0)
		{
			long elapsedTime = TimeUnit.MILLISECONDS.toSeconds(millisecond);// convert milli to seconds

			int secs = (int)elapsedTime;

			int hours = secs / 3600, 
					remainder = secs % 3600, 
					minutes = remainder / 60, 
					seconds = remainder % 60;	
			
			StringBuilder disHour = new StringBuilder((hours < 10 ? "0" : "") + hours + ":");
			disHour.append((minutes < 10 ? "0" : "") + minutes + ":");
			disHour.append((seconds < 10 ? "0" : "") + seconds);

			return disHour.toString();
		}
		return "00:00:00";
	}
	public static Date convertStringToDate(String date, String format) throws ParseException
	{
		//ex : "yyyy-MM-dd HH:mm"
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		if (date != null)
		{			
			return sdf.parse(date);		
		}
		else
		{
			throw new ParseException("Date string is empty",0);
		}
		
	}
}
