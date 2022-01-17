
	.text
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	movq $10, %rax
	movq $2, %rbx
	movq %rax, %rax
	cqto
	idivq %rbx
	movq %rax, %rax
	movq $5, %rbx
	cmpq %rax, %rbx
	jnz label190
	movq $1, %rax
	jmp label191
label190:
	movq $0, %rax
label191:
	movq %rax, %rdi
	call _assertion
	movq $10, %rax
	movq $3, %rbx
	movq %rax, %rax
	cqto
	idivq %rbx
	movq %rax, %rax
	movq $3, %rbx
	cmpq %rax, %rbx
	jnz label192
	movq $1, %rax
	jmp label193
label192:
	movq $0, %rax
label193:
	movq %rax, %rdi
	call _assertion
	movq $-10, %rax
	movq $3, %rbx
	movq %rax, %rax
	cqto
	idivq %rbx
	movq %rax, %rax
	movq $-3, %rbx
	cmpq %rax, %rbx
	jnz label194
	movq $1, %rax
	jmp label195
label194:
	movq $0, %rax
label195:
	movq %rax, %rdi
	call _assertion
	movq $1, %rax
	movq $4, %rbx
	movq %rax, %rax
	cqto
	idivq %rbx
	movq %rax, %rax
	movq $0, %rbx
	cmpq %rax, %rbx
	jnz label196
	movq $1, %rax
	jmp label197
label196:
	movq $0, %rax
label197:
	movq %rax, %rdi
	call _assertion
label189:
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
