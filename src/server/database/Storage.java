package server.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

import com.google.gson.ExclusionStrategy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public abstract class Storage {

	private static final int BUFFERSIZE = 1024;
	
	public static <K, V> void backupNonCached(ExclusionStrategy strategy, File fileToBeStoredIn, Map<K, V> data) throws FileNotFoundException, IOException, NullPointerException {
		Objects.requireNonNull(strategy, "Exclusion strategy is null");
		Objects.requireNonNull(fileToBeStoredIn, "File to store data is null");
		Objects.requireNonNull(data, "Data to be stored into file is null");

		Gson gson = new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(strategy).create();
		
		fileToBeStoredIn.getParentFile().mkdirs();
		
		ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);
		int i = 0;
		
		try(FileOutputStream fos = new FileOutputStream(fileToBeStoredIn, false); FileChannel channel = fos.getChannel()){
			writeChar(channel, '[');
			for(Iterator<V> it = data.values().iterator(); it.hasNext(); i++) {
				V v = it.next();
				byte[] bytes = gson.toJson(v).getBytes();
				for(int offset = 0; offset < bytes.length; offset += BUFFERSIZE) {
					buffer.clear();
					buffer.put(bytes, offset, Math.min(BUFFERSIZE, bytes.length - offset));
					buffer.flip();
					while(buffer.hasRemaining()) 
						channel.write(buffer);
				}
				if(i < data.size() - 1) 
					writeChar(channel, ',');
			}
			writeChar(channel, ']');
		}
	}
	
	public static <T> void backupNonCached(ExclusionStrategy strategy, File fileToBeStoredIn, Collection<T> data) throws FileNotFoundException, IOException{
		Objects.requireNonNull(strategy, "Exclusion strategy is null");
		Objects.requireNonNull(fileToBeStoredIn, "File to store data is null");
		Objects.requireNonNull(data, "Data to be stored into file is null");
		
		if(data.isEmpty()) return;
		
		Gson gson = new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(strategy).create();
		
		fileToBeStoredIn.getParentFile().mkdirs();
		
		ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);
		int i = 0;
		
		try(FileOutputStream fos = new FileOutputStream(fileToBeStoredIn, false); FileChannel channel = fos.getChannel()){
			writeChar(channel, '[');
			for(Iterator<T> it = data.iterator(); it.hasNext(); i++) {
				T t = it.next();
				byte[] bytes = gson.toJson(t).getBytes();
				for(int offset = 0; offset < bytes.length; offset += BUFFERSIZE) {
					buffer.clear();
					buffer.put(bytes, offset, Math.min(BUFFERSIZE, bytes.length - offset));
					buffer.flip();
					while(buffer.hasRemaining())
						channel.write(buffer);
				}
				if(i < data.size() - 1)
					writeChar(channel, ',');
			}
			writeChar(channel, ']');
		}
	}
	
	public static <K, V> void backupCached(ExclusionStrategy strategy, File fileToBeStoredIn, Map<K, V> backedUpData, Map<K, V> toBeBackedUpData, boolean firtsBackupAndNonEmptyStorage) throws IOException {
		Objects.requireNonNull(strategy, "Exclusion strategy is null");
		Objects.requireNonNull(fileToBeStoredIn, "File to store data is null");
		Objects.requireNonNull(backedUpData, "File of backed up data is null");
		Objects.requireNonNull(toBeBackedUpData, "File of data to be backed up is null");
		
		if(toBeBackedUpData.isEmpty()) return;
		
		Gson gson = new GsonBuilder().setPrettyPrinting().addSerializationExclusionStrategy(strategy).create();
		ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);
		byte[] data = null;
		Path from = null;
		Path to = null;
		Scanner scanner = null;
		FileOutputStream fos = null;
		FileChannel c = null;
		
		if(!(backedUpData.isEmpty()) || firtsBackupAndNonEmptyStorage) {
			File copy = new File("copy-map.json");
			scanner = new Scanner(fileToBeStoredIn);
			fos = new FileOutputStream(copy);
			c = fos.getChannel();
			
			while(scanner.hasNextLine()) {
				String line = scanner.nextLine();
				if(!scanner.hasNextLine())
					line = line.substring(0, line.length() - 1) + "\n";
				else
					line = line + "\n";
				buffer.clear();
				data = line.getBytes(StandardCharsets.UTF_8);
				buffer.put(data);
				buffer.flip();
				while(buffer.hasRemaining()) c.write(buffer);
			}
			scanner.close();
			
			from = copy.toPath();
			to= fileToBeStoredIn.toPath();
			
			c.close();
			fos.close();
			
			Files.copy(from, to, StandardCopyOption.REPLACE_EXISTING);
			Files.delete(from);
		}
		
		fileToBeStoredIn.getParentFile().mkdirs();
		fos = new FileOutputStream(fileToBeStoredIn, true);
		c = fos.getChannel();
		
		if(backedUpData.isEmpty()) writeChar(c, '[');
		else writeChar(c, ',');
		
		int i = 0;
		
		for(Iterator<V> it = toBeBackedUpData.values().iterator(); it.hasNext(); i++) {
			V v = it.next();
			data = gson.toJson(v).getBytes();
			buffer.flip();
			buffer.clear();
			
			for(int offset = 0; offset < data.length; offset += BUFFERSIZE) {
				buffer.clear();
				buffer.put(data, offset, Math.min(BUFFERSIZE, data.length - offset));
				buffer.flip();
				while(buffer.hasRemaining()) c.write(buffer);
			}
			
			if(i < toBeBackedUpData.size() - 1) writeChar(c, ',');
		}
		writeChar(c, ']');
		
		c.close();
		fos.close();
	
	}

	private static void writeChar(FileChannel c, char d) throws IOException {
		CharBuffer charBuffer = CharBuffer.wrap(new char[] {d});
		ByteBuffer byteBuffer = StandardCharsets.UTF_8.encode(charBuffer);
		
		c.write(byteBuffer);
		
	}

}
