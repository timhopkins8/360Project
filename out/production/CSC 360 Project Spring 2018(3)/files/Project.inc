; "library" routines for handling 
.386		; 368 only instructions
.MODEL flat, stdcall

.stack 4096				; define default stack size

; EBX = Xi
; EAX = Yi

.code
IntCrLf			EQU		20h
IntReadInt		EQU		21h
IntWriteInt		EQU		22h
IntWriteString	EQU		23h
IntExitProcess	EQU		24h
IntRandomRange	EQU	 	25h

; create wrappers for the interrupt calls
CrLf	PROC
		int		IntCrLf
		ret
CrLf	ENDP

ReadInt	PROC
		int		IntReadInt
		ret
ReadInt	ENDP

WriteInt	PROC
		int		IntWriteInt
		ret
WriteInt	ENDP

WriteString	PROC
		int		IntWriteString
		ret
WriteString	ENDP

ExitProcess	PROC
		int		IntExitProcess
		ret
ExitProcess	ENDP

RandomRange	PROC
		int		IntRandomRange
		ret
RandomRange	ENDP

; "exit" macro
exit MACRO
		int	intExitProcess
ENDM