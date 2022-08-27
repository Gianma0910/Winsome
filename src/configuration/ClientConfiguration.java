package configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

import exceptions.InvalidConfigurationException;
import exceptions.InvalidPortNumberException;

/**
 * Class used to set the parameters for client that are used during the interaction with server.
 * @author Gianmarco Petrocchi.
 *
 */
public class ClientConfiguration {
	/** Server address */
	public InetAddress SERVERADDRESS;
	/** TCP port */
	public int TCPPORT;
	/** UDP port*/
	public int UDPPORT;
	/** Port where is located the RMI Registry */
	public int RMIREGISTRYPORT;
	/** Host where is located the RMI Registry*/
	public String RMIREGISTRYHOST;
	/** Name of the registration service offered by Registry*/
	public String REGISTRATIONSERVICENAME;
	/** Name of the follower callback service offered by Registry*/
	public String CALLBACKSERVICENAME;
	
	/**
	 * Basic constructor where all the parameters are set by reading a configuration file
	 * @param configurationFile Where there are parameters. Cannot be null
	 * @throws InvalidConfigurationException Occurs when there are some error during configuration of client
	 */
	public ClientConfiguration(File configurationFile) throws InvalidConfigurationException {
		try (InputStream input = new FileInputStream(configurationFile)){
			//load the configurationFile and close the FileInputStream
			Properties prop = new Properties();
			prop.load(input);
			input.close();
			
			//check if the configuration file contains all the client's parameters
			if(prop.containsKey("TCPPORT") && prop.containsKey("UDPPORT") && prop.containsKey("RMIREGISTRYPORT") && prop.containsKey("SERVERADDRESS")
					&& prop.containsKey("RMIREGISTRYHOST") && prop.containsKey("REGISTRATIONSERVICENAME") && prop.containsKey("CALLBACKSERVICENAME")) {

				//check the port
				try {
					TCPPORT = checkAndGetPortNumber(prop.getProperty("TCPPORT"));
					UDPPORT = checkAndGetPortNumber(prop.getProperty("UDPPORT"));
					RMIREGISTRYPORT = checkAndGetPortNumber(prop.getProperty("RMIREGISTRYPORT"));

					//check if some port has the same number
					if(TCPPORT == UDPPORT || TCPPORT == RMIREGISTRYPORT || UDPPORT == RMIREGISTRYPORT)
						throw new InvalidConfigurationException("Some port has the same number. Check the properties file");

				}catch(NumberFormatException e) {
					throw new InvalidConfigurationException("The specified port number is not a integer");
				}catch(InvalidPortNumberException e) {
					throw new InvalidConfigurationException("Invalid port number");
				}
				
				//check the server address
				try {
					SERVERADDRESS = InetAddress.getByName(prop.getProperty("SERVERADDRESS"));
				}catch (UnknownHostException e) {
					throw new InvalidConfigurationException(e.getMessage());
				}
				
				RMIREGISTRYHOST = prop.getProperty("RMIREGISTRYHOST");
				REGISTRATIONSERVICENAME = prop.getProperty("REGISTRATIONSERVICENAME");
				CALLBACKSERVICENAME = prop.getProperty("CALLBACKSERVICENAME");
				
			}else
				throw new InvalidConfigurationException("File properties doesn't contains all the necessary properties. Check the file and the documentation");
		}catch(IOException e) {
			System.err.println("File properties not found, specify another file path");			
		}
	}
	
	/**
	 * This method check if the port number is legal and then return an Integer that represents the port number
	 * @param portNumber Port number. Cannot be null
	 * @return An Integer that represents the port number
	 * @throws InvalidPortNumberException Occurs when the port number is illegal, it isn't belongs [1024, 65535]
	 * @throws NumberFormatException Occurs when the string does not contain a parsable integer
	 * @throws NullPointerException Occurs when the parameter is null
	 */
	private int checkAndGetPortNumber(String portNumber) throws InvalidPortNumberException, NumberFormatException, NullPointerException {
		
		if(portNumber == null) throw new NullPointerException("Parameter portNumber is null");
		
		int numPort = Integer.parseInt(portNumber); 
		
		if(numPort < 1024 || numPort > 65535)
			throw new InvalidPortNumberException("Port number in file properties is not valid, it must belongs [1024, 65535]");
	
		return numPort;
	}
	
}
