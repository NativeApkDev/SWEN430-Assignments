
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	movq 24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, 24(%rbp)
	movq 24(%rbp), %rax
	movq 0(%rax), %rax
	movq %rax, 16(%rbp)
	jmp label1386
label1386:
	movq %rbp, %rsp
	popq %rbp
	ret
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
	movq $1, %rbx
	movq %rbx, 8(%rax)
	movq $56, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $3, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, 16(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $2, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $3, %rcx
	movq %rcx, 48(%rbx)
	subq $16, %rsp
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq $0, %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq 0(%rax), %rax
	movq %rax, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $3, %rbx
	cmpq %rax, %rbx
	jnz label1388
	movq $1, %rax
	jmp label1389
label1388:
	movq $0, %rax
label1389:
	movq %rax, %rdi
	call _assertion
label1387:
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
