CMPZ 0
SUB M1
JMPZ MF
CMPL 0
SUB M1
JMPZ ME
LOD 0
STO 1
LOD 0 
SUB M1
STO 0
CMPZ 0
SUB M1
JMPZ M6
LOD 0
MUL 1
DIV 10	
NOT
STO 1
HALT
DATA
0 8