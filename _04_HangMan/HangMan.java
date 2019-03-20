package _04_HangMan;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import java.util.Dictionary;
import java.util.Stack;
import java.util.regex.Pattern;
import java.awt.Font;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.function.Predicate;

public class HangMan implements KeyListener{
	Stack<String> words = new Stack<String>();
	int numWords;
	int lettersLeft;
	int lives = 10;
	ArrayList<Character> lettersGuessed = new ArrayList<Character>();
	ArrayList<Character> lettersWrong = new ArrayList<Character>();
	ArrayList<Character> lettersRight = new ArrayList<Character>();
	ArrayList<String> possibleWords = new ArrayList<String>();
	
	ArrayList<String> possibleWordsFiltered = new ArrayList<String>();
	public String currentWord;
	public static String shownWord = "";
	JFrame frame = new JFrame("HangMan");
	JPanel panel = new JPanel();
	JLabel label = new JLabel("");
	public HangMan() {
		if (frame != null) {
			
		}
		numWords = 1;
		/*while(true) { //keep looping until user enters integer for number of words
			try {
				numWords = Integer.parseInt(JOptionPane.showInputDialog("How many Words?"));
				break;
			} 
			catch(NumberFormatException e){
				JOptionPane.showMessageDialog(null, "Please enter a number between 0 and " + Utilities.getTotalWordsInFile("dictionary.txt"));
			}
		}*/
		for (int i = 0; i < numWords;i++) { //push that many words to a stack.
			String newWord = Utilities.readRandomLineFromFile("dictionary.txt");
			if (!words.contains(newWord)) {
				words.push(newWord);
			}
		}

		for (int i = 0; i < Utilities.getTotalWordsInFile("dictionary.txt"); i++){
			possibleWords.add(Utilities.readLineFromFile("dictionary.txt", i));
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

			possibleWords.stream().filter(wordFilter).forEach(e->possibleWordsFiltered.add(e));
			possibleWordsFiltered.removeIf(p -> !possibleWordsFiltered.contains(p));
			System.out.println("Filtered by regex" + possibleWordsFiltered);

			//filtering based on the wrong letters
			for (char character: lettersWrong) {
				possibleWordsFiltered.removeIf(p -> p.contains(character+""));
			}
			System.out.println("Filtered by wrong letters" + possibleWordsFiltered);
		}


		



		//reset arraylists that might have been previously used.
		/**possibleWordsInChars = new ArrayList<ArrayList<Character>>();

		//eliminate all words that are the wrong length.
		
		//find most common letter that has not already been guessed.
		for (String string : possibleWords) {
			ArrayList<Character> word = new ArrayList<>();
			for (int i = 0; i < string.length(); i++) {
				word.add(string.charAt(i));
			}
			possibleWordsInChars.add(word);
		}
		System.out.println(possibleWordsInChars);
		possibleWordsInChars.removeIf(charList -> {boolean needToRemove = false; //return true to rmove word
			for (int i = 0; i < charList.size(); i++) {// go through each character in the word from possible list
				if (charList.get(i) != shownWord.charAt(i) && shownWord.charAt(i) != '_' && needToRemove != true){
					needToRemove = false;
				}
				else {
					needToRemove = true;
					break;
				} // ends else statment
		}//ends for loop
		return needToRemove;
		}//ends code chunk
		)//ends predicate
		;//ends line *whew*		
				// if the char at the chosen word does not match the shown position 
				// and the char at the shown pos is not a blank
				// then remove the char list from the possible words.**/
		//System.out.println("possible words: " + possibleWords);
		


		return 'e';
	}
}
