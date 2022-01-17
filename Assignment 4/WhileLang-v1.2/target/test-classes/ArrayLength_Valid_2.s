
	.text
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	movq $8, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $0, %rbx
	movq %rbx, 0(%rax)
	movq 0(%rax), %rax
	movq $0, %rbx
	cmpq %rax, %rbx
	jnz label1422
	movq $1, %rax
	jmp label1423
label1422:
	movq $0, %rax
label1423:
	movq %rax, %rdi
	call _assertion
	movq $24, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $1, %rbx
	movq %rbx, 0(%rax)
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
	movq 0(%rax), %rax
	movq $1, %rbx
	cmpq %rax, %rbx
	jnz label1424
	movq $1, %rax
	jmp label1425
label1424:
	movq $0, %rax
label1425:
	movq %rax, %rdi
	call _assertion
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
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
	movq 0(%rax), %rax
	movq $2, %rbx
	cmpq %rax, %rbx
	jnz label1426
	movq $1, %rax
	jmp label1427
label1426:
	movq $0, %rax
label1427:
	movq %rax, %rdi
	call _assertion
	movq $56, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $3, %rbx
	movq %rbx, 0(%rax)
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
	movq $1, %rbx
	movq %rbx, 40(%rax)
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
	movq %rbx, 48(%rax)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	movq 0(%rax), %rax
	movq $3, %rbx
	cmpq %rax, %rbx
	jnz label1428
	movq $1, %rax
	jmp label1429
label1428:
	movq $0, %rax
label1429:
	movq %rax, %rdi
	call _assertion
label1421:
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
