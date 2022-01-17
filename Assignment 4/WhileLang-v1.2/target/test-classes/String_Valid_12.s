
	.text
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $88, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $5, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -8(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $72, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $101, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $108, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $108, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $111, %rbx
	movq %rbx, 80(%rax)
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -16(%rbp)
	movq $1, %rbx
	movq %rbx, 8(%rax)
	movq $88, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $5, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, 16(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $72, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $101, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $108, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $108, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $111, %rcx
	movq %rcx, 80(%rbx)
	movq $1, %rbx
	movq %rbx, 24(%rax)
	movq $88, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $5, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, 32(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $87, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $111, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $114, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $108, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $100, %rcx
	movq %rcx, 80(%rbx)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq -16(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, -16(%rbp)
	movq -16(%rbp), %rbx
	movq $0, %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1458
	movq $1, %rax
	jmp label1459
label1458:
	movq $0, %rax
label1459:
	movq %rax, %rdi
	call _assertion
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq -16(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, -16(%rbp)
	movq -16(%rbp), %rbx
	movq $1, %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jnz label1460
	movq $1, %rax
	jmp label1461
label1460:
	movq $0, %rax
label1461:
	movq %rax, %rdi
	call _assertion
label1457:
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
