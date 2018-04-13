TITLE Perform a bubblesort (bubblesort.asm)

; This program sorts an array of 32-bit integers

INCLUDE Project.inc

.data
ASIZE = 20
array	DWORD	ASIZE dup (0)

.code
main	PROC
; generate 20 random numbers
	mov	ebx,0
gennum:
	mov	eax,1000				; get numbers 0 to 999
	call	RandomRange
	mov	array[ebx],eax			; save in the array
	add	ebx,4					; move to next items
	cmp	ebx,ASIZE*4				; done generating numbers?
	jl	gennum
	
again:
; sort the numbers using bubblesort
	mov	ecx,(sizeof array)-4	; get the size of the array (6*4) minus one element
	mov	esi,0			; start loop
	mov	edi,0			; note we haven't swapped anything

; compare two consecutive numbers in the array
comp:
	mov	eax,array[esi]		; get the first value
	cmp	eax,array+4[esi]	; compare to the second
	jle	inorder

; need to swap the items
	mov	ebx,array+4[esi]	; get the second number
	mov	array[esi],ebx		; save the new first number
	mov	array+4[esi],eax	; save the new second number
	mov	edi,1			; note a swap occurred

inorder:
	add	esi,4			; increment the counter
	cmp	esi,ecx			; at the end of the array?
	jl	comp

; if we did any swaps, need to do another pass throught the array
	cmp	edi,1
	je	again

; display the result
	mov	esi,0			; initialize counter
dloop:	mov	eax,array[esi]		; get the next number
	call	WriteInt		; display result (value in EAX)
	call	CrLf
	add	esi,4			; move to next value
	cmp	esi,ecx			; at the end of the array?
	jle	dloop

	exit
main	ENDP
END	main
