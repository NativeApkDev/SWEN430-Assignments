
	.text
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $56, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $3, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -8(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $1, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $0, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $1, %rbx
	movq %rbx, 48(%rax)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
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
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $0, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $1, %rcx
	movq %rcx, 48(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label707
	movq $1, %rax
	jmp label708
label707:
	movq $0, %rax
label708:
	movq %rax, %rdi
	call _assertion
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq $0, %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq 0(%rax), %rax
	movq $1, %rbx
	cmpq %rax, %rbx
	jnz label709
	movq $1, %rax
	jmp label710
label709:
	movq $0, %rax
label710:
	movq %rax, %rdi
	call _assertion
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq $0, %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq $0, %rbx
	movq %rbx, 0(%rax)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
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
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $0, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $0, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $1, %rcx
	movq %rcx, 48(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label711
	movq $1, %rax
	jmp label712
label711:
	movq $0, %rax
label712:
	movq %rax, %rdi
	call _assertion
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq $0, %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq 0(%rax), %rax
	movq $0, %rbx
	cmpq %rax, %rbx
	jnz label713
	movq $1, %rax
	jmp label714
label713:
	movq $0, %rax
label714:
	movq %rax, %rdi
	call _assertion
label706:
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
