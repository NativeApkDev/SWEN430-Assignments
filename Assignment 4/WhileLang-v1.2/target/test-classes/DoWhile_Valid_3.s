
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
label284:
	movq $0, %rax
	cmpq $0, %rax
	jz label285
	jmp label284
label285:
	movq $-1, %rax
	movq %rax, 16(%rbp)
	jmp label283
label283:
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
	jnz label287
	movq $1, %rax
	jmp label288
label287:
	movq $0, %rax
label288:
	movq %rax, %rdi
	call _assertion
label286:
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
