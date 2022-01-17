
	.text
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $1, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq $1, %rbx
	cmpq %rax, %rbx
	jnz label1
	movq $1, %rax
	jmp label2
label1:
	movq $0, %rax
label2:
	movq %rax, %rdi
	call _assertion
	movq $0, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq $0, %rbx
	cmpq %rax, %rbx
	jnz label3
	movq $1, %rax
	jmp label4
label3:
	movq $0, %rax
label4:
	movq %rax, %rdi
	call _assertion
label0:
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
