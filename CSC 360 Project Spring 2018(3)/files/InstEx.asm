TITLE Add and Subtract (AddSub.asm)

; This program adds and subtracts 32-bit integers
.686P		; Pentium Pro or later
.MODEL flat, stdcall

.stack 10000				; define default stack size

.data
Z	DWORD	33
A	DWORD	20 dup(1)
.code
main	PROC
	mov		ebx,0
	mov		ecx,0
l1:	add		eax,ebx
	add		ebx,ecx
	add		eax,A[ebx]
	add		ebx,A[ecx]
	add		eax,99
	add		ebx,99
	add		ebx,9999
	add		ebx,999999
	add		eax,Z
	add		ebx,Z

	and		eax,ebx
	and		ebx,ecx
	and		eax,A[ebx]
	and		ebx,A[ecx]
	and		eax,99
	and		ebx,99
	and		eax,Z
	and		ebx,Z
	
	call	l
	
	cmp		eax,ebx
	cmp		ebx,ecx
	cmp		eax,A[ebx]
	cmp		ebx,8[ecx]
	cmp		eax,99
	cmp		ebx,99
	cmp		eax,Z
l:	cmp		ebx,Z
	
	je		l
	jne		l
	jg		l
	jge		l
	jl		l
	jle		l
	jmp		l
	jmp		l2
	
	LAHF
	SAHF
	
	mov		eax,ecx
	mov		ebx,ecx
	mov		eax,8[esi]
	mov		ebx,8[esi]
	mov		eax,99
	mov		ebx,99
	mov		eax,Z
	mov		ebx,Z
	mov		8[edi],eax
	mov		8[edi],ebx
	mov		Z,eax
	mov		Z,ebx
	
	neg		eax
	neg		ebx
	
	not		eax
	not		ebx
	
	or		eax,ebx
	or		ebx,ecx
	or		eax,8[ebx]
	or		ebx,8[ecx]
	or		eax,99
	or		ebx,99
	or		eax,Z
	or		ebx,Z
	
	pop		eax
	pop		ebx
	
	push	99
	push	9999
	push	999999
	push	eax
	push	ebx
	
	ret
	
	sal		eax,5
	sal		ebx,5
	
	sar		eax,5
	sar		ebx,5
	
	sub		eax,ebx
	sub		ebx,ecx
	sub		eax,8[ebx]
	sub		ebx,8[ecx]
	sub		eax,99
	sub		ebx,99
	sub		eax,Z
	sub		ebx,Z
	
	xor		eax,ebx
	xor		ebx,ecx
	xor		eax,8[ebx]
	xor		ebx,8[ecx]
	xor		eax,99
	xor		ebx,99
	xor		eax,Z
	xor		ebx,Z
	
	call	l1
l2:	jne		l1
main	ENDP
END	main
