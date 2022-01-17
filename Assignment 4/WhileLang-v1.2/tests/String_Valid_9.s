
	.text
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $184, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $11, %rbx
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
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $32, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $87, %rbx
	movq %rbx, 112(%rax)
	movq $0, %rbx
	movq %rbx, 120(%rax)
	movq $111, %rbx
	movq %rbx, 128(%rax)
	movq $0, %rbx
	movq %rbx, 136(%rax)
	movq $114, %rbx
	movq %rbx, 144(%rax)
	movq $0, %rbx
	movq %rbx, 152(%rax)
	movq $108, %rbx
	movq %rbx, 160(%rax)
	movq $0, %rbx
	movq %rbx, 168(%rax)
	movq $100, %rbx
	movq %rbx, 176(%rax)
	movq $24, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $1, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -16(%rbp)
	movq $1, %rbx
	movq %rbx, 8(%rax)
	movq -8(%rbp), %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _objcpy
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq %rbx, -8(%rbp)
	movq -8(%rbp), %rbx
	movq %rbx, 16(%rax)
	movq -16(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -16(%rbp)
	movq -16(%rbp), %rax
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
	movq $1, %rcx
	movq %rcx, 8(%rbx)
	movq $184, %rcx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, 8(%rsp)
	movq %rcx, %rdi
	call _malloc
	movq %rax, %rcx
	movq 0(%rsp), %rax
	movq 8(%rsp), %rbx
	addq $16, %rsp
	movq $11, %rdx
	movq %rdx, 0(%rcx)
	movq %rcx, 16(%rbx)
	movq $0, %rdx
	movq %rdx, 8(%rcx)
	movq $72, %rdx
	movq %rdx, 16(%rcx)
	movq $0, %rdx
	movq %rdx, 24(%rcx)
	movq $101, %rdx
	movq %rdx, 32(%rcx)
	movq $0, %rdx
	movq %rdx, 40(%rcx)
	movq $108, %rdx
	movq %rdx, 48(%rcx)
	movq $0, %rdx
	movq %rdx, 56(%rcx)
	movq $108, %rdx
	movq %rdx, 64(%rcx)
	movq $0, %rdx
	movq %rdx, 72(%rcx)
	movq $111, %rdx
	movq %rdx, 80(%rcx)
	movq $0, %rdx
	movq %rdx, 88(%rcx)
	movq $32, %rdx
	movq %rdx, 96(%rcx)
	movq $0, %rdx
	movq %rdx, 104(%rcx)
	movq $87, %rdx
	movq %rdx, 112(%rcx)
	movq $0, %rdx
	movq %rdx, 120(%rcx)
	movq $111, %rdx
	movq %rdx, 128(%rcx)
	movq $0, %rdx
	movq %rdx, 136(%rcx)
	movq $114, %rdx
	movq %rdx, 144(%rcx)
	movq $0, %rdx
	movq %rdx, 152(%rcx)
	movq $108, %rdx
	movq %rdx, 160(%rcx)
	movq $0, %rdx
	movq %rdx, 168(%rcx)
	movq $100, %rdx
	movq %rdx, 176(%rcx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1466
	movq $1, %rax
	jmp label1467
label1466:
	movq $0, %rax
label1467:
	movq %rax, %rdi
	call _assertion
	movq -16(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -16(%rbp)
	movq -16(%rbp), %rax
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
	movq $1, %rcx
	movq %rcx, 8(%rbx)
	movq $72, %rcx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, 8(%rsp)
	movq %rcx, %rdi
	call _malloc
	movq %rax, %rcx
	movq 0(%rsp), %rax
	movq 8(%rsp), %rbx
	addq $16, %rsp
	movq $4, %rdx
	movq %rdx, 0(%rcx)
	movq %rcx, 16(%rbx)
	movq $0, %rdx
	movq %rdx, 8(%rcx)
	movq $66, %rdx
	movq %rdx, 16(%rcx)
	movq $0, %rdx
	movq %rdx, 24(%rcx)
	movq $108, %rdx
	movq %rdx, 32(%rcx)
	movq $0, %rdx
	movq %rdx, 40(%rcx)
	movq $97, %rdx
	movq %rdx, 48(%rcx)
	movq $0, %rdx
	movq %rdx, 56(%rcx)
	movq $104, %rdx
	movq %rdx, 64(%rcx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jnz label1468
	movq $1, %rax
	jmp label1469
label1468:
	movq $0, %rax
label1469:
	movq %rax, %rdi
	call _assertion
label1465:
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
