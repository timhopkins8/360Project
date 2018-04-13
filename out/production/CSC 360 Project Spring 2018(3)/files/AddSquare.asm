TITLE Squares by Addition (AddSquare.asm)

; Generate a set of squares by repeated addition
INCLUDE Project.inc

.data
XYZ		DWORD		9

.code
main	PROC
	mov	eax,1			; set Yi to 1
	mov	ebx,1			; set Xi to 1
	mov	ecx,20			; set loop counter
lp:
	call	WriteInt		; display the number
	call	CrLf

	add	ebx,2			; calculate Xi+1
	add	eax,ebx			; calculate Yi+1 (next square)
	sub	ecx,1
	jnz		lp

	mov	eax,XYZ
	exit
main	ENDP
END	main
