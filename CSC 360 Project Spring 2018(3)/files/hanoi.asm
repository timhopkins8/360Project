TITLE Recursive solve the Towers of Hanoi puzzlle (hanoi.asm)

; This program adds two 32-bit integers
; Author: E. Styer
; Date 10/11/2002

INCLUDE Project.inc

.data
pmt	BYTE	"How many disks? ",0
errmsg	BYTE	"Too many disks, quitting",13,10,0
outmsg	BYTE	"Move disk: ",0
frommsg	BYTE	" from post ",0
tomsg	BYTE	" to post ",0

.code
; inputs
; EAX - # of disks to move
; EBX - Post to move disks from
; ECX - Post to move disks to
; EDX - Temporary post
hanoi	PROC
		push	ebp				; set up stack frame
		mov		ebp,esp
		push	eax				; save counter
		push	ebx				; save from post
		push	ecx				; save to post
		push	edx				; save via post

		cmp		eax,0			; 0 disks?
		jbe		done			; nothing to do

; solve it recursively

; move the smaller disks out of the way
; call recursively for n-1, from, via, to
		sub		eax	,1			; one less disk
		mov		ebx,[ebp-8]		; get from pole
		mov		ecx,[ebp-16]	; move to via pole
		mov		edx,[ebp-12]	; use from as temp
		call	hanoi

; move the biggest disk
		mov		edx,offset outmsg
		call		WriteString
		mov		eax,[ebp-4]		; get # of disks
		call		WriteInt		; display disk #
		mov		edx,offset frommsg
		call		WriteString
		mov		eax,[ebp-8]		; get from pole
		call		WriteInt
		mov		edx,offset tomsg
		call		WriteString
		mov		eax,[ebp-12]	; get to pole
		call		WriteInt
		call		CrLF

; move the smaller disks to the destination
; call recursively for n-1, from, via, to
		mov		eax,[ebp-4]		; get disk count again
		sub		eax,1			; one less disk
		mov		ebx,[ebp-16]	; from via pole
		mov		ecx,[ebp-12]	; move to destination
		mov		edx,[ebp-8]		; use from as temp
		call	hanoi
	
done:
		pop		edx				; restore post info
		pop		ecx
		pop		ebx
		pop		eax
		pop		ebp				; remove stack frame
		ret
hanoi	ENDP

main	PROC
; get the number of disks
		mov		edx,offset pmt		; prepare to display prompt
		call		WriteString		; display prompt (addr in EDX)
		call		ReadInt			; get the first number (in EAX)
		cmp		eax,8			; limit # of disks
		ja		errexit

		mov		ebx,1			; from post 1
		mov		ecx,2			; to post 2
		mov		edx,3			; via post 3
		call	hanoi
		jmp		done

errexit:
		mov		edx,offset errmsg	; let them know we won't do this
		call		Writestring
done:
		exit
main	ENDP
END	main
