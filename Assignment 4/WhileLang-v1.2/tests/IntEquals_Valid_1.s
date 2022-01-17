
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	movq 32(%rbp), %rax
	movq 24(%rbp), %rbx
	cmpq %rax, %rbx
	jnz label972
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $69, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $81, %rbx
	movq %rbx, 32(%rax)
	jmp label970
	jmp label971
label972:
	movq $104, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $6, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $78, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $79, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $84, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $32, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $69, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $81, %rbx
	movq %rbx, 96(%rax)
	jmp label970
label971:
label970:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $32, %rsp
	movq $4, %rax
	movq %rax, 8(%rsp)
	movq $1, %rax
	movq %rax, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $104, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $6, %rcx
	movq %rcx, 0(%rbx)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $78, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $79, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $84, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $32, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $69, %rcx
	movq %rcx, 80(%rbx)
	movq $0, %rcx
	movq %rcx, 88(%rbx)
	movq $81, %rcx
	movq %rcx, 96(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label974
	movq $1, %rax
	jmp label975
label974:
	movq $0, %rax
label975:
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $4, %rax
	movq %rax, 8(%rsp)
	movq $1, %rax
	movq %rax, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $104, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $6, %rcx
	movq %rcx, 0(%rbx)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $78, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $79, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $84, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $32, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $69, %rcx
	movq %rcx, 80(%rbx)
	movq $0, %rcx
	movq %rcx, 88(%rbx)
	movq $81, %rcx
	movq %rcx, 96(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label976
	movq $1, %rax
	jmp label977
label976:
	movq $0, %rax
label977:
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $0, %rax
	movq %rax, 8(%rsp)
	movq $0, %rax
	movq %rax, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $40, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $2, %rcx
	movq %rcx, 0(%rbx)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $69, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $81, %rcx
	movq %rcx, 32(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label978
	movq $1, %rax
	jmp label979
label978:
	movq $0, %rax
label979:
	movq %rax, %rdi
	call _assertion
label973:
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
