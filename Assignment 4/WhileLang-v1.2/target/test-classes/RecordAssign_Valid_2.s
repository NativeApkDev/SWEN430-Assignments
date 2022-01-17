
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
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
	movq 16(%rbx), %rbx
	movq $1, %rcx
	subq %rcx, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
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
	movq 32(%rbx), %rbx
	movq %rbx, 32(%rax)
	jmp label1002
label1002:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -8(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $2, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $3, %rbx
	movq %rbx, 32(%rax)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq $40, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $2, %rcx
	movq %rcx, 0(%rbx)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $2, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $3, %rcx
	movq %rcx, 32(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1004
	movq $1, %rax
	jmp label1005
label1004:
	movq $0, %rax
label1005:
	movq %rax, %rdi
	call _assertion
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq 16(%rax), %rax
	movq $2, %rbx
	cmpq %rax, %rbx
	jnz label1006
	movq $1, %rax
	jmp label1007
label1006:
	movq $0, %rax
label1007:
	movq %rax, %rdi
	call _assertion
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq 32(%rax), %rax
	movq $3, %rbx
	cmpq %rax, %rbx
	jnz label1008
	movq $1, %rax
	jmp label1009
label1008:
	movq $0, %rax
label1009:
	movq %rax, %rdi
	call _assertion
	movq -8(%rbp), %rax
	movq $1, %rbx
	movq %rbx, 16(%rax)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq $40, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $2, %rcx
	movq %rcx, 0(%rbx)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $1, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $3, %rcx
	movq %rcx, 32(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1010
	movq $1, %rax
	jmp label1011
label1010:
	movq $0, %rax
label1011:
	movq %rax, %rdi
	call _assertion
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq 16(%rax), %rax
	movq $1, %rbx
	cmpq %rax, %rbx
	jnz label1012
	movq $1, %rax
	jmp label1013
label1012:
	movq $0, %rax
label1013:
	movq %rax, %rdi
	call _assertion
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq 32(%rax), %rax
	movq $3, %rbx
	cmpq %rax, %rbx
	jnz label1014
	movq $1, %rax
	jmp label1015
label1014:
	movq $0, %rax
label1015:
	movq %rax, %rdi
	call _assertion
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
	movq %rax, -16(%rbp)
	movq -16(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -16(%rbp)
	movq -16(%rbp), %rax
	movq $40, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $2, %rcx
	movq %rcx, 0(%rbx)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $0, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $3, %rcx
	movq %rcx, 32(%rbx)
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1016
	movq $1, %rax
	jmp label1017
label1016:
	movq $0, %rax
label1017:
	movq %rax, %rdi
	call _assertion
	movq -16(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -16(%rbp)
	movq -16(%rbp), %rax
	movq 16(%rax), %rax
	movq $0, %rbx
	cmpq %rax, %rbx
	jnz label1018
	movq $1, %rax
	jmp label1019
label1018:
	movq $0, %rax
label1019:
	movq %rax, %rdi
	call _assertion
	movq -16(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -16(%rbp)
	movq -16(%rbp), %rax
	movq 32(%rax), %rax
	movq $3, %rbx
	cmpq %rax, %rbx
	jnz label1020
	movq $1, %rax
	jmp label1021
label1020:
	movq $0, %rax
label1021:
	movq %rax, %rdi
	call _assertion
label1003:
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
