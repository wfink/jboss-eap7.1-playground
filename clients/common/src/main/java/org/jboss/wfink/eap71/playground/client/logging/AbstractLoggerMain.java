package org.jboss.wfink.eap71.playground.client.logging;

import java.util.ArrayList;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base class for client test to initialize logging.
 * 
 * @author <a href="mailto:wfink@redhat.com">Wolf-Dieter Fink</a>
 */
public abstract class AbstractLoggerMain {
	public static final Logger log = Logger.getLogger(AbstractLoggerMain.class.getName());
	
	protected static String server = "localhost:8080";
	protected static String user = null;
	protected static String passwd = null;
	
	static private void setLogging(int logging) {
		Level logLevel = getLogLevel(logging%10);

		Logger.getLogger("org.jboss.wfink.eap71.playground").setLevel(logLevel);

		logLevel = getLogLevel((logging/10)%10);
		Logger.getLogger("org.xnio").setLevel(logLevel);
		Logger.getLogger("org.jboss.remoting").setLevel(logLevel);	//classes are remoting3
		
		logLevel = getLogLevel((logging/100)%10);
		Logger.getLogger("org.jboss.ejb.client").setLevel(logLevel);
		
		logLevel = getLogLevel((logging/1000)%10);
		Logger.getLogger("org.wildfly").setLevel(logLevel);
		
//		Logger.getLogger("org.jboss.invocation").setLevel(logLevel);

		// change default ConsoleHandler
		Logger root = Logger.getLogger("");
		Handler[] handlers = root.getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			root.removeHandler(handlers[i]);
		}
		ConsoleHandler handler = new ConsoleHandler();
		handler.setFormatter(new PlaygroundFormatter());
		handler.setLevel(Level.ALL);
		root.addHandler(handler);
	}
	
	static private Level getLogLevel(int level) {
		switch (level) {
		case 0:
			return Level.OFF;
		case 1:
			return Level.WARNING;
		case 2:
			return Level.INFO;
		case 3:
			return Level.FINE;
		case 4:
			return Level.FINER;
		case 9:
			return Level.ALL;
		default:
			return Level.FINEST;
		}
	}
	
	public static final void checkArgs(String[] args) {
		ArrayList<String> newArgs = new ArrayList<>();
		boolean isLoggingSet = false;
		
		for (int i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-log":
				setLogging(Integer.parseInt(args[++i]));
				isLoggingSet = true;
				break;

			case "-server":
				server = args[++i];
				isLoggingSet = true;
				break;

			case "-user":
				user = args[++i];
				break;

			case "-password":
				passwd = args[++i];
				break;

			default:
				newArgs.add(args[i]);
			}
		}
		if(newArgs.size() > 0)
			log.severe("not all arguments parsed " + newArgs);

		if(user != null && passwd == null)
			log.severe("User can not be set without password!");
		
		if(!isLoggingSet)
			setLogging(9);
	}
}
