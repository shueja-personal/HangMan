package _04_HangMan;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import javax.swing.JOptionPane;

public class Utilities {
	public static String readRandomLineFromFile(String filename) {
		String word = "";

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			int randomNumber = new Random().nextInt(getTotalWordsInFile(filename));
			br.close();
			br = new BufferedReader(new FileReader(filename));
			for (int i = 0; i < randomNumber; i++) {
				word = br.readLine();
			}

			br.close();

		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Could not find file.", "ERROR", 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return word;
	}
	
	public static int getTotalWordsInFile(String filename) {
		int totalLines = 0;
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line = br.readLine();
			
			while (line != null) {
				totalLines++;
				line = br.readLine();
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return totalLines;
		
	}

	public static String readLineFromFile(String filename, int index){
		String word = "";

		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			br.close();
			br = new BufferedReader(new FileReader(filename));
			for (int i = 0; i < index; i++) {
				word = br.readLine();
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Could not find file.", "ERROR", 1);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return word;
	}
	public static int occurenceOf(char character, String str) {
		int count = str.length() - str.replace(character + "", "").length();
		return count;
	}
}
