package pack; 

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;	
import javax.swing.text.*;
import java.awt.event.*;
import java.util.concurrent.atomic.*;
import java.nio.file.*;
import java.io.*;

/**
* The Matrix program prints random numbers on the generated GUI. The user has number of options to manipulate the program
* as he wishes. The instructions for manipulating the result of the Matrix are show when the program starts. 
*
*@author Igor Kojic
*@version 1.0
*
*/
public class Matrix extends JFrame implements KeyListener{
	
	private JTextArea textArea = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane(textArea);
	
	/**
	* The thread that constantly tries to print the numbers.
	*/
	private ScheduledExecutorService service = Executors.newScheduledThreadPool(1);
	
	private int emptySlotsInMatrix = 0;
	
	/**
	* Determines how slow/fast will the program run. The higher the sleep time the slower the program will be and vice versa.
	* Minimum sleep time can be 0 and the maximum 800.
	*/
	private int sleepTime = 0;
	
	/**
	* If set to true the program will print the number that is being pressed. If set to false the program will output random numbers.
	*/
	private boolean printSpecificNumber = false;
	
	
	/**
	* If the matrixTrigger is set to true, the program will run. If the matrixTrigger is set to false, the program will pause.
	*/
	private boolean matrixTrigger = true;
	
	private int numberPressed;
	
	/**
	* Creates the program window.
	* @param title The title of the program window.
	*/
	public Matrix(String title){
		
		createDialog("welcome"); // when 'welcome' then the welcome instructions will be created. Any other input will create standard instructions 
		
		setSize(900,600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setTitle(title);
		Path logoPath = Path.of("logoPicture");
		try{
			Files.setAttribute(logoPath,"dos:hidden",true);
		} catch(IOException e){
			e.printStackTrace();
		}
		
		textArea.setFont(new Font(Font.MONOSPACED, Font.BOLD, 13)); // makes every character in the line take equal amount of space
		textArea.setEditable(false);
		textArea.setBackground(Color.BLACK);
		textArea.setForeground(Color.GREEN);
		textArea.requestFocusInWindow();
		textArea.addKeyListener(this);
		
		service.scheduleWithFixedDelay(() -> appendLineToMatrix(),0,1,TimeUnit.MILLISECONDS);
		getContentPane().add(scrollPane);
		
		
		
		setResizable(false);
		setVisible(true);
		setIconImage(Toolkit.getDefaultToolkit().getImage("logoPicture/logo.png"));
	}
	
	/**
	* Creates the instructions dialog. 
	* @param str Determines if the dialog is the start dialog or the help dialog based on the input.
	*/
	private void createDialog(String str){
		String firstString = str.equals("welcome") ? "                                      Welcome to the Matrix \n \n " + 
		" These are the instructions you might need: \n" : "Matrix instructions: \n";
		JOptionPane.showMessageDialog(this,firstString
		+ "\n- Press CTRL and S to stop the program. "
		+ "\n- Press CTRL and C to continue the program."
		+ "\n- Press the UP arrow to increase the Matrix speed."
		+ "\n- Press the DOWN arrow to decrease the Matrix speed."
		+ "\n- Press O to increase the number of empty slots."
		+ "\n- Press L to decrease the number of empty slots."
		+ "\n- Press SPACE to put an empty line in the Matrix."
		+ "\n- Press CTRL and BACKSPACE to clear the whole Matrix."
		+ "\n- Press and hold any number for it to be outputed in the whole Matrix."
		+ "\n- Press CTRL and I if you wish to see these instructions again."
		+ "\n- Press CTRL and E to close the Matrix.","Matrix instructions",JOptionPane.PLAIN_MESSAGE);
	}
	
	/**
	* Increases the matrix speed.
	* @see #decreaseMatrixSpeed()
	*/
	private void increaseMatrixSpeed() {
		if(!(sleepTime <= 0)){
			sleepTime -= 20;
		}
	}
	
	/**
	* Decreases the matrix speed.
	* @see #increaseMatrixSpeed()
	*/
	private void decreaseMatrixSpeed() {
		if(sleepTime < 800){
			sleepTime += 20;
		}
	}
	
	/**
	* This list saves which numbers are pressed at the same time. When one of the keys gets released, the list will be cleared.
	*/
	private ArrayList<Integer> keysPressed = new ArrayList<>();

	/**
	* This list contains the numbers from 0 to 9 in the form of KeyEvent's.
	*/
	private ArrayList<Integer> numbers = new ArrayList<>(Arrays.asList(KeyEvent.VK_0,KeyEvent.VK_1,KeyEvent.VK_2,KeyEvent.VK_3,KeyEvent.VK_4, 
								KeyEvent.VK_5,KeyEvent.VK_6,KeyEvent.VK_7,KeyEvent.VK_8,KeyEvent.VK_9));
	
	@Override
	public void keyPressed(KeyEvent e){
		
		if(!keysPressed.contains(e.getKeyCode())){
			keysPressed.add(e.getKeyCode());
		}
		
		if(keysPressed.contains(KeyEvent.VK_S) && keysPressed.contains(KeyEvent.VK_CONTROL)){
			matrixTrigger = false;
		}
		else if(keysPressed.contains(KeyEvent.VK_C) && keysPressed.contains(KeyEvent.VK_CONTROL)){
			matrixTrigger = true;
		}
		else if(KeyEvent.VK_DOWN == e.getKeyCode()){
			decreaseMatrixSpeed();
		} 
		else if(KeyEvent.VK_UP == e.getKeyCode()){
			increaseMatrixSpeed();
		}
		else if((keysPressed.contains(KeyEvent.VK_CONTROL)) && (keysPressed.contains(KeyEvent.VK_BACK_SPACE)) ){
			textArea.setText(null);
		}
		else if(KeyEvent.VK_O == e.getKeyCode()){
			if(emptySlotsInMatrix < 80){
				emptySlotsInMatrix += 1;
			}
		}
		else if(KeyEvent.VK_L == e.getKeyCode()){
			if(!(emptySlotsInMatrix <= 0)){
				emptySlotsInMatrix -= 1;
			}
		} 
		else if(KeyEvent.VK_SPACE == e.getKeyCode()){
			textArea.append("\n");
		} 
		else if((keysPressed.contains(KeyEvent.VK_CONTROL)) && (keysPressed.contains(KeyEvent.VK_E))){
			this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));	
		}
		else if(keysPressed.contains(KeyEvent.VK_CONTROL) && keysPressed.contains(KeyEvent.VK_I)){
			createDialog("normal");
		}
		// this if statement determines which number is being pressed
		else if(numbers.contains(e.getKeyCode())){
			printSpecificNumber = true;
			final var pressedKey = e.getKeyCode();
			numberPressed = numbers.stream()
			.filter((a) -> pressedKey == a)
			.mapToInt(a -> numbers.indexOf(a))
			.sum();
			
			System.out.println(numberPressed);
		}
		
	}
		
	@Override
	public void keyReleased(KeyEvent e){
		keysPressed.clear();
		printSpecificNumber = false;
	}
		
	@Override
	public void keyTyped(KeyEvent e){
		
	}
	
	/**
	* Appends new line to the Matrix.
	*/
	private void appendLineToMatrix(){
		
		if(matrixTrigger){ // if true then the matrix can run
			try{
				Thread.sleep(sleepTime);
			} catch(InterruptedException e){System.err.println("InterruptedException thrown by sleep() method!");}
						
			StringBuilder builder = new StringBuilder();
		
			if(printSpecificNumber == true){ 			// if true then this algorithm is used for creating the matrix line with only one number
				for(int i = 0; i < 108; i++){
					builder.append(numberPressed);
				}
				
				if(emptySlotsInMatrix  > 0){				
					for(int i = 0; i < emptySlotsInMatrix; i++){
						int random = 1 + new Random().nextInt(108);
						builder.replace(random-1,random," "); 
					}
				}
				
			} 
			else if(printSpecificNumber == false){		// if false then this algorithm is used for creating the matrix line with all numbers
				
				for(int i = 0; i < 108; i++){
					builder.append(new Random().nextInt(9));
				}
				
				if(emptySlotsInMatrix  > 0){				
					for(int i = 0; i < emptySlotsInMatrix; i++){
						int random = 1 + new Random().nextInt(108);
						builder.replace(random-1,random," "); 
					}
				}
			}
			
			textArea.append("\n" + builder.toString()); 
			scrollPane.getVerticalScrollBar().setValue(Integer.MAX_VALUE);
		}
	}
	
	public final static void main(String... args){
		
		Matrix matrix = new Matrix("Matrix");
		
	}
} 
