
	.text
wl_indexOf:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $0, %rax
	movq %rax, -16(%rbp)
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
	movq 24(%rbp), %rbx
	cmpq %rax, %rbx
	jnz label924
	movq -16(%rbp), %rax
	movq %rax, 16(%rbp)
	jmp label921
	jmp label924
label924:
	movq -16(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
	movq $0, %rax
	movq %rax, -8(%rbp)
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
	jge label925
	movq $1, %rax
	movq %rax, -8(%rbp)
	jmp label922
	jmp label925
label925:
label922:
	movq -8(%rbp), %rax
	cmpq $0, %rax
	jz label923
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
	movq 24(%rbp), %rbx
	cmpq %rax, %rbx
	jnz label926
	movq -16(%rbp), %rax
	movq %rax, 16(%rbp)
	jmp label921
	jmp label926
label926:
	movq -16(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
	movq $0, %rax
	movq %rax, -8(%rbp)
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
	jge label927
	movq $1, %rax
	movq %rax, -8(%rbp)
	jmp label922
	jmp label927
label927:
	jmp label922
label923:
	movq $-1, %rax
	movq %rax, 16(%rbp)
	jmp label921
label921:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $32, %rsp
	movq $1, %rax
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
	movq $1, %rbx
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
	movq $1, %rbx
	movq %rbx, 64(%rax)
	call wl_indexOf
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $0, %rbx
	cmpq %rax, %rbx
	jnz label929
	movq $1, %rax
	jmp label930
label929:
	movq $0, %rax
label930:
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $2, %rax
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
	movq $1, %rbx
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
	movq $1, %rbx
	movq %rbx, 64(%rax)
	call wl_indexOf
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $1, %rbx
	cmpq %rax, %rbx
	jnz label931
	movq $1, %rax
	jmp label932
label931:
	movq $0, %rax
label932:
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $3, %rax
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
	movq $1, %rbx
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
	movq $1, %rbx
	movq %rbx, 64(%rax)
	call wl_indexOf
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $2, %rbx
	cmpq %rax, %rbx
	jnz label933
	movq $1, %rax
	jmp label934
label933:
	movq $0, %rax
label934:
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
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $0, %rbx
	movq %rbx, 16(%rax)
	call wl_indexOf
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $-1, %rbx
	cmpq %rax, %rbx
	jnz label935
	movq $1, %rax
	jmp label936
label935:
	movq $0, %rax
label936:
	movq %rax, %rdi
	call _assertion
label928:
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
