
	.text
wl_f1:
	pushq %rbp
	movq %rsp, %rbp
	movq $136, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $8, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $71, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $79, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $84, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $32, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $66, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $79, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $79, %rbx
	movq %rbx, 112(%rax)
	movq $0, %rbx
	movq %rbx, 120(%rax)
	movq $76, %rbx
	movq %rbx, 128(%rax)
	jmp label980
label980:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_f2:
	pushq %rbp
	movq %rsp, %rbp
	movq $120, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $7, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $71, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $79, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $84, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $32, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $73, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $78, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $84, %rbx
	movq %rbx, 112(%rax)
	jmp label981
label981:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $1, %rax
	movq %rax, 8(%rsp)
	call wl_f2
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $120, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $7, %rcx
	movq %rcx, 0(%rbx)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $71, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $79, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $84, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $32, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $73, %rcx
	movq %rcx, 80(%rbx)
	movq $0, %rcx
	movq %rcx, 88(%rbx)
	movq $78, %rcx
	movq %rcx, 96(%rbx)
	movq $0, %rcx
	movq %rcx, 104(%rbx)
	movq $84, %rcx
	movq %rcx, 112(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label983
	movq $1, %rax
	jmp label984
label983:
	movq $0, %rax
label984:
	movq %rax, %rdi
	call _assertion
	subq $16, %rsp
	movq $0, %rax
	movq %rax, 8(%rsp)
	call wl_f1
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $136, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $8, %rcx
	movq %rcx, 0(%rbx)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $71, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $79, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $84, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $32, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $66, %rcx
	movq %rcx, 80(%rbx)
	movq $0, %rcx
	movq %rcx, 88(%rbx)
	movq $79, %rcx
	movq %rcx, 96(%rbx)
	movq $0, %rcx
	movq %rcx, 104(%rbx)
	movq $79, %rcx
	movq %rcx, 112(%rbx)
	movq $0, %rcx
	movq %rcx, 120(%rbx)
	movq $76, %rcx
	movq %rcx, 128(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label985
	movq $1, %rax
	jmp label986
label985:
	movq $0, %rax
label986:
	movq %rax, %rdi
	call _assertion
label982:
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
