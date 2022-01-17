
	.text
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -8(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $1, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $0, %rbx
	movq %rbx, 32(%rax)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq 16(%rax), %rax
	cmpq $0, %rax
	jz label761
	movq $1, %rax
	movq %rax, %rdi
	call _assertion
	jmp label760
label761:
	movq $0, %rax
	movq %rax, %rdi
	call _assertion
label760:
label759:
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
