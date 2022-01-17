
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	movq 24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, 24(%rbp)
	movq 24(%rbp), %rax
	movq %rax, 16(%rbp)
	jmp label1054
label1054:
	movq %rbp, %rsp
	popq %rbp
	ret
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
	subq $16, %rsp
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq -16(%rsp), %rax
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
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1056
	movq $1, %rax
	jmp label1057
label1056:
	movq $0, %rax
label1057:
	movq %rax, %rdi
	call _assertion
label1055:
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
