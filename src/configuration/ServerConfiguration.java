package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import exceptions.IllegalFileException;
import exceptions.InvalidConfigurationException;
import exceptions.InvalidPortNumberException;

/**
 * Class used to set the parameters for server that are used during the interaction with client.
 * @author Gianmarco Petrocchi.
 */
public class ServerConfiguration {
	/** Server address.*/
	public InetAddress SERVERADDRESS;
	/** TCP port.*/
	public int TCPPORT;
	/** UDP port.*/
	public int UDPPORT;
	/** Port where is located the RMI Registry.*/
	public int RMIREGISTRYPORT;
	/** Host where is located the RMI Registry.*/
	public String RMIREGISTRYHOST;
	/** Multicast port. */
	public int MULTICASTPORT;
	/** Multicast address. */
	public InetAddress MULTICASTADDRESS;
	/** Name of the registration service offered by Registry.*/
	public String REGISTRATIONSERVICENAME;
	/** Name of follower callback service offered by Registry.*/
	public String CALLBACKSERVICENAME;
	/** Delay of calculation earnings. */
	public int DELAYEARNINGSCALCULATION;
	/** Delay of database backup.*/
	public int DELAYBACKUP;
	/** Delay of thread pool shutdown. */
	public long DELAYSHUTDOWNTHREADPOOL;
	/** Author percentage earn used to distributed the earn for a post.*/
	public float AUTHORPERCENTAGEEARN;
	/** Directory where files will be created to storage database data.*/
	public String DIRECTORYFORFILE;
	/** Name of the file where there are stored all the users of winsome.*/
	public String USERSFILENAMEPATH = "users.json";
	/** Name of the file where there are stored all the posts created in winsome.*/
	public String POSTSFILENAMEPATH = "posts.json";
	/** Name of the file where there are stored all the posts' mutable data. */
	public String MUTABLEDATAPOSTSFILENAMEPATH = "mutable_data.json";
	/** Name of the file where there are stored all the posts' comments.*/
	public String COMMENTSFILENAMEPATH = "comments.json";
	/** Name of the file where there are stored all the posts' votes.*/
	public String VOTESFILENAMEPATH = "votes.json";
	/** Name of the file where there are stored all the transactions made by the server to distributed the earns.*/
	public String TRANSACTIONSFILENAMEPATH = "transactions.json";
	/** Name of the file where there are stored all the users that a certain user x is following in Winsome. */
	public String FOLLOWINGFILENAMEPATH = "following.json";
	
	/**
	 * Basic constructor where all the parameters are set by reading a configuration file.
	 * @param configurationFile Where there are parameters. Cannot be null.
	 * @throws InvalidConfigurationException Occurs when there are some error during configuration of server.
	 */
	public ServerConfiguration(File configurationFile) throws InvalidConfigurationException{
		try {
			//load the configuration file
			InputStream input = new FileInputStream(configurationFile);
			Properties prop = new Properties();
			prop.load(input);
			input.close();
			
			//check if the configuration file contains all the server's parameters
			if(prop.containsKey("SERVERADDRESS") && prop.containsKey("TCPPORT") && prop.containsKey("UDPPORT") && prop.containsKey("RMIREGISTRYPORT")
			   && prop.containsKey("RMIREGISTRYHOST") && prop.containsKey("MULTICASTPORT") && prop.containsKey("MULTICASTADDRESS") && prop.containsKey("REGISTRATIONSERVICENAME")
			   && prop.containsKey("CALLBACKSERVICENAME") && prop.containsKey("DELAYBACKUP") && prop.containsKey("DELAYEARNINGSCALCULATION")
			   && prop.containsKey("AUTHORPERCENTAGEEARN") && prop.containsKey("DIRECTORYFORFILE") && prop.containsKey("DELAYSHUTDOWNTHREADPOOL")) {
				
				//check the port
				try {
					TCPPORT = checkAndGetPortNumber(prop.getProperty("TCPPORT"));
					UDPPORT = checkAndGetPortNumber(prop.getProperty("UDPPORT")); 
					RMIREGISTRYPORT = checkAndGetPortNumber(prop.getProperty("RMIREGISTRYPORT"));
					MULTICASTPORT = checkAndGetPortNumber(prop.getProperty("MULTICASTPORT"));

					//check if some port has the same port number
					if(TCPPORT == UDPPORT || TCPPORT == RMIREGISTRYPORT || TCPPORT == MULTICASTPORT
					   || UDPPORT == RMIREGISTRYPORT || UDPPORT == MULTICASTPORT || RMIREGISTRYPORT == MULTICASTPORT)
						throw new InvalidConfigurationException("Some port has the same number. Check the properties file");
					
				}catch (InvalidPortNumberException e) {
					throw new InvalidConfigurationException("Invalid port number");
				}catch(NumberFormatException e) {
					throw new InvalidConfigurationException("The specified port number is not a integer");
				}

				//check the server address
				try {
					SERVERADDRESS = InetAddress.getByName(prop.getProperty("SERVERADDRESS"));					
				}catch(UnknownHostException e){
					throw new InvalidConfigurationException(e.getMessage());
				}
				
				//check the multicast address
				try {
					MULTICASTADDRESS = InetAddress.getByName(prop.getProperty("MULTICASTADDRESS"));					
				}catch(UnknownHostException e) {
					throw new InvalidConfigurationException(e.getMessage());
				}
				//check if the multicast address is legal
				if(!MULTICASTADDRESS.isMulticastAddress()) throw new InvalidConfigurationException("The specified address isn't multicast");
				
				//check the delay of backup
				try {
					DELAYBACKUP = Integer.parseInt(prop.getProperty("DELAYBACKUP"));
				}catch(NumberFormatException e) {
					throw new InvalidConfigurationException(e.getMessage());
				}
				
				//check the delay for calculation of earn
				try {
					DELAYEARNINGSCALCULATION = Integer.parseInt(prop.getProperty("DELAYEARNINGSCALCULATION"));
				}catch(NumberFormatException e) {
					throw new InvalidConfigurationException(e.getMessage());
				}
				
				//check the author percentage earn
				try {
					AUTHORPERCENTAGEEARN = Float.parseFloat(prop.getProperty("AUTHORPERCENTAGEEARN"));
				}catch(NumberFormatException e) {
					throw new InvalidConfigurationException(e.getMessage());
				}
				
				try {
					DIRECTORYFORFILE = prop.getProperty("DIRECTORYFORFILE");
					checkIsDirectory(DIRECTORYFORFILE);
				}catch(IllegalFileException e) {
					throw new InvalidConfigurationException(e.getMessage());
				}
				
				RMIREGISTRYHOST = prop.getProperty("RMIREGISTRYHOST");
				REGISTRATIONSERVICENAME = prop.getProperty("REGISTRATIONSERVICENAME");
				CALLBACKSERVICENAME = prop.getProperty("CALLBACKSERVICENAME");
				
				USERSFILENAMEPATH = DIRECTORYFORFILE.concat("/").concat(USERSFILENAMEPATH);
				POSTSFILENAMEPATH = DIRECTORYFORFILE.concat("/").concat(POSTSFILENAMEPATH);
				VOTESFILENAMEPATH = DIRECTORYFORFILE.concat("/").concat(VOTESFILENAMEPATH);
				COMMENTSFILENAMEPATH = DIRECTORYFORFILE.concat("/").concat(COMMENTSFILENAMEPATH);
				FOLLOWINGFILENAMEPATH = DIRECTORYFORFILE.concat("/").concat(FOLLOWINGFILENAMEPATH);
				MUTABLEDATAPOSTSFILENAMEPATH = DIRECTORYFORFILE.concat("/").concat(MUTABLEDATAPOSTSFILENAMEPATH);
				TRANSACTIONSFILENAMEPATH = DIRECTORYFORFILE.concat("/").concat(TRANSACTIONSFILENAMEPATH);
				
				DELAYSHUTDOWNTHREADPOOL = Long.parseLong(prop.getProperty("DELAYSHUTDOWNTHREADPOOL"));
				
			}else {
				throw new InvalidConfigurationException("File properties doesn't contains all the necessary properties. Check the file and the documentation");
			}
		}catch(IOException e) {
			System.err.println("File properties not found, specify another file path");
		}
	}
	
	/**
	 * This method check is the port number is legal and then return an Integer that represents the port number.
	 * @param portNumber Port number. Cannot be null.
	 * @return An Integer that represents the port number.
	 * @throws InvalidPortNumberException Occurs when the port number is illegal, it isn't belongs [1024, 65535].
	 * @throws NumberFormatException Occurs when the string does not contains a parsable integer.
	 * @throws NullPointerException Occurs when the parameter is null.
	 */
	private int checkAndGetPortNumber(String portNumber) throws InvalidPortNumberException, NumberFormatException, NullPointerException{
		
		if(portNumber == null) throw new NullPointerException("Parameter portNumber is null");
		
		int numPort = Integer.parseInt(portNumber);
		
		if(numPort < 1024 || numPort > 65535)
			throw new InvalidPortNumberException("Port number in file properties is not valid, it must belongs [1024, 65535]");
	
		return numPort;
	}
	
	/** 
	 * This method return the multicast info for client.
	 * @return Multicast info for client (MULTICAST_ADDRESS, MULTICAST_PORT).
	 */
	public String getMulticastInfo() {
		return MULTICASTADDRESS.toString() + ":" + MULTICASTPORT;
	}
	
	private void checkIsDirectory(String dir) throws IllegalFileException{
		File directory = new File(dir);
		if(directory.isDirectory() == false)
			throw new IllegalFileException("The path specified in server configuration file isn't a valid directory");
		
		return;
	}
	
}
