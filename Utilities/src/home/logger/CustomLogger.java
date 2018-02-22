package home.logger;


import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import home.exception.LoggerException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.DailyRollingFileAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.varia.LevelRangeFilter;

//OFF
//FATAL
//ERROR
//WARN
//INFO
//DEBUG
//TRACE
//ALL
//
//Use:  Logger log = Logger.getLogger(className.class);
public class CustomLogger {

	public static final String FILE_MB = "MB";
	public static final String FILE_KB = "KB";
	public static final String FILE_GB = "GB";
	
	private static String PATTERN = "%-5p [%d{ISO8601}] [%t] %c (%F:%L) - %m%n";	
		
	/**
	 * Create a console logger to display the error or debug message into the console.
	 */
	public static void createConsole(String name, Level minLevel, Level maxLevel) throws LoggerException
	{
		//verify the inputs
		verifyInputs(name, "empty", minLevel, maxLevel, "empty");
		
		ConsoleAppender console = new ConsoleAppender(); 
		//configure the appender		
		console.setLayout(new PatternLayout(PATTERN));
		console.setName(name);
		
		LevelRangeFilter filter = new LevelRangeFilter();
		filter.setLevelMin(minLevel);
		filter.setLevelMax(maxLevel);		
		console.addFilter(filter);
		
		console.activateOptions();	
		console.setTarget("System.out");
		
		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(console);			
	}
	/**
	 * Create a file logger that will not change and re-used every time the program is started.
	 * Cannot set the limit on file size.
	 * 
	 * @param level
	 */
	public static void createStaticFileLogger(String name, String fileName, Level minLevel, Level maxLevel) throws LoggerException
	{
		verifyInputs(name, fileName, minLevel, maxLevel, "empty");
		
		FileAppender fa = new FileAppender();
		fa.setName(name);
		fa.setFile(fileName);
		fa.setLayout(new PatternLayout(PATTERN));
		
		//set range filter
		LevelRangeFilter filter = new LevelRangeFilter();
		filter.setLevelMin(minLevel);
		filter.setLevelMax(maxLevel);		
		fa.addFilter(filter);

		fa.setAppend(true);
		fa.activateOptions();

		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(fa);
	}
	/**
	 * Create a file logger with a maximum length. When max lenght reached, a new file will be created.
	 * @param level
	 */
	public static void createDiffLenghtFileLogger(String name,String fileName,Level minLevel, Level maxLevel, String length) throws LoggerException
	{				
		
		verifyInputs(name, fileName, minLevel, maxLevel, length);
		
		RollingFileAppender rfa = new RollingFileAppender();
		rfa.setName(name);
		rfa.setFile(fileName);
		rfa.setLayout(new PatternLayout(PATTERN));
		
		//set range filter
		LevelRangeFilter filter = new LevelRangeFilter();
		filter.setLevelMin(minLevel);
		filter.setLevelMax(maxLevel);		
		rfa.addFilter(filter);
		
		rfa.setMaxFileSize(length);	
		rfa.activateOptions();

		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(rfa);		
	}
	/**
	 * Logger will create a new file every day when written to it.
	 * @param level
	 */
	public static void createDailyFileLogger(String name,String fileName, Level minLevel, Level maxLevel) throws LoggerException
	{
		verifyInputs(name, fileName, minLevel, maxLevel, "empty");
		
		DailyRollingFileAppender daily = new DailyRollingFileAppender();
		daily.setName(name);
		daily.setFile(fileName);
		daily.setLayout(new PatternLayout(PATTERN));
		
		//set range filter
		LevelRangeFilter filter = new LevelRangeFilter();
		filter.setLevelMin(minLevel);
		filter.setLevelMax(maxLevel);		
		daily.addFilter(filter);
	
		daily.setDatePattern("yyyy-MM-dd");
		daily.activateOptions();

		//add appender to any Logger (here is root)
		Logger.getRootLogger().addAppender(daily);
	}
	
	/**
	 * Create a logger that will e-mail the admin or other
	 * @param level
	 */
	public static void createEmailLogger(String name, Level minLevel, Level maxLevel, String toEmail, String userName, String pass, String from, String subject) throws LoggerException
	{
		verifyEmailInputs(name, minLevel, maxLevel, toEmail, userName, pass, from, subject);
		
		GmailSMTPAppender gMailAppender = new GmailSMTPAppender();
		gMailAppender.setSMTPHost("smtp.gmail.com");
		gMailAppender.setSMTPDebug(false);
		gMailAppender.setBufferSize(512);
	
		//contact information
		gMailAppender.setFrom(from);
		gMailAppender.setTo(toEmail);
		gMailAppender.setSMTPUsername(userName);
		gMailAppender.setSMTPPassword(pass);
		gMailAppender.setSubject(subject);
		
		gMailAppender.setLayout(new PatternLayout("[%d{ISO8601}]%n%n%-5p%n%n%c%n%n%m%n%n"));
		
		//set range filter
		LevelRangeFilter filter = new LevelRangeFilter();
		filter.setLevelMin(minLevel);
		filter.setLevelMax(maxLevel);		
		gMailAppender.addFilter(filter);
		
		Logger.getRootLogger().addAppender(gMailAppender);		
	}
	
	/**
	 * set the root logger level
	 * 
	 * @param level
	 */
	public static void setRootLoggerLevel(Level level)
	{
		Logger.getRootLogger().setLevel(level);
	}
	
	/**
	 * Automatically create a logger for the development environment.
	 * create a console logger with debug to fatal
	 * create a static file logger with debug to fatal
	 */
	public static void createWebDevelopmentLogger(String fileName) throws LoggerException
	{
		if (fileName == null || fileName.length() == 0)
		{
			throw new LoggerException("File name needed for createWebDevelopmentLogger");
		}
		createConsole("Debug messages", Level.DEBUG, Level.FATAL);
		createStaticFileLogger("DevMode", fileName,Level.DEBUG, Level.FATAL );
		setRootLoggerLevel(Level.DEBUG);
	}
	/**
	 * Automatically create a logger for the development environment.
	 * create a console logger with debug to fatal
	 * create a static file logger with debug to fatal
	 */
	public static void createSwingDevelopmentLogger(String fileName) throws LoggerException
	{
		if (fileName == null || fileName.length() == 0)
		{
			throw new LoggerException("File name needed for createSwingDevelopmentLogger");
		}
		createConsole("Debug messages", Level.DEBUG, Level.FATAL);
		createStaticFileLogger("DevMode", fileName,Level.DEBUG, Level.FATAL );
		setRootLoggerLevel(Level.DEBUG);
	}
	/**
	 * Automatically create a logger for the production environment
	 * Level start at WARN.
	 * Send e-mail and write to file
	 * 
	 * @param fileName --> file name of the log
	 * @param toEmail --> Where to send the e-mail to.
	 * @param userName --> user name of the gmail account
	 * @param pass --> password of the  gmail account.
	 * @param from  --> the e-mail is from who?
	 * @param subject --> subject of the e-mail
	 * @throws LoggerException
	 */
	public static void createWebProductionLogger(String fileName, String toEmail, String userName, String pass, String from, String subject) throws LoggerException 
	{
		if (fileName == null || fileName.length() == 0)
		{
			throw new LoggerException("File name needed for createWebProductionLogger");
		}
		createDailyFileLogger("Prod", fileName ,Level.WARN, Level.FATAL );
		createEmailLogger("Prod email", Level.WARN, Level.FATAL, toEmail, userName, pass, from, subject);
		setRootLoggerLevel(Level.WARN);
	}
	/**
	 * Automatically create a logger for the Swing Application production environment
	 * Level start at WARN.
	 * 
	 * @param fileName --> file name of the log
	 * @throws LoggerException
	 */
	public static void createSwingProductionLogger(String fileName) throws LoggerException 
	{
		if (fileName == null || fileName.length() == 0)
		{
			throw new LoggerException("File name needed for createSwingProductionLogger");
		}
		createStaticFileLogger("ProdLogger", fileName, Level.WARN, Level.FATAL );	
		setRootLoggerLevel(Level.WARN);
	}
	
	/**
	 * calculate the file size input for the max size of the file appender.
	 * @param size
	 * @param suffix
	 * @return
	 */
	public static String calcFileSize(int size, String suffix)
	{
		String retVal = "";
		
		if ( ( FILE_GB.equals(suffix) || FILE_MB.equals(suffix) || FILE_KB.equals(suffix) ) && size > 0)
		{
			retVal = String.valueOf(size) + suffix;
		}
		else
		{
			//no value compatible, so set as 10mb
			retVal = "10" + FILE_MB;
		}
		return retVal;
	}
	
	/**
	 * 
	 * @param name	name of appender
	 * @param fileName --> file name if needed, else enter empty
	 * @param minLevel --> minLevel for the appender
	 * @param maxLevel --> max level for the appender
	 * @param length   --> size of the file , else enter empty.
	 * @throws LoggerException
	 */
	private static void verifyInputs(String name, String fileName ,Level minLevel, Level maxLevel, String length) throws LoggerException
	{
		if (fileName == null || fileName.length() == 0)
		{
			throw new LoggerException("File name needed");
		}
		if (name == null || name.length() == 0)
		{
			throw new LoggerException("name needed");
		}
		if (minLevel == null)
		{
			throw new LoggerException("Minimum level needed for the logger");
		}
		if (maxLevel == null )
		{
			throw new LoggerException("Maximum level needed for the logger");
		}
		if (length == null || length.length() == 0)
		{
			throw new LoggerException("File size needed");
		}
	}
	/**
	 * Make more verification for the e-mail appender
	 * @param name
	 * @param minLevel
	 * @param maxLevel
	 * @param toEmail
	 * @param userName
	 * @param pass
	 * @param from
	 * @param subject
	 * @throws LoggerException
	 */
	private static void verifyEmailInputs(String name, Level minLevel, Level maxLevel, String toEmail, String userName, String pass, String from, String subject) throws LoggerException
	{
		verifyInputs(name, "empty", minLevel, maxLevel, "empty");
		
		if (toEmail == null || toEmail.length() == 0)
		{
			throw new LoggerException("A TO email address needs to be provided.");
		}
		if (toEmail != null && toEmail.length() > 0)
		{
			try {
				InternetAddress internetAddress = new InternetAddress(toEmail);			
				internetAddress.validate();
			} catch (AddressException e) {
				throw new LoggerException("E-mail address : " + toEmail + " invalid.", e);
			}
			
			//verify if its a correct e-mail.			
			if (!toEmail.contains("@"))
			{
				throw new LoggerException("To E-mail address is not correct. Does not contains @ ");
			}						
		}
		if (userName == null || userName.length() == 0)
		{
			throw new LoggerException("User name is needed for the e-mail appender");
		}		
		if (pass == null || pass.length() == 0)
		{
			throw new LoggerException("Password is needed for the e-mail appender");
		}
		if (from == null || from.length() == 0)
		{
			throw new LoggerException("From address is needed for the e-mail appender");
		}
		if (subject == null || subject.length() == 0)
		{
			throw new LoggerException("Subject is needed for the e-mail appender");
		}
		
	}
}
