TITLE Check for a valid number (CheckNum.asm)

; Check if we have a number of the form 123.456

INCLUDE Project.inc

.data
prompt	BYTE	"Enter a guess: ",0
big	BYTE	"Too Big",13,10,0
small	BYTE	"Too Small",13,10,0
tries	BYTE	"The number of tries was ",0

.code
; registers
;   ECX = count (# of guesses)
;   EAX = i (user's current guess)
;   EBX = answer (number we are trying to guess)
main    PROC
; generate a random number from 1 to 100
	mov	eax,100
	call	RandomRange		; get a number from 0 to 99
	add	eax,1			; make it 1 to 100
	mov	ebx,eax

; initialize count to 1 try
	mov	ecx,1

; loop until the user gets the result correct
make_guess:
; ask the user for a guess
	mov	edx,offset prompt
	call	WriteString
	call	ReadInt			; get guess

; did they get it right?
	cmp	eax,ebx
	je	correct_ans
	jg	big_num

; their guess was smaller than the answer
	mov	edx,offset small
	call	WriteString
	add	ecx,1			; increment count
	jmp	make_guess

; their guess was larger than the answer
big_num:
	mov	edx,offset big
	call	WriteString
	add	ecx,1			; increment count
	jmp	make_guess

; answer is correct, tell them how many guesses they made
correct_ans:
	mov	edx,offset tries
	call	WriteString
	mov	eax,ecx
	call	WriteInt		; display # guesses
	call	CrLf
done:    exit
main    ENDP
END    main