#ifndef VM_PROTECTION_H
#define VM_PROTECTION_H

#define F_LEN 36
#define OPCODE_N 7

// 全局变量声明
extern char* vm_stack;
extern int enc_flag[F_LEN];
extern int username[F_LEN];
extern unsigned char vm_code[]; // 声明 vm_code

// 定义 vm_cpu 结构体
typedef struct {
    int r1;
    int r2;
    int r3;
    unsigned char* eip;
    struct {
        unsigned char opcode;
        void (*handle)(void*);
    } op_list[OPCODE_N];
} vm_cpu;

// 函数声明
void vm_init(vm_cpu* cpu);
void vm_start(vm_cpu* cpu);
void check();
void func3(vm_cpu* cpu);
void func1(vm_cpu* cpu);
void func2(vm_cpu* cpu);
void generate_username_array(const char* username_input, int* output_array);

#endif // VM_PROTECTION_H
