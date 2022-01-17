
	.text
wl_sum:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $0, %rax
	movq %rax, -8(%rbp)
	movq $0, %rax
	movq %rax, -16(%rbp)
label1303:
	movq -8(%rbp), %rax
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
	jge label1304
	movq 24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, 24(%rbp)
	movq 24(%rbp), %rax
	movq -8(%rbp), %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq 0(%rax), %rax
	movq $0, %rbx
	cmpq %rbx, %rax
	jge label1305
	movq -8(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -8(%rbp)
	jmp label1303
	jmp label1305
label1305:
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
	movq -8(%rbp), %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	shlq %rcx
	addq $16, %rcx
	addq %rcx, %rbx
	movq 0(%rbx), %rbx
	addq %rbx, %rax
	movq %rax, -16(%rbp)
	movq -8(%rbp), %rax
	movq $1, %rbx
	addq %rbx, %rax
	movq %rax, -8(%rbp)
	jmp label1303
label1304:
	movq -16(%rbp), %rax
	movq %rax, 16(%rbp)
	jmp label1302
label1302:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq $8, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $0, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 8(%rsp)
	call wl_sum
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $0, %rbx
	cmpq %rax, %rbx
	jnz label1307
	movq $1, %rax
	jmp label1308
label1307:
	movq $0, %rax
label1308:
	movq %rax, %rdi
	call _assertion
	subq $16, %rsp
	movq $56, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $3, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 8(%rsp)
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
	call wl_sum
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $6, %rbx
	cmpq %rax, %rbx
	jnz label1309
	movq $1, %rax
	jmp label1310
label1309:
	movq $0, %rax
label1310:
	movq %rax, %rdi
	call _assertion
	subq $16, %rsp
	movq $88, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $5, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 8(%rsp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $1, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $-1, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $2, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $-2, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $3, %rbx
	movq %rbx, 80(%rax)
	call wl_sum
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $6, %rbx
	cmpq %rax, %rbx
	jnz label1311
	movq $1, %rax
	jmp label1312
label1311:
	movq $0, %rax
label1312:
	movq %rax, %rdi
	call _assertion
	subq $16, %rsp
	movq $88, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $5, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 8(%rsp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $123, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $981, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $1, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $3, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $0, %rbx
	movq %rbx, 80(%rax)
	call wl_sum
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $1108, %rbx
	cmpq %rax, %rbx
	jnz label1313
	movq $1, %rax
	jmp label1314
label1313:
	movq $0, %rax
label1314:
	movq %rax, %rdi
	call _assertion
	subq $16, %rsp
	movq $120, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $7, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 8(%rsp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $123, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $-1, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $981, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $1, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $-1, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $3, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $0, %rbx
	movq %rbx, 112(%rax)
	call wl_sum
	addq $16, %rsp
	movq -16(%rsp), %rax
	movq $1108, %rbx
	cmpq %rax, %rbx
	jnz label1315
	movq $1, %rax
	jmp label1316
label1315:
	movq $0, %rax
label1316:
	movq %rax, %rdi
	call _assertion
label1306:
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
