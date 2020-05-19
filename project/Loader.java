package project;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

public class Loader {
	
	public static String load(Machine machine, File file) throws Exception {
		if(machine==null || file == null)
			return null;
		int numInstr =0;
		boolean flag=true;
		try (DataInputStream input = new DataInputStream(new FileInputStream(file))) {
			while(true) {
				int namex=input.readInt();
				if(flag==true && namex==-1)
					flag=false;
				else if(flag==true) {
					numInstr++;
					machine.addCode(new Instruction((byte)namex, input.readInt()));
				}
				else
					machine.setData(namex,input.readInt());
			}
			
		}
		catch (EOFException e) {
			return "" + numInstr;
		} catch (FileNotFoundException e1) {
			return("File " + file.getName() + " Not Found");
		} catch (Exception e) {
			throw e;
		}
		
	}
	
	public static void main(String [] args) throws Exception {
		Machine machine = new Machine(() -> System.exit(0));
		String str = Loader.load(machine, new File("factorial.pexe"));
		
		for(int i=0; i<Integer.parseInt(str); i++) {
			System.out.println(machine.getCode(i).getText());
		}
		System.out.println(Arrays.toString(machine.getData(0,20)));
		machine.setPC(0);
		while(true) {
			System.out.println(machine.getCode(machine.getPC()).getText());
			machine.step();
			System.out.println(Arrays.toString(machine.getData(0,20)));
		}
		
	
	}
}


