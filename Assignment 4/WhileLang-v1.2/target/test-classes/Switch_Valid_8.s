
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	movq 24(%rbp), %rax
	movq $1, %rbx
	cmpq %rax, %rbx
	jnz label499
label498:
	movq $123, %rbx
	movq %rbx, 16(%rbp)
	jmp label496
	jmp label500
label499:
	movq $2, %rbx
	cmpq %rax, %rbx
	jnz label501
label500:
	movq $234, %rbx
	movq %rbx, 16(%rbp)
	jmp label496
	jmp label502
label501:
label502:
	movq $456, %rbx
	movq %rbx, 16(%rbp)
	jmp label496
label503:
label497:
label496:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	movq $123, %rax
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $1, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	cmpq %rax, %rbx
	jnz label505
	movq $1, %rax
	jmp label506
label505:
	movq $0, %rax
label506:
	movq %rax, %rdi
	call _assertion
	movq $234, %rax
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $2, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	cmpq %rax, %rbx
	jnz label507
	movq $1, %rax
	jmp label508
label507:
	movq $0, %rax
label508:
	movq %rax, %rdi
	call _assertion
	movq $456, %rax
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $3, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	cmpq %rax, %rbx
	jnz label509
	movq $1, %rax
	jmp label510
label509:
	movq $0, %rax
label510:
	movq %rax, %rdi
	call _assertion
label504:
	movq %rbp, %rsp
	popq %rbp
	ret
	.globl _main
_main:
	pushq %rbp
	call wl_main
	popq %rbp
	movq $0, %rax
	ret

	.data
