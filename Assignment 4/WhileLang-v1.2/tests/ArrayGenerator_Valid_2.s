
	.text
wl_abs:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $0, %rax
	movq %rax, -16(%rbp)
	movq 24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, 24(%rbp)
	movq 24(%rbp), %rax
	movq 0(%rax), %rax
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
label654:
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
	jge label655
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
	movq 0(%rax), %rax
	movq $0, %rbx
	cmpq %rbx, %rax
	jge label657
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
	movq -16(%rbp), %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	negq %rbx
	movq %rbx, 0(%rax)
	jmp label656
label657:
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
	movq -16(%rbp), %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	movq %rbx, 0(%rax)
label656:
	movq -16(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
	jmp label654
label655:
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, 16(%rbp)
	jmp label653
label653:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	subq $16, %rsp
	movq $216, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $13, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 8(%rsp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $-1, %rbx
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
	movq $-4, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $5, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $6, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $7, %rbx
	movq %rbx, 112(%rax)
	movq $0, %rbx
	movq %rbx, 120(%rax)
	movq $23987, %rbx
	movq %rbx, 128(%rax)
	movq $0, %rbx
	movq %rbx, 136(%rax)
	movq $-23897, %rbx
	movq %rbx, 144(%rax)
	movq $0, %rbx
	movq %rbx, 152(%rax)
	movq $0, %rbx
	movq %rbx, 160(%rax)
	movq $0, %rbx
	movq %rbx, 168(%rax)
	movq $-1, %rbx
	movq %rbx, 176(%rax)
	movq $0, %rbx
	movq %rbx, 184(%rax)
	movq $1, %rbx
	movq %rbx, 192(%rax)
	movq $0, %rbx
	movq %rbx, 200(%rax)
	movq $-2389, %rbx
	movq %rbx, 208(%rax)
	call wl_abs
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq $216, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $13, %rcx
	movq %rcx, 0(%rbx)
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
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $4, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $5, %rcx
	movq %rcx, 80(%rbx)
	movq $0, %rcx
	movq %rcx, 88(%rbx)
	movq $6, %rcx
	movq %rcx, 96(%rbx)
	movq $0, %rcx
	movq %rcx, 104(%rbx)
	movq $7, %rcx
	movq %rcx, 112(%rbx)
	movq $0, %rcx
	movq %rcx, 120(%rbx)
	movq $23987, %rcx
	movq %rcx, 128(%rbx)
	movq $0, %rcx
	movq %rcx, 136(%rbx)
	movq $23897, %rcx
	movq %rcx, 144(%rbx)
	movq $0, %rcx
	movq %rcx, 152(%rbx)
	movq $0, %rcx
	movq %rcx, 160(%rbx)
	movq $0, %rcx
	movq %rcx, 168(%rbx)
	movq $1, %rcx
	movq %rcx, 176(%rbx)
	movq $0, %rcx
	movq %rcx, 184(%rbx)
	movq $1, %rcx
	movq %rcx, 192(%rbx)
	movq $0, %rcx
	movq %rcx, 200(%rbx)
	movq $2389, %rcx
	movq %rcx, 208(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label659
	movq $1, %rax
	jmp label660
label659:
	movq $0, %rax
label660:
	movq %rax, %rdi
	call _assertion
	subq $16, %rsp
	movq $8, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $0, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 8(%rsp)
	call wl_abs
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq $8, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $0, %rcx
	movq %rcx, 0(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label661
	movq $1, %rax
	jmp label662
label661:
	movq $0, %rax
label662:
	movq %rax, %rdi
	call _assertion
label658:
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
