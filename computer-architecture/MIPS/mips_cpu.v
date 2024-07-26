`include "util.v"

module mips_cpu(clk, pc, pc_new, instruction_memory_a, instruction_memory_rd, data_memory_a, data_memory_rd, data_memory_we, data_memory_wd,
                register_a1, register_a2, register_a3, register_we3, register_wd3, register_rd1, register_rd2);
  // сигнал синхронизации
  input clk;
  input [31:0] register_rd1, register_rd2, instruction_memory_rd, data_memory_rd;
  output reg [31:0] pc;
  output [31:0] pc_new, instruction_memory_a, data_memory_a, data_memory_wd, register_wd3;
  output data_memory_we, register_we3;
  output [4:0] register_a1, register_a2, register_a3;
  
  // внутренние output
  output regdst, regwrite, alusrc, memtoreg, memwrite, zero, src;
  output branch;
  output [2:0] control;
  output [31:0] imm, b, shl_out, tmp, PC4, PC_branch, result, alu_ans;
  
  initial begin
    pc = 0;
  end

  always @ (posedge clk) begin
    pc = tmp;
  end
  
  assign register_a1 = instruction_memory_rd[25:21];
  assign register_a2 = instruction_memory_rd[20:16];
  // assign pc_new = instruction_memory_rd[25:0];
  assign data_memory_wd = register_rd2;

  assign instruction_memory_a = pc;
  adder my_add(pc, 4, PC4);
  adder my_add_(shl_out, PC4, PC_branch);
  shl_2 ext_s(imm, shl_out);
  control_unit cont(instruction_memory_rd[31:26], instruction_memory_rd[5:0], alusrc, regdst, register_we3, memtoreg, data_memory_we, branch, control);

  sign_extend ext(instruction_memory_rd[15:0], imm);
  mux2_32 mx(register_rd2, imm, alusrc, b);
  alu answer(register_rd1, b, control, alu_ans, zero);
  assign src = zero & branch;
  assign data_memory_a = alu_ans;
  mux2_5 mx_3(instruction_memory_rd[20:16], instruction_memory_rd[15:11], regdst, register_a3);
  mux2_32 PC(PC4, PC_branch, src, tmp);
  mux2_32 data(alu_ans, data_memory_rd, memtoreg, result);
  assign register_wd3 = result;

endmodule

module alu(a, b, control, result, zero);
  input [31:0] a, b;
  input [2:0] control;
  output reg [31:0] result;
  output reg zero; 
   
  always @(a, b, control) begin
    case (control)
      3'b000: 
        result = a & b;
      3'b001: 
        result = a | b;
      3'b010: 
        result = a + b;
      3'b011:
        result = 0;
      3'b100:
        result = a & !b;
      3'b101:
        result = a | !b;
      3'b110: 
        result = a - b;
      3'b111: begin 
        if (a[31] != b[31]) begin
          if (a[31] > b[31]) begin
            result = 32'b1;
          end
          else begin
            result = 32'b0;
          end  
                
        end 
        else begin
          if (a < b) begin
            result = 32'b1;
          end else begin
            result = 32'b0;
          end
        end
      end
    endcase
  end

  always @(result) begin
    if (result != 0) begin
      zero = 0;
    end else begin
      zero = 1;
    end
  end
  
endmodule

module control_unit(opcode, funct, alusrc, regdst, regwrite, memtoreg, memwrite, branch, control);
  input [5:0] opcode, funct;
  output reg memtoreg, memwrite, alusrc, regdst, regwrite;
  //output reg jump, jal
  output reg branch;
  output reg [1:0] op;
  output reg [2:0] control;
  
  always @ (*) begin
    case (opcode)
      //-lw-
      6'b100011: begin
        memwrite = 0;
      	memtoreg = 1;
        regwrite = 1;
      	regdst = 0;
      	alusrc = 1;
      	branch = 0;
        // jump = 0;
        // jal = 0;
      	op = 2'b00;
      end
      //-sw-
      6'b101011: begin
        memwrite = 1;
        regwrite = 0;
      	alusrc = 1;
      	branch = 0;
      	op = 2'b00;
      end
      //-beq-
      6'b000100: begin
        memwrite = 0;
        regwrite = 0;
      	alusrc = 0;
      	branch = 1;
      	op = 2'b01;
      end
      //-addi-
      6'b001000: begin
        memwrite = 0;
      	memtoreg = 0;
        regwrite = 1;
      	regdst = 0;
      	alusrc = 1;
      	branch = 0;
      	op = 2'b00;
      end
      //-andi-
      6'b001100: begin
        memwrite = 0;
      	memtoreg = 0;
        regwrite = 1;
      	regdst = 0;
      	alusrc = 0;
      	branch = 0;
      	op = 2'b00;
      end
      //-bne-
      6'b001100: begin
        memwrite = 0;
        regwrite = 0;
      	alusrc = 0;
      	branch = 1;
      	op = 2'b11;
      end
    //for funct
      6'b000000: begin
        memwrite = 0;
      	memtoreg = 0;
        regwrite = 1;
      	regdst = 1;
      	alusrc = 0;
      	branch = 0;
      	op = 2'b10;
      end
    endcase
  end
  
  always @ (*) begin
    case(op)
      2'b00:
        control = 3'b010;
      2'b01:
        control = 3'b110;
      2'b10:
        case(funct)
          //-add-
          6'b100000:
            control = 3'b010;
        
          //-sub-
          6'b100010:
            control = 3'b110;

          //-and-
          6'b100100:
            control = 3'b000;

          //-or-
          6'b100101:
            control = 3'b001;

          //-slt-
          6'b101010:
            control = 3'b111;
        endcase
    endcase
  end
  
endmodule