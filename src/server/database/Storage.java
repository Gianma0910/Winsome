package server.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;

public abstract class Storage {
	
	public static <K, V> void backupNonCached(ExclusionStrategy strategy, File fileToBeStoredIn, Map<K, V> data) throws FileNotFoundException, IOException, NullPointerException {
		Objects.requireNonNull(strategy, "Exclusion strategy is null");
		Objects.requireNonNull(fileToBeStoredIn, "File to store data is null");
		Objects.requireNonNull(data, "Data to be stored into file is null");

		Gson gson = new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(strategy).create();
		
		fileToBeStoredIn.getParentFile().mkdirs();
		
		FileOutputStream fos = new FileOutputStream(fileToBeStoredIn, false);
		String s = gson.toJson(data);
		fos.write(s.getBytes());
		fos.close();
	
	}
	
	public static <T> void backupNonCached(ExclusionStrategy strategy, File fileToBeStoredIn, Collection<T> data) throws FileNotFoundException, IOException{
		Objects.requireNonNull(strategy, "Exclusion strategy is null");
		Objects.requireNonNull(fileToBeStoredIn, "File to store data is null");
		Objects.requireNonNull(data, "Data to be stored into file is null");
		
		if(data.isEmpty()) return;
		
		Gson gson = new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(strategy).create();
		
		fileToBeStoredIn.getParentFile().mkdirs();
		
		FileOutputStream fos = new FileOutputStream(fileToBeStoredIn, false);
		String s = gson.toJson(data);
		fos.write(s.getBytes());
		fos.close();
	}
	
	public static <K, V> void backupCached(ExclusionStrategy strategy, File fileToBeStoredIn, Map<K, V> backedUpData, Map<K, V> toBeBackedUpData, boolean firtsBackupAndNonEmptyStorage) throws IOException {
		Objects.requireNonNull(strategy, "Exclusion strategy is null");
		Objects.requireNonNull(fileToBeStoredIn, "File to store data is null");
		Objects.requireNonNull(backedUpData, "File of backed up data is null");
		Objects.requireNonNull(toBeBackedUpData, "File of data to be backed up is null");
		
		if(toBeBackedUpData.isEmpty()) return;
		
		Gson gson = new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(strategy).create();
		
		Path from = null;
		Path to = null;
		FileOutputStream fos = null;
		FileInputStream fis = null;
		JsonReader reader = null;
		
		if(!(backedUpData.isEmpty()) || firtsBackupAndNonEmptyStorage) {
			File copy = new File("copy-map.json");
			fis = new FileInputStream(fileToBeStoredIn);
			fos = new FileOutputStream(copy);
			
			reader = new JsonReader(new InputStreamReader(fis));
			Type dataType = new TypeToken<Map<K, V>>(){} .getType();
			Map<K, V> dataInFile = gson.fromJson(reader, dataType);
			
			String s = gson.toJson(dataInFile);
			fos.write(s.getBytes());
			
			from = copy.toPath();
			to = fileToBeStoredIn.toPath();
			Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
			Files.delete(from);
			fos.close();
			fis.close();
		}
		
		fileToBeStoredIn.getParentFile().mkdirs();
		fos = new FileOutputStream(fileToBeStoredIn, true);
		String s = gson.toJson(toBeBackedUpData);
		fos.write(s.getBytes());
		
		fos.close();
		backedUpData.putAll(toBeBackedUpData);		

	}

}
