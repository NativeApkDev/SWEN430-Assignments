
	.text
wl_f:
	pushq %rbp
	movq %rsp, %rbp
	movq 32(%rbp), %rax
	movq $0, %rbx
	cmpq %rax, %rbx
	jnz label1114
label1113:
	movq 24(%rbp), %rbx
	movq $0, %rcx
	cmpq %rbx, %rcx
	jnz label1117
label1116:
	jmp label1115
	jmp label1118
label1117:
	movq $1, %rcx
	cmpq %rbx, %rcx
	jnz label1119
label1118:
	movq $136, %rcx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, 8(%rsp)
	movq %rcx, %rdi
	call _malloc
	movq %rax, %rcx
	movq 0(%rsp), %rax
	movq 8(%rsp), %rbx
	addq $16, %rsp
	movq $8, %rdx
	movq %rdx, 0(%rcx)
	movq %rcx, 16(%rbp)
	movq $0, %rdx
	movq %rdx, 8(%rcx)
	movq $82, %rdx
	movq %rdx, 16(%rcx)
	movq $0, %rdx
	movq %rdx, 24(%rcx)
	movq $69, %rdx
	movq %rdx, 32(%rcx)
	movq $0, %rdx
	movq %rdx, 40(%rcx)
	movq $68, %rdx
	movq %rdx, 48(%rcx)
	movq $0, %rdx
	movq %rdx, 56(%rcx)
	movq $32, %rdx
	movq %rdx, 64(%rcx)
	movq $0, %rdx
	movq %rdx, 72(%rcx)
	movq $66, %rdx
	movq %rdx, 80(%rcx)
	movq $0, %rdx
	movq %rdx, 88(%rcx)
	movq $76, %rdx
	movq %rdx, 96(%rcx)
	movq $0, %rdx
	movq %rdx, 104(%rcx)
	movq $85, %rdx
	movq %rdx, 112(%rcx)
	movq $0, %rdx
	movq %rdx, 120(%rcx)
	movq $69, %rdx
	movq %rdx, 128(%rcx)
	jmp label1111
	jmp label1120
label1119:
	movq $2, %rcx
	cmpq %rbx, %rcx
	jnz label1121
label1120:
	movq $152, %rcx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, 8(%rsp)
	movq %rcx, %rdi
	call _malloc
	movq %rax, %rcx
	movq 0(%rsp), %rax
	movq 8(%rsp), %rbx
	addq $16, %rsp
	movq $9, %rdx
	movq %rdx, 0(%rcx)
	movq %rcx, 16(%rbp)
	movq $0, %rdx
	movq %rdx, 8(%rcx)
	movq $82, %rdx
	movq %rdx, 16(%rcx)
	movq $0, %rdx
	movq %rdx, 24(%rcx)
	movq $69, %rdx
	movq %rdx, 32(%rcx)
	movq $0, %rdx
	movq %rdx, 40(%rcx)
	movq $68, %rdx
	movq %rdx, 48(%rcx)
	movq $0, %rdx
	movq %rdx, 56(%rcx)
	movq $32, %rdx
	movq %rdx, 64(%rcx)
	movq $0, %rdx
	movq %rdx, 72(%rcx)
	movq $71, %rdx
	movq %rdx, 80(%rcx)
	movq $0, %rdx
	movq %rdx, 88(%rcx)
	movq $82, %rdx
	movq %rdx, 96(%rcx)
	movq $0, %rdx
	movq %rdx, 104(%rcx)
	movq $69, %rdx
	movq %rdx, 112(%rcx)
	movq $0, %rdx
	movq %rdx, 120(%rcx)
	movq $69, %rdx
	movq %rdx, 128(%rcx)
	movq $0, %rdx
	movq %rdx, 136(%rcx)
	movq $78, %rdx
	movq %rdx, 144(%rcx)
	jmp label1111
label1121:
label1115:
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
	movq %rbx, 16(%rbp)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $82, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $69, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $68, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $32, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $82, %rcx
	movq %rcx, 80(%rbx)
	movq $0, %rcx
	movq %rcx, 88(%rbx)
	movq $69, %rcx
	movq %rcx, 96(%rbx)
	movq $0, %rcx
	movq %rcx, 104(%rbx)
	movq $68, %rcx
	movq %rcx, 112(%rbx)
	jmp label1111
	jmp label1122
label1114:
	movq $1, %rbx
	cmpq %rax, %rbx
	jnz label1123
label1122:
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
	movq %rbx, 16(%rbp)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $66, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $76, %rcx
	movq %rcx, 32(%rbx)
	movq $0, %rcx
	movq %rcx, 40(%rbx)
	movq $85, %rcx
	movq %rcx, 48(%rbx)
	movq $0, %rcx
	movq %rcx, 56(%rbx)
	movq $69, %rcx
	movq %rcx, 64(%rbx)
	movq $0, %rcx
	movq %rcx, 72(%rbx)
	movq $32, %rcx
	movq %rcx, 80(%rbx)
	movq $0, %rcx
	movq %rcx, 88(%rbx)
	movq $63, %rcx
	movq %rcx, 96(%rbx)
	movq $0, %rcx
	movq %rcx, 104(%rbx)
	movq $63, %rcx
	movq %rcx, 112(%rbx)
	movq $0, %rcx
	movq %rcx, 120(%rbx)
	movq $63, %rcx
	movq %rcx, 128(%rbx)
	jmp label1111
	jmp label1124
label1123:
	movq $2, %rbx
	cmpq %rax, %rbx
	jnz label1125
label1124:
	movq $152, %rbx
	subq $16, %rsp
	movq %rax, 0(%rsp)
	movq %rbx, %rdi
	call _malloc
	movq %rax, %rbx
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq $9, %rcx
	movq %rcx, 0(%rbx)
	movq %rbx, 16(%rbp)
	movq $0, %rcx
	movq %rcx, 8(%rbx)
	movq $71, %rcx
	movq %rcx, 16(%rbx)
	movq $0, %rcx
	movq %rcx, 24(%rbx)
	movq $82, %rcx
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
	movq $0, %rcx
	movq %rcx, 88(%rbx)
	movq $32, %rcx
	movq %rcx, 96(%rbx)
	movq $0, %rcx
	movq %rcx, 104(%rbx)
	movq $63, %rcx
	movq %rcx, 112(%rbx)
	movq $0, %rcx
	movq %rcx, 120(%rbx)
	movq $63, %rcx
	movq %rcx, 128(%rbx)
	movq $0, %rcx
	movq %rcx, 136(%rbx)
	movq $63, %rcx
	movq %rcx, 144(%rbx)
	jmp label1111
label1125:
label1112:
	movq $8, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $0, %rbx
	movq %rbx, 0(%rax)
	movq %rax, 16(%rbp)
	jmp label1111
label1111:
	movq %rbp, %rsp
	popq %rbp
	ret
wl_main:
	pushq %rbp
	movq %rsp, %rbp
	subq $32, %rsp
	movq $0, %rax
	movq %rax, -8(%rbp)
	movq $1, %rax
	movq %rax, -16(%rbp)
	movq $2, %rax
	movq %rax, -24(%rbp)
	movq $120, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $7, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $82, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $69, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $68, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $32, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $82, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $69, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $68, %rbx
	movq %rbx, 112(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $32, %rsp
	movq -8(%rbp), %rbx
	movq %rbx, 8(%rsp)
	movq -8(%rbp), %rbx
	movq %rbx, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -48(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1127
	movq $1, %rax
	jmp label1128
label1127:
	movq $0, %rax
label1128:
	movq %rax, %rdi
	call _assertion
	movq $136, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $8, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $82, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $69, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $68, %rbx
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
	movq $76, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $85, %rbx
	movq %rbx, 112(%rax)
	movq $0, %rbx
	movq %rbx, 120(%rax)
	movq $69, %rbx
	movq %rbx, 128(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $32, %rsp
	movq -16(%rbp), %rbx
	movq %rbx, 8(%rsp)
	movq -8(%rbp), %rbx
	movq %rbx, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -48(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1129
	movq $1, %rax
	jmp label1130
label1129:
	movq $0, %rax
label1130:
	movq %rax, %rdi
	call _assertion
	movq $152, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $9, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $82, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $69, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $68, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $32, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $71, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $82, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $69, %rbx
	movq %rbx, 112(%rax)
	movq $0, %rbx
	movq %rbx, 120(%rax)
	movq $69, %rbx
	movq %rbx, 128(%rax)
	movq $0, %rbx
	movq %rbx, 136(%rax)
	movq $78, %rbx
	movq %rbx, 144(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $32, %rsp
	movq -24(%rbp), %rbx
	movq %rbx, 8(%rsp)
	movq -8(%rbp), %rbx
	movq %rbx, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -48(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1131
	movq $1, %rax
	jmp label1132
label1131:
	movq $0, %rax
label1132:
	movq %rax, %rdi
	call _assertion
	movq $136, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $8, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $66, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $76, %rbx
	movq %rbx, 32(%rax)
	movq $0, %rbx
	movq %rbx, 40(%rax)
	movq $85, %rbx
	movq %rbx, 48(%rax)
	movq $0, %rbx
	movq %rbx, 56(%rax)
	movq $69, %rbx
	movq %rbx, 64(%rax)
	movq $0, %rbx
	movq %rbx, 72(%rax)
	movq $32, %rbx
	movq %rbx, 80(%rax)
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $63, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $63, %rbx
	movq %rbx, 112(%rax)
	movq $0, %rbx
	movq %rbx, 120(%rax)
	movq $63, %rbx
	movq %rbx, 128(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $32, %rsp
	movq -8(%rbp), %rbx
	movq %rbx, 8(%rsp)
	movq -16(%rbp), %rbx
	movq %rbx, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -48(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1133
	movq $1, %rax
	jmp label1134
label1133:
	movq $0, %rax
label1134:
	movq %rax, %rdi
	call _assertion
	movq $152, %rax
	movq %rax, %rdi
	call _malloc
	movq %rax, %rax
	movq $9, %rbx
	movq %rbx, 0(%rax)
	movq $0, %rbx
	movq %rbx, 8(%rax)
	movq $71, %rbx
	movq %rbx, 16(%rax)
	movq $0, %rbx
	movq %rbx, 24(%rax)
	movq $82, %rbx
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
	movq $0, %rbx
	movq %rbx, 88(%rax)
	movq $32, %rbx
	movq %rbx, 96(%rax)
	movq $0, %rbx
	movq %rbx, 104(%rax)
	movq $63, %rbx
	movq %rbx, 112(%rax)
	movq $0, %rbx
	movq %rbx, 120(%rax)
	movq $63, %rbx
	movq %rbx, 128(%rax)
	movq $0, %rbx
	movq %rbx, 136(%rax)
	movq $63, %rbx
	movq %rbx, 144(%rax)
	subq $16, %rsp
	movq %rax, 0(%rsp)
	subq $32, %rsp
	movq -8(%rbp), %rbx
	movq %rbx, 8(%rsp)
	movq -24(%rbp), %rbx
	movq %rbx, 16(%rsp)
	call wl_f
	addq $32, %rsp
	movq 0(%rsp), %rax
	addq $16, %rsp
	movq -48(%rsp), %rbx
	movq %rax, %rdi
	movq %rbx, %rsi
	call _objcmp
	movq %rax, %rax
	cmpq $0, %rax
	jz label1135
	movq $1, %rax
	jmp label1136
label1135:
	movq $0, %rax
label1136:
	movq %rax, %rdi
	call _assertion
label1126:
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
