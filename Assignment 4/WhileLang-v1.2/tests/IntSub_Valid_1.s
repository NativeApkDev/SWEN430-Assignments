
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	movq 40(%rbp), %rax
	movq 32(%rbp), %rbx
	subq %rbx, %rax
	movq 24(%rbp), %rbx
	subq %rbx, %rax
	movq %rax, 16(%rbp)
	jmp label221
label221:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $32, %rsp
	movq $3, %rax
	movq %rax, 8(%rsp)
	movq $2, %rax
	movq %rax, 16(%rsp)
	movq $1, %rax
	movq %rax, 24(%rsp)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $-4, %rbx
	cmpq %rax, %rbx
	jnz label223
	movq $1, %rax
	jmp label224
label223:
	movq $0, %rax
label224:
	movq %rax, %rdi
	call _assertion
label222:
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
