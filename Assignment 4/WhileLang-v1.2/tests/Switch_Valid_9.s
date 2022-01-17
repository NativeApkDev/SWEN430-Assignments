
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	movq 24(%rbp), %rax
	movq $1, %rbx
	cmpq %rax, %rbx
	jnz label514
label513:
	movq $0, %rbx
	movq %rbx, 16(%rbp)
	jmp label511
	jmp label515
label514:
	movq $0, %rbx
	cmpq %rax, %rbx
	jnz label516
label515:
	movq $1, %rbx
	movq %rbx, 16(%rbp)
	jmp label511
label516:
label512:
	movq $3, %rax
	movq %rax, 16(%rbp)
	jmp label511
label511:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	movq $0, %rax
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
	jnz label518
	movq $1, %rax
	jmp label519
label518:
	movq $0, %rax
label519:
	movq %rax, %rdi
	call _assertion
	movq $1, %rax
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $0, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	cmpq %rax, %rbx
	jnz label520
	movq $1, %rax
	jmp label521
label520:
	movq $0, %rax
label521:
	movq %rax, %rdi
	call _assertion
label517:
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
