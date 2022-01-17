
	.text
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $32, %rsp
	movq $56, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $3, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -24(%rbp)
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
	movq $40, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $2, %rbx
	movq %rbx, 0(%rax)
	movq %rax, -8(%rbp)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $1, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $2, %rbx
	movq %rbx, 32(%rax)
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq 16(%rax), %rax
	movq %rax, -16(%rbp)
	movq -16(%rbp), %rax
	movq $1, %rbx
	cmpq %rax, %rbx
	jnz label1269
	movq $1, %rax
	jmp label1270
label1269:
	movq $0, %rax
label1270:
	movq %rax, %rdi
	call _assertion
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq 32(%rax), %rax
	movq $2, %rbx
	cmpq %rax, %rbx
	jnz label1271
	movq $1, %rax
	jmp label1272
label1271:
	movq $0, %rax
label1272:
	movq %rax, %rdi
	call _assertion
	movq -24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -24(%rbp)
	movq -24(%rbp), %rax
	movq 0(%rax), %rax
	movq $3, %rbx
	cmpq %rax, %rbx
	jnz label1273
	movq $1, %rax
	jmp label1274
label1273:
	movq $0, %rax
label1274:
	movq %rax, %rdi
	call _assertion
	movq -24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -24(%rbp)
	movq -24(%rbp), %rax
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
	jnz label1275
	movq $1, %rax
	jmp label1276
label1275:
	movq $0, %rax
label1276:
	movq %rax, %rdi
	call _assertion
	movq -24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -24(%rbp)
	movq -24(%rbp), %rax
	movq $1, %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq 0(%rax), %rax
	movq $2, %rbx
	cmpq %rax, %rbx
	jnz label1277
	movq $1, %rax
	jmp label1278
label1277:
	movq $0, %rax
label1278:
	movq %rax, %rdi
	call _assertion
	movq -24(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -24(%rbp)
	movq -24(%rbp), %rax
	movq $2, %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	shlq %rbx
	addq $16, %rbx
	addq %rbx, %rax
	movq 0(%rax), %rax
	movq $3, %rbx
	cmpq %rax, %rbx
	jnz label1279
	movq $1, %rax
	jmp label1280
label1279:
	movq $0, %rax
label1280:
	movq %rax, %rdi
	call _assertion
label1268:
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
