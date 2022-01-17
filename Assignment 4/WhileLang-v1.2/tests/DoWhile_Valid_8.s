
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	jmp label291
label290:
	movq $1, %rax
	cmpq $0, %rax
	jz label291
	jmp label291
	jmp label290
label291:
	movq $-1, %rax
	movq %rax, 16(%rbp)
	jmp label289
label289:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	call wl_f
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $-1, %rbx
	cmpq %rax, %rbx
	jnz label293
	movq $1, %rax
	jmp label294
label293:
	movq $0, %rax
label294:
	movq %rax, %rdi
	call _assertion
label292:
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
