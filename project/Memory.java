package project;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Memory {
	public static final int DATA_SIZE = 512;
	private int[] data = new int[DATA_SIZE];
	public static final int CODE_SIZE=256;
	List<Instruction> code = new ArrayList<>();
	int changedDataIndex = -1;
	
	int[] getData(int min, int max) {
		if(min<0 || max>=DATA_SIZE || max<min)
			throw new DataAccessException("Array index is out of limits");
		return Arrays.copyOfRange(data, min, max);
	}
	
	int[] getData() {
		return data;}
	
	int getData(int index){
		if(index<0 || index >= DATA_SIZE)
			throw new DataAccessException("Array index is out of limits");
		return data[index];}
	
	void setData(int index, int value) { 
		if(index<0 || index >= DATA_SIZE)
			throw new DataAccessException("Array index is out of limits");
		data[index]=value;
		changedDataIndex=index;}
	
	int getChangedDataIndex() {
		return changedDataIndex;
	}
	
	List<Instruction> getCode(){
		return code;
	}
	
	Instruction getCode(int index) {
		if(index < 0 || index >= code.size())
			throw new CodeAccessException("Illegal access to code");
		return code.get(index);	
	}
	
	public Instruction[] getCode(int min, int max) {
		if(!(0 <= min && min <= max && max < code.size()))
			throw new CodeAccessException("Illegal access to code");
		Instruction[] temp = {};
		temp = code.toArray(temp);
		return Arrays.copyOfRange(temp, min, max); 
		}
	
	void clearData(){
		for(int i=0; i< data.length; i++)
			setData(i,0);
		changedDataIndex = -1;
	}
	
	void addCode(Instruction value) {
		if(code.size() < CODE_SIZE)
			code.add(value);
	}
	
	void setCode(int index, Instruction instr) {
		if(!(0 <= index && index < code.size()))
			throw new CodeAccessException("Illegal access to code");
		code.set(index, instr);
			
	}
	
	void clearCode() {
		code.clear();
	}
	
	int getProgramSize() {
		return code.size();
	}

	

}
