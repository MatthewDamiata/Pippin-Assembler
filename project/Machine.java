package project;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static project.Instruction.*;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;

public class Machine {
	private class CPU{
		private int accum;
		private int pc;
	}
	
	public final Map<Integer, Consumer<Instruction>> ACTION = new TreeMap<>();
	private CPU cpu = new CPU();
	private Memory memory = new Memory();
	private boolean withGUI = false;
	private HaltCallback callBack; 
	
	public void halt() {
		callBack.halt();
	}
	public int getData(int index) {
		return memory.getData(index);
	}
	public void setData(int i, int j) {
		memory.setData(i, j);		
	}
	//package private
	int[] getData() {
		return memory.getData();
	}
	//package private
	int[] getData(int min, int max) {
		return memory.getData(min,max);
	}
	public Instruction getCode(int index) {
		return memory.getCode(index);
	}
	public int getProgramSize() {
		return memory.getProgramSize();
	}
	public void addCode(Instruction j) {
		memory.addCode(j);	
	}
	// package private
	void setCode(int index, Instruction instr) {
		memory.setCode(index, instr);	
	}
	public List<Instruction> getCode() {
		return memory.getCode();
	}
	//package private
	Instruction[] getCode(int min, int max) {
		return memory.getCode(min,max);
	}
	public int getPC() {
		return cpu.pc;
	}
	public void setPC(int pc) {
		cpu.pc = pc;
	}
	public int getChangedDataIndex() {
		return memory.getChangedDataIndex();
	}
	public int getAccum() {
		return cpu.accum;
	}
	public void setAccum(int i) {
		cpu.accum = i;
	}
	
	public void clear() {
		memory.clearData();
		memory.clearCode();
		setPC(0);
		setAccum(0); 
		}
	
	public void step(){
		try {
			Instruction instr= getCode(cpu.pc);
			Instruction.checkParity(instr);
			ACTION.get(instr.opcode/8).accept(instr);
		}
		catch(Exception e) {
			e.printStackTrace();
			callBack.halt();
			throw e;
		}
	}
	
	
	public Machine(HaltCallback cb) {
		callBack = cb;
		
		ACTION.put(OPCODES.get("NOP"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags != 0){
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;		
		});
		
		ACTION.put(OPCODES.get("NOT"), instr ->{
		int flags = instr.opcode & 6; // remove parity bit that will have been verified
		if(flags == 0){
			cpu.accum = (cpu.accum ==0) ? 1 : 0;
		}
		
		else {
			String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
			throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
		}
		cpu.pc++;		
	});
		
		ACTION.put(OPCODES.get("HALT"), instr ->{
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0){
				cb.halt();
			}
			
			else {
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}		
		});
				
		ACTION.put(OPCODES.get("JUMP"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.pc += instr.arg;
			} else if(flags == 2) { // immediate addressing
				cpu.pc=(instr.arg);
			} else if(flags == 4) { // indirect addressing
				cpu.pc += memory.getData(instr.arg);}
			else
				cpu.pc=memory.getData(instr.arg);
			}); 
		
		ACTION.put(OPCODES.get("JMPZ"), instr -> {
			if(cpu.accum==0) {
				int flags = instr.opcode & 6; // remove parity bit that will have been verified
				if(flags == 0) { // direct addressing
					cpu.pc += instr.arg;
				} else if(flags == 2) { // immediate addressing
					setPC(instr.arg);
				} else if(flags == 4) { // indirect addressing
					cpu.pc += memory.getData(instr.arg);				
				}
				else
					cpu.pc=memory.getData(instr.arg);}
			else
				cpu.pc++;
		});
		
		ACTION.put(OPCODES.get("LOD"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				setAccum(memory.getData(instr.arg));
			} else if(flags == 2) { // immediate addressing
				setAccum(instr.arg);
			} else if(flags == 4) { // indirect addressing
				setAccum(memory.getData(memory.getData(instr.arg)));				
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(OPCODES.get("STO"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				setData(instr.arg,cpu.accum);
			} else if(flags == 4) { // indirect addressing
				setData(memory.getData(instr.arg),cpu.accum);				
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(OPCODES.get("AND"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				setAccum((getAccum()!=0 && memory.getData(instr.arg) != 0)? 1:0);
				cpu.pc++;
			} else if(flags == 2) { 
				setAccum((getAccum()!=0 && instr.arg != 0)? 1:0);
				cpu.pc++;			
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
						
		});
		
		ACTION.put(OPCODES.get("CMPL"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) {
				setAccum((memory.getData(instr.arg)<0)?1:0);
			}
			
			else{
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;		
		});
		
		ACTION.put(OPCODES.get("CMPZ"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) {
				setAccum((memory.getData(instr.arg)==0)?1:0);
			}
			
			else{
				String fString = "(" + (flags%8 > 3?"1":"0") + (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;		
		});
		
		ACTION.put(OPCODES.get("ADD"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum += memory.getData(instr.arg);
			} else if(flags == 2) { // immediate addressing
				cpu.accum += instr.arg;
			} else if(flags == 4) { // indirect addressing
				cpu.accum += memory.getData(memory.getData(instr.arg));				
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(OPCODES.get("SUB"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum -= memory.getData(instr.arg);
			} else if(flags == 2) { // immediate addressing
				cpu.accum -= instr.arg;
			} else if(flags == 4) { // indirect addressing
				cpu.accum -= memory.getData(memory.getData(instr.arg));				
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(OPCODES.get("MUL"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				cpu.accum *= memory.getData(instr.arg);
			} else if(flags == 2) { // immediate addressing
				cpu.accum *= instr.arg;
			} else if(flags == 4) { // indirect addressing
				cpu.accum *= memory.getData(memory.getData(instr.arg));				
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		ACTION.put(OPCODES.get("DIV"), instr -> {
			int flags = instr.opcode & 6; // remove parity bit that will have been verified
			if(flags == 0) { // direct addressing
				if(memory.getData(instr.arg)!=0)
					cpu.accum /= memory.getData(instr.arg);
				else
					throw new DivideByZeroException("Zero Division");
			} else if(flags == 2) { // immediate addressing
				if(instr.arg!=0)
					cpu.accum /= instr.arg;
				else
					throw new DivideByZeroException("Zero Division");
			} else if(flags == 4) { // indirect addressing
				if(memory.getData(memory.getData(instr.arg))!=0)
					cpu.accum /= memory.getData(memory.getData(instr.arg));
				else
					throw new DivideByZeroException("Zero Division");
						memory.getData(memory.getData(instr.arg));				
			} else {
				String fString = "(" + (flags%8 > 3?"1":"0") 
						+ (flags%4 > 1?"1":"0") + ")";
				throw new IllegalInstructionException("Illegal flags for this instruction: " + fString);
			}
			cpu.pc++;			
		});
		
		
	}
	public static void main(String[] args) {
		Machine machine = new Machine(() ->  {});
		int[] dataCopy = new int[Memory.DATA_SIZE];
		int accInit;
		int pcInit;
		for (int i = 0; i < Memory.DATA_SIZE; i++) {
			dataCopy[i] = -5*Memory.DATA_SIZE + 10*i;
			machine.setData(i, -5*Memory.DATA_SIZE + 10*i);
			// Initially the machine will contain a known spread
			// of different numbers: 
			// -2560, -2550, -2540, ..., 0, 10, 20, ..., 2550 
			// This allows us to check that the instructions do 
			// not corrupt machine unexpectedly.
			// 0 is at index 256
		}
		// prefill with 10 NOP instructions
		for(int i = 0; i < 10; i++)
			machine.addCode(new Instruction((byte)0, 0));
		accInit = 7;
		pcInit = 4;
		Instruction instr = new Instruction((byte)0b00000001,0);
		machine.setCode(pcInit, instr);
		machine.setPC(pcInit);
		Throwable exception = assertThrows(ParityCheckException.class,
				() -> machine.step());	
		System.out.println(exception);
	}
	

}
