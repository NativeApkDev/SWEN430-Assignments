
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	subq $48, %rsp
	movq $0, %rax
	movq %rax, -8(%rbp)
	movq $0, %rax
	movq %rax, -16(%rbp)
label1431:
	movq -16(%rbp), %rax
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
	movq 0(%rbx), %rbx
	cmpq %rbx, %rax
	jge label1433
	movq 32(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, 32(%rbp)
	movq 32(%rbp), %rax
	movq -16(%rbp), %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq 0(%rax), %rax
	movq %rax, -24(%rbp)
	movq $0, %rax
	movq %rax, -32(%rbp)
	movq $0, %rax
	movq %rax, -40(%rbp)
label1434:
	movq -40(%rbp), %rax
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
	cmpq %rbx, %rax
	jge label1436
	movq -24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -24(%rbp)
	movq -24(%rbp), %rax
	movq -40(%rbp), %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq 0(%rax), %rax
	movq 24(%rbp), %rbx
	cmpq %rax, %rbx
	jnz label1437
	movq $1, %rax
	movq %rax, -32(%rbp)
	jmp label1436
	jmp label1437
label1437:
label1435:
	movq -40(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -40(%rbp)
	jmp label1434
label1436:
	movq -32(%rbp), %rax
	cmpq $0, %rax
	jz label1439
	jmp label1438
label1439:
	movq $0, %rax
	movq %rax, 16(%rbp)
	jmp label1430
	jmp label1438
label1438:
label1432:
	movq -16(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
	jmp label1431
label1433:
	movq $1, %rax
	movq %rax, 16(%rbp)
	jmp label1430
label1430:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $32, %rsp
	movq $1, %rax
	movq %rax, 8(%rsp)
	movq $24, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $1, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
	movq $1, %rbx
	movq %rbx, 8(%rax)
	movq $24, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $1, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, 16(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $1, %rax
	movq %rax, 8(%rsp)
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
	movq $1, %rbx
	movq %rbx, 8(%rax)
	movq $24, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $1, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, 16(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	movq $1, %rbx
	movq %rbx, 24(%rax)
	movq $24, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $1, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, 32(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $1, %rax
	movq %rax, 8(%rsp)
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
	movq $1, %rbx
	movq %rbx, 8(%rax)
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
	movq %rbx, 16(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $2, %rcx
	movq %rcx, 32(%rbx)
	movq $1, %rbx
	movq %rbx, 24(%rax)
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
	movq %rbx, 32(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $2, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $1, %rcx
	movq %rcx, 32(%rbx)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $2, %rax
	movq %rax, 8(%rsp)
	movq $56, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $3, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
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
	movq $1, %rbx
	movq %rbx, 24(%rax)
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
	movq %rbx, 32(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $3, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $2, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $1, %rcx
	movq %rcx, 48(%rbx)
	movq $1, %rbx
	movq %rbx, 40(%rax)
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
	movq %rbx, 48(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $3, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $2, %rcx
	movq %rcx, 48(%rbx)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $2, %rax
	movq %rax, 8(%rsp)
	movq $24, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $1, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
	movq $1, %rbx
	movq %rbx, 8(%rax)
	movq $24, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $1, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, 16(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	notq %rax
	andq $1, %rax
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $2, %rax
	movq %rax, 8(%rsp)
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
	movq $1, %rbx
	movq %rbx, 8(%rax)
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
	movq %rbx, 16(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $2, %rcx
	movq %rcx, 32(%rbx)
	movq $1, %rbx
	movq %rbx, 24(%rax)
	movq $24, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $1, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, 32(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	notq %rax
	andq $1, %rax
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $2, %rax
	movq %rax, 8(%rsp)
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rsp)
	movq $1, %rbx
	movq %rbx, 8(%rax)
	movq $24, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $1, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, 16(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	movq $1, %rbx
	movq %rbx, 24(%rax)
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
	movq %rbx, 32(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $2, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $1, %rcx
	movq %rcx, 32(%rbx)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	notq %rax
	andq $1, %rax
	movq %rax, %rdi
	call _assertion
label1440:
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
