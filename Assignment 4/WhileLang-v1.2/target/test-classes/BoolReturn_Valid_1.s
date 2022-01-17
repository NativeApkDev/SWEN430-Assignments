
	.text
wl_pred:
	pushq %rbp
	movq %rsp, %rbp
	movq $0, %rax
	movq %rax, 16(%rbp)
	jmp label44
label44:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	call wl_pred
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $0, %rbx
	cmpq %rax, %rbx
	jnz label46
	movq $1, %rax
	jmp label47
label46:
	movq $0, %rax
label47:
	movq %rax, %rdi
	call _assertion
label45:
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
