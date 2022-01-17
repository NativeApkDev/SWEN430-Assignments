
	.text
wl_has:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $0, %rax
	movq %rax, -8(%rbp)
label1448:
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
	movq 0(%rbx), %rbx
	cmpq %rbx, %rax
	jge label1449
	movq 24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, 24(%rbp)
	movq 24(%rbp), %rax
	movq -8(%rbp), %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq 0(%rax), %rax
	movq 32(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, 32(%rbp)
	movq 32(%rbp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1450
	movq $1, %rax
	movq %rax, 16(%rbp)
	jmp label1447
	jmp label1450
label1450:
	movq -8(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -8(%rbp)
	jmp label1448
label1449:
	movq $0, %rax
	movq %rax, 16(%rbp)
	jmp label1447
label1447:
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
	movq %rax, -8(%rbp)
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
	subq $32, %rsp
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, 8(%rsp)
	movq $8, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $0, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
	call wl_has
	addq $32, %rsp
	movq -32(%rsp), %rax
	notq %rax
	andq $1, %rax
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, 8(%rsp)
	movq $88, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $5, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $104, %rbx
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
	call wl_has
	addq $32, %rsp
	movq -32(%rsp), %rax
	notq %rax
	andq $1, %rax
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, 8(%rsp)
	movq $88, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $5, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
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
	call wl_has
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, 8(%rsp)
	movq $56, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $3, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
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
	call wl_has
	addq $32, %rsp
	movq -32(%rsp), %rax
	notq %rax
	andq $1, %rax
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, 8(%rsp)
	movq $72, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $4, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $98, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $108, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $97, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $104, %rbx
	movq %rbx, 64(%rax)
	call wl_has
	addq $32, %rsp
	movq -32(%rsp), %rax
	notq %rax
	andq $1, %rax
	movq %rax, %rdi
	call _assertion
label1451:
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
