
	.text
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $24, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $1, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -8(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $0, %rbx
	movq %rbx, 16(%rax)
	movq -8(%rbp), %rax
	movq -8(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, -8(%rbp)
	movq -8(%rbp), %rbx
	movq 16(%rbx), %rbx
	movq $123, %rcx
	addq %rcx, %rbx
	movq %rbx, 16(%rax)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq 16(%rax), %rax
	movq $123, %rbx
	cmpq %rax, %rbx
	jnz label1384
	movq $1, %rax
	jmp label1385
label1384:
	movq $0, %rax
label1385:
	movq %rax, %rdi
	call _assertion
label1383:
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
