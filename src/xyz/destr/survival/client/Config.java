package xyz.destr.survival.client;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.UUID;

public class Config {

	public static final String CONFIG_FILE_NAME = "uuid.txt";
	public static UUID userUUID;
	
	public static void load() {
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(CONFIG_FILE_NAME)));
			try {
				userUUID = UUID.fromString(br.readLine());
				System.out.println("Config loaded");
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				br.close();
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static void save() {
		try {
			PrintWriter writer = new PrintWriter(new FileOutputStream(CONFIG_FILE_NAME));
			writer.println(userUUID.toString());
			writer.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
}
