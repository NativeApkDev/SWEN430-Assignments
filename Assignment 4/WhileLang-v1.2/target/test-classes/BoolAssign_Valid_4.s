
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $1, %rax
	movq %rax, -8(%rbp)
	movq 32(%rbp), %rax
	movq 24(%rbp), %rbx
	cmpq %rbx, %rax
	jge label33
	movq $0, %rax
	movq %rax, -8(%rbp)
	jmp label33
label33:
	movq -8(%rbp), %rax
	cmpq $0, %rax
	jz label36
	jmp label35
label36:
	movq 32(%rbp), %rax
	movq 24(%rbp), %rbx
	addq %rbx, %rax
	movq %rax, 16(%rbp)
	jmp label32
	jmp label34
label35:
	movq $123, %rax
	movq %rax, 16(%rbp)
	jmp label32
label34:
label32:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $32, %rsp
	movq $1, %rax
	movq %rax, 8(%rsp)
	movq $1, %rax
	movq %rax, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $123, %rbx
	cmpq %rax, %rbx
	jnz label38
	movq $1, %rax
	jmp label39
label38:
	movq $0, %rax
label39:
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $11, %rax
	movq %rax, 8(%rsp)
	movq $0, %rax
	movq %rax, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $11, %rbx
	cmpq %rax, %rbx
	jnz label40
	movq $1, %rax
	jmp label41
label40:
	movq $0, %rax
label41:
	movq %rax, %rdi
	call _assertion
	subq $32, %rsp
	movq $0, %rax
	movq %rax, 8(%rsp)
	movq $11, %rax
	movq %rax, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq -32(%rsp), %rax
	movq $123, %rbx
	cmpq %rax, %rbx
	jnz label42
	movq $1, %rax
	jmp label43
label42:
	movq $0, %rax
label43:
	movq %rax, %rdi
	call _assertion
label37:
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
