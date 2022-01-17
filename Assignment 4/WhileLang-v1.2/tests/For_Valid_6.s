
	.text
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $32, %rsp
	movq $0, %rax
	movq %rax, -16(%rbp)
	movq $88, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $5, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -24(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $40, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $50, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $30, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $20, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $10, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rax
	movq %rax, -8(%rbp)
label944:
	movq -8(%rbp), %rax
	movq -24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, -24(%rbp)
	movq -24(%rbp), %rbx
	movq 0(%rbx), %rbx
	cmpq %rax, %rbx
	jz label946
	movq -16(%rbp), %rax
	movq -24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, -24(%rbp)
	movq -24(%rbp), %rbx
	movq -8(%rbp), %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
label945:
	movq -8(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -8(%rbp)
	jmp label944
label946:
	movq -16(%rbp), %rax
	movq $150, %rbx
	cmpq %rax, %rbx
	jnz label947
	movq $1, %rax
	jmp label948
label947:
	movq $0, %rax
label948:
	movq %rax, %rdi
	call _assertion
	movq $56, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $3, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -24(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $5, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $3, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $2, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rax
	movq %rax, -8(%rbp)
label949:
	movq -8(%rbp), %rax
	movq -24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, -24(%rbp)
	movq -24(%rbp), %rbx
	movq 0(%rbx), %rbx
	cmpq %rax, %rbx
	jz label951
	movq -16(%rbp), %rax
	movq -24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, -24(%rbp)
	movq -24(%rbp), %rbx
	movq -8(%rbp), %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
label950:
	movq -8(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -8(%rbp)
	jmp label949
label951:
	movq -16(%rbp), %rax
	movq $160, %rbx
	cmpq %rax, %rbx
	jnz label952
	movq $1, %rax
	jmp label953
label952:
	movq $0, %rax
label953:
	movq %rax, %rdi
	call _assertion
label943:
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
