
	.text
wl_trim:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $0, %rax
	movq %rax, -16(%rbp)
	movq 24(%rbp), %rax
	movq %rax, %rbx
	imulq $2, %rbx
	incq %rbx
	imulq $8, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rax, 0(%rbx)
	movq %rbx, -8(%rbp)
	movq $0, %rcx
	addq $8, %rbx
	subq $32, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, 8(%rsp)
	movq %rcx, 16(%rsp)
	movq %rbx, %rdi
	movq %rcx, %rsi
	call _objfill
	movq 0(%rsp), %rax
	movq 8(%rsp), %rbx
	movq 16(%rsp), %rcx
	addq $32, %rsp
label1318:
	movq -16(%rbp), %rax
	movq 24(%rbp), %rbx
	cmpq %rbx, %rax
	jge label1319
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq -16(%rbp), %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
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
	movq -16(%rbp), %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	movq %rbx, 0(%rax)
	movq -16(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
	jmp label1318
label1319:
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, 16(%rbp)
	jmp label1317
label1317:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	subq $32, %rsp
	movq $4, %rax
	movq %rax, 8(%rsp)
	movq $152, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $9, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $-2, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $-3, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $1, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $2, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $-23, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $3, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $2345, %rbx
	movq %rbx, 112(%rax)
	movq $0, %rbx
	movq %rbx, 120(%rax)
	movq $4, %rbx
	movq %rbx, 128(%rax)
	movq $0, %rbx
	movq %rbx, 136(%rax)
	movq $5, %rbx
	movq %rbx, 144(%rax)
	call wl_trim
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
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
	movq $-2, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $-3, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $1, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $2, %rcx
	movq %rcx, 64(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1321
	movq $1, %rax
	jmp label1322
label1321:
	movq $0, %rax
label1322:
	movq %rax, %rdi
	call _assertion
label1320:
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
