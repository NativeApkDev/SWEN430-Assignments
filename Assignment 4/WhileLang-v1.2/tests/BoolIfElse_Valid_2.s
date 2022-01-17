
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	movq 24(%rbp), %rax
	movq $1, %rbx
	cmpq %rax, %rbx
	jnz label740
	movq $72, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $4, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $84, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $82, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $85, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $69, %rbx
	movq %rbx, 64(%rax)
	jmp label738
	jmp label739
label740:
	movq $88, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $5, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $70, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $65, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $76, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $83, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $69, %rbx
	movq %rbx, 80(%rax)
	jmp label738
label739:
label738:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	movq $72, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $4, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $84, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $82, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $85, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $69, %rbx
	movq %rbx, 64(%rax)
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
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label742
	movq $1, %rax
	jmp label743
label742:
	movq $0, %rax
label743:
	movq %rax, %rdi
	call _assertion
	movq $88, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $5, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $70, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $65, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $76, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $83, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $69, %rbx
	movq %rbx, 80(%rax)
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
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label744
	movq $1, %rax
	jmp label745
label744:
	movq $0, %rax
label745:
	movq %rax, %rdi
	call _assertion
label741:
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
