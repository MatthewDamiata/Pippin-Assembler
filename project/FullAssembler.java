package project;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FullAssembler implements Assembler {
	/**
	 * Method to assemble a file to its executable representation. 
	 * If the input has errors one of the errors will be reported 
	 * the StringBulder. The error may not be the first error in 
	 * the code and will depend on the order in which instructions 
	 * are checked. The line number of the error that is reported 
	 * is returned as the value of the method. 
	 * A return value of 0 indicates that the code had no errors 
	 * and an output file was produced and saved. If the input or 
	 * output cannot be opened, the return value is -1.
	 * The unchecked exception IllegalArgumentException is thrown 
	 * if the error parameter is null, since it would not be 
	 * possible to provide error information about the source code.
	 * @param input the source assembly language file
	 * @param output the executable version of the program if 
	 * the source program is correctly formatted
	 * @param error the StringBuilder to store the description 
	 * of the error that is reported. It will be empty (length 
	 * zero) if no error is found.
	 * @return 0 if the source code is correct and the executable 
	 * is saved, -1 if the input or output files cannot be opened, 
	 * otherwise the line number of the reported error.
	 */
	public int assemble(String inputS, String outputS, StringBuilder error) {
		if(error == null) 
			throw new IllegalArgumentException("Coding error: the error buffer is null");
		
		//we will use 2 passes in our solution
		//first pass will check for blank lines, DATA delimiter errors, and illegal white space
		//second pass will check for errors specific to code and data
		
		//in first pass, will add all lines before DATA delimiter to code list, everything else in the data list
		List<String> code = new ArrayList<>();
		List<String> data = new ArrayList<>();
		
		//retVal will be zero if there are no errors found
		//otherwise we will set it to the line number of the last error which has 
		//been appended to the StringBuilder error (passed in as an parameter to this assemble method)
		int retVal = 0;
		
		//lineNum will keep track of which line we are currently on while error checking
		int lineNum = 0;

		//first pass: open the pasm file via a Scanner
		try (Scanner inp = new Scanner(new File(inputS))) {
			
			//we have some book keeping information
			
			boolean blankLineFound = false; //tracks whether we saw a blank line while parsing
			
			int firstBlankLineNum = 0; //tracks the line number a blank line was found on
			
			//tracks whether we are reading code. we assume we are reading code until we see a DATA delimiter.
			//everything after DATA delimiter is assumed to be data entries
			boolean readingCode = true; 
			
			//read the pasm file line by line
			while(inp.hasNextLine()) {
				lineNum++;
				String line = inp.nextLine();
				System.out.println(lineNum + ": " + line); //this is here for debugging purposes
				
				//TODO: check if we are considering a blank line
				//you should trim the line of leading or trailing white space, using the trim() method
				//after you do that, you can consider the length of the string
				if(line.trim().length()==0) {
					//TODO: keep in mind blank lines are legal as long as there is no non-blank lines that appear after the blank lines
					//if we have not found a blank line:
						//1. set the blankLineFound to true, indicating we have encountered one

						//2. be sure to store the current lineNum inside firstBlankLineNum, so we have that information if we need it later.
						
					if(!blankLineFound) {
						blankLineFound=true;
						firstBlankLineNum=lineNum;
					}
				}
				
				//otherwise we are looking at a non blank line
				else {
					//if we have found a blank line, then that blank line is illegal since we are viewing a non-blank line after it.
					//note that we will not report consecutive blank lines, just the first illegal blank line. 
					//we will consider this acceptable behavior.
					if(blankLineFound) {
						//1. append an appropriate error message to the String builder error, using the append() method
						error.append("\n Illegal blank line in the source file");
						//2.set retVal to the line number of the illegal blank line (recall we stored this blank line number above)
						retVal=firstBlankLineNum;
						//3. set blankLineFound back to false, so we can search for more illegal blank lines if they are present
						blankLineFound=false;
					} 
					//TODO: check for illegal white space, specifically a tab or space at the beginning of the line
					//you can use the charAt(0) method to grab the first character of the line and compare it to a space, ' ' ,  or a tab, '\t'
					if(line.toCharArray()[0] == ' ' || line.toCharArray()[0] == '\t') {
						error.append("\nLine starts with illegal white space"); 
						//consider this a concrete example of reporting an error in the StringBuilder
						
						//1. we append an appropriate error message
						//2. and set retVal to the current line number
						error.append("Error on line " + lineNum + ": Line starts with illegal whitespace\n");
						retVal = lineNum;
					}
					//TODO: check for a DATA delimiter
					//to ensure we don't miss it due to illegal white space, or not being all upper case,
					//be sure to use the trim() and toUpperCase() methods
					//then check if the line equals "DATA" using the equals() method
					if(line.trim().toUpperCase().equals("DATA")) {
						
						//TODO: check if the trimmed line does not equal "DATA" if you don't uppercase line
						//this would mean that the DATA delimiter is not all upper cased, which is an error
						if(!line.trim().equals("DATA")) {
							//TODO: append to error and update retVal
							error.append("\nLine does not have DATA in upper case");
							retVal=lineNum;
						}
						
						//if readingCode is true
						//then this is the first DATA delimiter we have encountered, and we assume 
						//everything after it is data, not code.
						//we are no longer readingCode, so we set the boolean to false
						if(readingCode) {
							readingCode = false;
						}
						//TODO: otherwise, we are looking at a DATA delimiter that appeared after the first one
						//so we have a duplicate DATA delimiter. this is an error
						else{
							//TODO: report Duplicate DATA delimiter at the current line
							error.append("Duplicate Data Delmiter at line:"+lineNum);
						}
					} 
				}//end of non blank line checks

				//finally, if we are readingCode, we add the trimmed line to the code list
				//otherwise, we add the trimmed line to the data list
				//note this does not skip blank lines, nor does it skip the DATA delimiter(s) here
				if(readingCode) code.add(line.trim());
				else data.add(line.trim());

			} 
		} catch (FileNotFoundException e) {
			error.append("Unable to open the assembled file\n");
			retVal = -1;
		} // end of first pass

		//now that we are doing our second pass, we are starting over from the beginning of the file
		//so we reset lineNum to zero
		lineNum = 0;

		//second pass, dealing with code specific error checking
		for(String line : code) {
			lineNum++;
			//If the line is blank, continue to the next line
			if(line.length() == 0) continue;

			//takes the current line and splits it based on spaces
			//note parts[0] should be a mnemonic such as ADD or DIV
			//and if parts[1] is present, it should be an argument for the instruction
			String[] parts = line.split("\\s+");
			
			//here, we check whether parts[0] is an illegal mnemonic
			//it is considered illegal if it is not found in the map of OPCODES
			
			//recall the OPCODES map is found in the Instruction class
			//you will need to do similar things below with other data structures in the Instruction class
			//this has been provided as an example
			if(!Instruction.OPCODES.containsKey(parts[0].toUpperCase())) {
				error.append("Error on line " + lineNum + 
						": illegal mnemonic\n");
				retVal = lineNum;				
			}
			
			//otherwise, we are dealing with a legal mnemonic
			else{
				//TODO: check if the mnemonic is not all upper case
				//you can do this by comparing parts[0] to itself
				//but calling toUpperCase() 
				
				//string comparisons should be done via the equals() method
				if(!parts[0].equals(parts[0].toUpperCase())) {
					//TODO: append error message and update retVal
					error.append("\n The mnemonic should be capitalized");
					retVal=lineNum;
					//TODO: also set parts[0] to the upper cased version of itself so you don't have to account for that below
					parts[0]=parts[0].toUpperCase();
				}
				
				//TODO: check if mnemonic is supposed to take no arguments
				//you will need to use the NO_ARG_MNEMONICS set found in the Instruction class
				//you can use the contains() method to see if the set contains the mnemonic
				if(Instruction.NO_ARG_MNEMONICS.contains(parts[0])) {
					//TODO: if parts.length is greater than 1, append an error message, explaining this mnemonic cannot take arguments, and update retVal
					if(parts.length>1) {
						error.append("\n This mnemonic cannot take arguments");
						retVal=lineNum;}
				}
				
				//otherwise, the mnemonic should have an argument
				else {
					//TODO: if parts.length is greater than 2, there are too many arguments present. append to error and update retVal
					if(parts.length>2) {
						error.append("There are too many arguments");
						retVal=lineNum;
						
					}
					//TODO: else if parts.length is less than 2, the mnemonic is missing an argument. append to error and update retVal
					else if(parts.length<2) {
						error.append("The mnemoic is missing an argument");
						retVal=lineNum;
						
					}
					//otherwise, there is a correct number of arguments
					else {
						//TODO: now we check to make sure the mode specified is legal for the given mnemonic. 
						//we check if there is an 'M' (indicates immediate mode) appended to the front of the argument
						//and provide you an example here. you will need to do a similar check for 'N' (indirect mode)
						//and for 'J' (JMP mnemonics)
						
						//Immediate mode error checking
						if(parts[1].charAt(0) == 'M') {
							//if there is an 'M' but the mnemonic is not contained in the IMM_MNEMONIC set from the Instruction class
							//then we have an error
							if(!Instruction.IMM_MNEMONICS.contains(parts[0].toUpperCase())) {
								error.append("Error on line " + lineNum + 
										": this mnemonic does not allow immediate mode\n");
								retVal = lineNum;
							}
							//also make sure to get rid of the 'M' from the argument 
							parts[1] = parts[1].substring(1);
						}
						//TODO: do a similar check for 'N'.
						//the set you will need to consider is IND_MNEMONICS from the Instruction class.
						//make sure you remember to get rid of the 'N' from the argument as we did above.
						else if(parts[1].charAt(0)=='N') {
							if(!Instruction.IND_MNEMONICS.contains(parts[0].toUpperCase())) {
								error.append("Error on line " + lineNum + 
										": this mnemonic does not allow immediate mode\n");
								retVal = lineNum;
							}
							parts[1] = parts[1].substring(1);
						
						} 
						//TODO: do a similar check for 'J'.
						//the set you will need to consider is JMP_MNEMONICS from the Instruction class.
						//make sure you remember to get rid of the 'J' from the argument as we did above.
						else if (parts[1].charAt(0)=='J'){
							if(!Instruction.JMP_MNEMONICS.contains(parts[0].toUpperCase())) {
								error.append("Error on line " + lineNum + 
										": this mnemonic does not allow immediate mode\n");
								retVal = lineNum;
							}
							parts[1] = parts[1].substring(1);

						}
						
						//next, we check to make sure the argument is hexadecimal.
						//you can do this with a try / catch block, which we showcase here.
						//you will need to do this again for the data entries, so take note.
						try {
							Integer.parseInt(parts[1],16); // test arg is in hex, base 16
						}
						//if it is not hexadecimal, the parseInt call will fail and throw a NumberFormatException.
						//we will catch that exception and append an error message and update retVal as usual.
						catch(NumberFormatException e) {
							error.append("Error on line " + lineNum + 
									": argument is not a hex number\n");
							retVal = lineNum;				
						}
					}
				}
			}
		}

		//second pass, data specific error checking
		for(String line : data) {
			lineNum++;
			//Similar to what we did for code, we want to continue passed blank lines
			//BUT additionally we also want to skip passed lines that hold DATA delimiters
			if(line.length() == 0 || line.toUpperCase().trim().equals("DATA")) continue;

			String[] parts = line.split("\\s+");
			
			//make sure 2 data entries are present.
			//TODO: if there are less than 2, report that data entry has too few numbers and update retVal.
			//this will be similar to how we checked in the code error parsing
			if(parts.length<2) {
				error.append("Too few data entries");
				retVal=lineNum;
				
								
			} 
			
			//TODO: else if there are more than 2, report that data entry has too many numbers and update retVal.
			//this will be similar to how we checked in the code error parsing
			else if(parts.length>2) {
				error.append("There are too many numbers");
				retVal=lineNum;
				
			}
			//otherwise, we have the right number of values. 
			//we just need to see if they are both hexadecimal
			else {
				//TODO: similar to what we did in the code error checking,
				//if parts[0] is not hexadecimal report an error: "data has non-numeric memory address" and update retVal.
				try {
				Integer.parseInt(parts[0],16);
				}catch(NumberFormatException e) {
					error.append("Error on line " + lineNum + 
							": argument is not a hex number\n");
					retVal = lineNum;		
				}

				//TODO: similar to what we did in the code error checking,
				//if parts[1] is not hexadecimal report an error: "data has non-numeric memory value" and update retVal.
				try {
					Integer.parseInt(parts[1],16);
					}catch(NumberFormatException e) {
						error.append("Error on line " + lineNum + 
								": argument is not a hex number\n");
						retVal = lineNum;		
					}


			}
		}
		
		//if retVal is still zero, no errors were found so we actually assemble the pasm file into an pexe file
		if(retVal == 0) {
			new SimpleAssembler().assemble(inputS, outputS, error);
		}
		//otherwise, we print out error for easy error checking and debugging purposes
		else {
			System.out.println("\n" + error.toString());
		}

		return retVal;
	}
	
	//This main method has also been provided so you can test your full assembler against problem files.
	//note all files that have an 'e' in their name indicate there is at least one error to find in that file in our error checking above, such as 03e.pasm
	public static void main(String[] args) {
		StringBuilder error = new StringBuilder();
		System.out.println("Enter the name of the file without extension: ");
		try (Scanner keyboard = new Scanner(System.in)) { 
			String filename = keyboard.nextLine();
			int i = new FullAssembler().assemble(filename + ".pasm", 
					filename + ".pexe", error);
			System.out.println("result = " + i);
		}
	}
}
