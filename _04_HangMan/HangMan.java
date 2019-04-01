package _04_HangMan;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.util.function.Predicate;
import java.util.Collections;
import java.util.Map.Entry;

public class HangMan implements KeyListener{
	Stack<String> words = new Stack<String>();
	String dictPath = "_04_HangMan/dictionary.txt";
	int numWords;
	int lettersLeft;
	int lives = 10;
	ArrayList<Character> lettersGuessed = new ArrayList<Character>();
	ArrayList<Character> lettersWrong = new ArrayList<Character>();
	ArrayList<Character> lettersRight = new ArrayList<Character>();
	ArrayList<String> possibleWords = new ArrayList<String>();
	ArrayList<Character> mostLikelyLetters = new ArrayList<Character>();
	
	List<String> possibleWordsFiltered;
	public String currentWord;
	public static String shownWord = "";
	JFrame frame = new JFrame("HangMan");
	JPanel panel = new JPanel();
	JLabel label = new JLabel("");
	public HangMan() {
		if (frame != null) {
			
		}
		numWords = 1;
		for (int i = 0; i < numWords;i++) { //push that many words to a stack.
			String newWord = Utilities.readRandomLineFromFile(dictPath);
			if (!words.contains(newWord)) {
				words.push(newWord);
			}
		}

		for (int i = 0; i < Utilities.getTotalWordsInFile(dictPath); i++){
			possibleWords.add(Utilities.readLineFromFile(dictPath, i));
		}
		lettersGuessed.add('w');
		lettersGuessed.add('r');
		label.setFont(new Font(Font.MONOSPACED, 0, 24));
		panel.add(label);
		frame.add(panel);
		frame.addKeyListener(this);
		frame.setVisible(true);
		frame.pack();
		playGame();
	}
	public void showWord(String word, ArrayList<Character> keysTyped) {
		shownWord = "";
		lettersLeft = 0;
		for(int i = 0; i < word.length();i++) {
				if(keysTyped.contains(word.charAt(i))){
					shownWord += word.charAt(i);
					if(!lettersRight.contains(word.charAt(i))){
						lettersRight.add(word.charAt(i));
					}
				}
				else {
					shownWord += "_";
					lettersLeft++;
				}
		}
		//make a list of keys typed that were not in the word.
		//If the character is in keysTyped but not in lettersRight, add it to lettersWrong.
		for( int i = 0; i < keysTyped.size();i++) {
			if(!lettersRight.contains(keysTyped.get(i))){
				if(!lettersWrong.contains(keysTyped.get(i))){
					lettersWrong.add(keysTyped.get(i));
				}	
			}
		}
		label.setText(shownWord + " Wrong Letters: " + lettersWrong + " Lives Left: " + (lives-lettersWrong.size()));
		frame.pack();
		//Game restart
		if(lettersLeft == 0){ //word is solved, restart
			System.out.println("Congrats. Word was " + currentWord);
			frame.dispose();
			new HangMan();
		}
		else if (lives-lettersWrong.size() <= 0) { //out of lives
			System.out.println("Out of lives. Word was "+ currentWord);
			frame.dispose();
			new HangMan();
		}
	}
	@Override
	public void keyPressed(KeyEvent e) {
		char guess;
		if( e.getKeyChar() == '`') {
			guess = AI();
		}
		else {
			guess = e.getKeyChar();
		}
		if(!lettersGuessed.contains(guess)){
			lettersGuessed.add(guess);
		}
		showWord(currentWord, lettersGuessed);
	}
	public static void main(String[] args) {
		new HangMan();
	}

	public void playGame() {
		lettersGuessed = new ArrayList<Character>();
		lettersWrong = new ArrayList<Character>();
		lettersRight = new ArrayList<Character>();
		currentWord = words.pop();
		//System.out.println("Playing Game. Word is  ");
		showWord(currentWord, lettersGuessed);
		
	}
	@Override
	public void keyReleased(KeyEvent arg0) {
	}
	@Override
	public void keyTyped(KeyEvent e) {
	}

	public char AI(){
		HashMap<Character, Integer> charCounts = new HashMap<Character,Integer>();
		mostLikelyLetters = new ArrayList<Character>();
		charCounts.put('a', 0);
		possibleWordsFiltered = possibleWords;
		System.out.println();
		String regex = shownWord.replace('_', '.');
		//regex += '$';
		System.out.println("Regex =" + regex);
		

		//filtering based on the regex
		int length = shownWord.length();
		if(lettersGuessed.size() == 0) { //no letters have been guessed , AI can't help
			System.out.println("You need to guess a letter first.");
		}
		else{
			
			Predicate<String> wordFilter = Pattern.compile(regex).asPredicate();
			possibleWords.removeIf(p -> p.length() != length);

			possibleWordsFiltered = possibleWords.stream().filter(wordFilter).collect(Collectors.toList());/*forEach(e->possibleWordsFiltered.add(e));*/
			//possibleWordsFiltered.removeIf(p -> !possibleWordsFiltered.contains(p));
			System.out.println("Filtered by regex" + possibleWordsFiltered);
			if(possibleWordsFiltered.size() != 1) {

				//filtering based on the wrong letters
				for (char character: lettersWrong) {
					possibleWordsFiltered.removeIf(p -> p.contains(character+""));
				}
				System.out.println("Filtered by wrong letters" + possibleWordsFiltered);

				if(possibleWordsFiltered.size() != 1) {
					//filtering out words that contain the wrong number of a correctly guessed letter.
					for (char character: lettersRight) {
						possibleWordsFiltered.removeIf(p -> shownWord.codePoints().filter(ch -> ch == character).count() != p.codePoints().filter(ch -> ch == character).count());
					}
					System.out.println("Eliminating words with wrong number of a correct letter"+ possibleWordsFiltered);
				}
			}
			for (char ch = 'a'; ch <= 'z'; ++ch){
				charCounts.put(ch, 0);
			}
			for (String str: possibleWordsFiltered) {
				for (char ch = 'a'; ch <= 'z'; ++ch){
					if(!lettersGuessed.contains(ch))
					charCounts.put(ch, charCounts.get(ch) + Utilities.occurenceOf(ch, str)); //TODO: Need to replace this with occurence of character

				}	
			}
			}
			System.out.println("Frequencies of Chars in Remaining Words" + charCounts);
			
			int maxValueInMap=(Collections.max(charCounts.values()));  // This will return max value in the Hashmap
			for (Entry<Character, Integer> entry : charCounts.entrySet()) {  // Itrate through hashmap
				if (entry.getValue()==maxValueInMap){
					mostLikelyLetters.add(entry.getKey());
				}
			}
			System.out.println(mostLikelyLetters);
		return mostLikelyLetters.get(0);
	}
}