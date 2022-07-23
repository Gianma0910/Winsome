import java.io.File;

import UI.RegWindow;
import configuration.ClientConfiguration;
import exceptions.InvalidConfigurationException;

public class ClientMain {

	public static void main(String[] args) throws InvalidConfigurationException {
		if(args.length != 1) {
			System.err.println("Usage: java ClientMain <path configuration file>\n");
			System.err.println("Check the documentation\n");
			System.exit(0);
		}
		
		String pathConfigurationFile = args[0];
		File configurationFile = new File(pathConfigurationFile);
		
		ClientConfiguration clientConf = new ClientConfiguration(configurationFile);
		
		System.out.println("File properties read successfully\n");
		
		RegWindow regWindow = new RegWindow(clientConf);
		
		regWindow.setVisible(true);
	}
	
}
