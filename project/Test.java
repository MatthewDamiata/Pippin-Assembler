package project;

public class Test {

	public static void main(String[] args) {
	Instruction test = new Instruction((byte) 5, 5);
	Instruction.checkParity(test);
	}
}
