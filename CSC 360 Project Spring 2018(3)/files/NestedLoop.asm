TITLE Initialize array

; initialize an array of bytes to 1

INCLUDE Project.inc

.data
outer_count	DWORD	?

.code
main	PROC
	mov	ebx,5			; initialize outer counter
loop1:
; start inner loop
	mov	ecx,4
loop2:	mov	eax,ebx		; display outer counter
	call	WriteInt
	mov	eax,ecx			; display inner counter
	call	WriteInt
	call	CrLf

; end of the inner loop
	sub		ecx,1
	jnz		loop2

; restore outer counter and end outer loop
	mov	ecx,outer_count
	sub	ebx,1
	jnz	loop1

	exit
main	ENDP
END	main

