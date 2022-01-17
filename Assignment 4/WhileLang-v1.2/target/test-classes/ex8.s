
	.text
wl_mul:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $0, %rax
	movq %rax, -8(%rbp)
	movq $0, %rax
	movq %rax, -16(%rbp)
label553:
	movq -16(%rbp), %rax
	movq 32(%rbp), %rbx
	cmpq %rbx, %rax
	jge label554
	movq -8(%rbp), %rax
	movq 24(%rbp), %rbx
	addq %rbx, %rax
	movq %rax, -8(%rbp)
	movq -16(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
	jmp label553
label554:
	movq -8(%rbp), %rax
	movq %rax, 16(%rbp)
	jmp label552
label552:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $32, %rsp
	movq $3, %rax
	movq %rax, 8(%rsp)
	movq $5, %rax
	movq %rax, 16(%rsp)
	call wl_mul
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $15, %rbx
	cmpq %rax, %rbx
	jnz label556
	movq $1, %rax
	jmp label557
label556:
	movq $0, %rax
label557:
	movq %rax, %rdi
	call _assertion
label555:
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
