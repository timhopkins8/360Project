TITLE Sieve of Eratosthenes (sieve.asm)

; Calculate primes using the Sieve of Eratosthenes

; This program adds and subtracts 32-bit integers
INCLUDE Project.inc

; maximum number we are planning to check
MAXVAL = 200
.data
primelist DWORD	 MAXVAL+1 dup (1)		; create array, initialize to 1 (prime)

res	BYTE	"The list of primes are:",13,10,0

.code
; EBX = current tentative prime
; ECX = multiples of a prime
main	PROC
; start the output
	mov		edx,OFFSET res		; label our prime output
	call		WriteString

; start scanning the array
	mov		ebx,2*4			; ignore 0, 1
scan_lp: 
	mov		edi,primelist[ebx]
	cmp		edi,1			; is it prime?
	jne		not_prime

	mov		EAX,EBX			; copy the prime
	shr		eax,2			; divide by 4 to get actual prime
	call		WriteInt		; display this prime
	call		CrLf			; do it one per line

; mark off duplicates of the prime
	mov		ecx,ebx			; copy the prime*4
	shl		ecx,1			; calc 2 * prime
	xor		edi,edi			; set edi to 0
cancel_lp:
	cmp		ecx,MAXVAL*4		; at/beyond the end of our array?
	jae		not_prime		; if so, done here

	mov		primelist[ecx],edi	; clear multiples of the prime
	add		ecx,ebx			; move to the next multiple
	jmp		cancel_lp

; move on to the next number
not_prime:
	add		ebx,4			; move to next prime*4
	cmp		ebx,MAXVAL*4		; are we done?
	jb		scan_lp

	exit
main	ENDP
END	main

