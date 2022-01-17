
	.text
wl_cumulativeSum:
	pushq %rbp
	movq %rsp, %rbp
	subq $32, %rsp
	movq $0, %rax
	movq %rax, -16(%rbp)
	movq -16(%rbp), %rax
	movq %rax, -24(%rbp)
	movq $0, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq 24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, 24(%rbp)
	movq 24(%rbp), %rbx
	movq -24(%rbp), %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	addq %rbx, %rax
	movq %rax, -8(%rbp)
	movq -24(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -24(%rbp)
label878:
	movq -24(%rbp), %rax
	movq 24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, 24(%rbp)
	movq 24(%rbp), %rbx
	movq 0(%rbx), %rbx
	cmpq %rbx, %rax
	jge label879
	movq -8(%rbp), %rax
	movq 24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, 24(%rbp)
	movq 24(%rbp), %rbx
	movq -24(%rbp), %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	addq %rbx, %rax
	movq %rax, -8(%rbp)
	movq -24(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -24(%rbp)
	jmp label878
label879:
	movq 24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, 24(%rbp)
	movq 24(%rbp), %rax
	movq -16(%rbp), %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq -8(%rbp), %rbx
	movq %rbx, 0(%rax)
	movq -16(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
label876:
	movq -16(%rbp), %rax
	movq 24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, 24(%rbp)
	movq 24(%rbp), %rbx
	movq 0(%rbx), %rbx
	cmpq %rbx, %rax
	jge label877
	movq -16(%rbp), %rax
	movq %rax, -24(%rbp)
	movq $0, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq 24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, 24(%rbp)
	movq 24(%rbp), %rbx
	movq -24(%rbp), %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	addq %rbx, %rax
	movq %rax, -8(%rbp)
	movq -24(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -24(%rbp)
label880:
	movq -24(%rbp), %rax
	movq 24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, 24(%rbp)
	movq 24(%rbp), %rbx
	movq 0(%rbx), %rbx
	cmpq %rbx, %rax
	jge label881
	movq -8(%rbp), %rax
	movq 24(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, 24(%rbp)
	movq 24(%rbp), %rbx
	movq -24(%rbp), %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	addq %rbx, %rax
	movq %rax, -8(%rbp)
	movq -24(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -24(%rbp)
	jmp label880
label881:
	movq 24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, 24(%rbp)
	movq 24(%rbp), %rax
	movq -16(%rbp), %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq -8(%rbp), %rbx
	movq %rbx, 0(%rax)
	movq -16(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
	jmp label876
label877:
	movq 24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, 24(%rbp)
	movq 24(%rbp), %rax
	movq %rax, 16(%rbp)
	jmp label875
label875:
	movq %rbp, %rsp
	popq %rbp
	ret
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
	movq %rax, 8(%rsp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $1, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $2, %rbx
	movq %rbx, 32(%rax)
	call wl_cumulativeSum
	addq $16, %rsp
	movq -16(%rsp), %rax
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
	movq $3, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $2, %rcx
	movq %rcx, 32(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label883
	movq $1, %rax
	jmp label884
label883:
	movq $0, %rax
label884:
	movq %rax, %rdi
	call _assertion
	subq $16, %rsp
	movq $72, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $4, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 8(%rsp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $1, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $2, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $3, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $1, %rbx
	movq %rbx, 64(%rax)
	call wl_cumulativeSum
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $72, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $4, %rcx
	movq %rcx, 0(%rbx)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $7, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $6, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $4, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $1, %rcx
	movq %rcx, 64(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label885
	movq $1, %rax
	jmp label886
label885:
	movq $0, %rax
label886:
	movq %rax, %rdi
	call _assertion
label882:
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
