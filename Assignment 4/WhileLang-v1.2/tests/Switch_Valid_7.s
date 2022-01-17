
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	subq $16, %rsp
	movq 24(%rbp), %rax
	movq $78, %rbx
	cmpq %rax, %rbx
	jnz label1242
label1241:
	movq $104, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $6, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, -8(%rbp)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $75, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $78, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $73, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $71, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $72, %rcx
	movq %rcx, 80(%rbx)
	movq $0, %rcx
	movq %rcx, 88(%rbx)
	movq $84, %rcx
	movq %rcx, 96(%rbx)
	jmp label1240
	jmp label1243
label1242:
	movq $66, %rbx
	cmpq %rax, %rbx
	jnz label1244
label1243:
	movq $104, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $6, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, -8(%rbp)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $66, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $73, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $83, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $72, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $79, %rcx
	movq %rcx, 80(%rbx)
	movq $0, %rcx
	movq %rcx, 88(%rbx)
	movq $80, %rcx
	movq %rcx, 96(%rbx)
	jmp label1240
	jmp label1245
label1244:
	movq $82, %rbx
	cmpq %rax, %rbx
	jnz label1246
label1245:
	movq $72, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $4, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, -8(%rbp)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $82, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $79, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $79, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $75, %rcx
	movq %rcx, 64(%rbx)
	jmp label1240
	jmp label1247
label1246:
	movq $81, %rbx
	cmpq %rax, %rbx
	jnz label1248
label1247:
	movq $88, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $5, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, -8(%rbp)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $81, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $85, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $69, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $69, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $78, %rcx
	movq %rcx, 80(%rbx)
	jmp label1240
	jmp label1249
label1248:
	movq $75, %rbx
	cmpq %rax, %rbx
	jnz label1250
label1249:
	movq $72, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $4, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, -8(%rbp)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $75, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $73, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $78, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $71, %rcx
	movq %rcx, 64(%rbx)
	jmp label1240
	jmp label1251
label1250:
label1251:
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
	movq %rbx, -8(%rbp)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $78, %rcx
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
	movq $72, %rcx
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
	movq $71, %rcx
	movq %rcx, 112(%rbx)
	jmp label1240
label1252:
label1240:
	movq -8(%rbp), %rax
	movq %rax, %rdi
	call _objcpy
	movq %rax, %rax
	movq %rax, -8(%rbp)
	movq -8(%rbp), %rax
	movq %rax, 16(%rbp)
	jmp label1239
label1239:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	movq $104, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $6, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $75, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $78, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $73, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $71, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $72, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $84, %rbx
	movq %rbx, 96(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $78, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1254
	movq $1, %rax
	jmp label1255
label1254:
	movq $0, %rax
label1255:
	movq %rax, %rdi
	call _assertion
	movq $72, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $4, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $75, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $73, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $78, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $71, %rbx
	movq %rbx, 64(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $75, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1256
	movq $1, %rax
	jmp label1257
label1256:
	movq $0, %rax
label1257:
	movq %rax, %rdi
	call _assertion
	movq $88, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $5, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $81, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $85, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $69, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $69, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $78, %rbx
	movq %rbx, 80(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $81, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1258
	movq $1, %rax
	jmp label1259
label1258:
	movq $0, %rax
label1259:
	movq %rax, %rdi
	call _assertion
	movq $104, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $6, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $66, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $73, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $83, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $72, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $79, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $80, %rbx
	movq %rbx, 96(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $66, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1260
	movq $1, %rax
	jmp label1261
label1260:
	movq $0, %rax
label1261:
	movq %rax, %rdi
	call _assertion
	movq $72, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $4, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $82, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $79, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $79, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $75, %rbx
	movq %rbx, 64(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $82, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1262
	movq $1, %rax
	jmp label1263
label1262:
	movq $0, %rax
label1263:
	movq %rax, %rdi
	call _assertion
	movq $120, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $7, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $78, %rbx
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
	movq $72, %rbx
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
	movq $71, %rbx
	movq %rbx, 112(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $101, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1264
	movq $1, %rax
	jmp label1265
label1264:
	movq $0, %rax
label1265:
	movq %rax, %rdi
	call _assertion
	movq $120, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $7, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $78, %rbx
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
	movq $72, %rbx
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
	movq $71, %rbx
	movq %rbx, 112(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $16, %rsp
	movq $49, %rbx
	movq %rbx, 8(%rsp)
	call wl_f
	addq $16, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -32(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1266
	movq $1, %rax
	jmp label1267
label1266:
	movq $0, %rax
label1267:
	movq %rax, %rdi
	call _assertion
label1253:
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
