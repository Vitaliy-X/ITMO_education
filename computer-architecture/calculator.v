module alu(a, b, control, res);
  input [3:0] a, b; // Операнды
  input [2:0] control; // Управляющие сигналы для выбора операции
  output [3:0] res; // Результат
  wire [0:3] w0, w1, w2, w3, w4, w5, w6, w7;

  bits_and n000(a, b, w0);
  bits_or n001(a, b, w1);
  ripple_carry_adder n010(a, b, w2);
  bits_and n011(a, b, w3);
  bits_100_gate n100(a, b, w4);
  bits_101_gate n101(a, b, w5);
  minus n110(a, b, w6);
  slt n111(a, b, w7);

  mux_8 mux_(w0, w1, w2, w3, w4, w5, w6, w7, control, res);

endmodule

module d_latch(clk, d, we, q);
  input clk; // Сигнал синхронизации
  input d; // Бит для записи в ячейку
  input we; // Необходимо ли перезаписать содержимое ячейки

  output reg q; // Сама ячейка
  // Изначально в ячейке хранится 0
  initial begin
    q <= 0;
  end
  // Значение изменяется на переданное на спаде сигнала синхронизации
  always @ (negedge clk) begin
    if (we) begin
      q <= d;
    end
  end
endmodule

module register_file(clk, rd_addr, we_addr, we_data, rd_data);
  input clk; // Сигнал синхронизации
  input [1:0] rd_addr, we_addr; // Номера регистров для чтения и записи
  input [3:0] we_data; // Данные для записи в регистровый файл

  output [3:0] rd_data; // Данные, полученные в результате чтения из регистрового файла
  wire [3:0] rd_data0, rd_data1, rd_data2, rd_data3;
  wire [3:0] we;

  decoder dec(we_addr, we);
  memory m0(clk, we[3], we_data, rd_data0);
  memory m1(clk, we[2], we_data, rd_data1);
  memory m2(clk, we[1], we_data, rd_data2);
  memory m3(clk, we[0], we_data, rd_data3);
  mux_4 m(rd_data0, rd_data1, rd_data2, rd_data3, rd_addr, rd_data);
endmodule

module calculator(clk, rd_addr, immediate, we_addr, control, rd_data);
  input clk; // Сигнал синхронизации
  input [1:0] rd_addr; // Номер регистра, из которого берется значение первого операнда
  input [3:0] immediate; // Целочисленная константа, выступающая вторым операндом
  input [1:0] we_addr; // Номер регистра, куда производится запись результата операции
  input [2:0] control; // Управляющие сигналы для выбора операции

  output [3:0] rd_data; // Данные из регистра c номером 'rd_addr', подающиеся на выход
  wire [3:0] we_data;
  register_file reg1(clk, rd_addr, we_addr, we_data, rd_data);
  alu alu1(rd_data, immediate, control, we_data);

endmodule

module mux_8(a0, a1, a2, a3, a4, a5, a6, a7, control, out);
  input wire [0:2] control;
  input wire [0:3] a0, a1, a2, a3, a4, a5, a6, a7;
  output wire [0:3] out;

  wire [0:3] w0, w1, w2, w3, w4, w5, w6, w7;
  wire [0:3] out0, out1, out2, out3, out4, out5;

  not_gate not0(control[0], nc0);
  not_gate not1(control[1], nc1);
  not_gate not2(control[2], nc2);

  b_and_gate b0(a0, nc0, nc1, nc2, w0);
  b_and_gate b1(a1, nc0, nc1, control[2], w1);
  b_and_gate b2(a2, nc0, control[1], nc2, w2);
  b_and_gate b3(a3, nc0, nc1, nc2, w3);
  b_and_gate b4(a4, control[0], nc1, nc2, w4);
  b_and_gate b5(a5, control[0], nc1, control[2], w5);
  b_and_gate b6(a6, control[0], control[1], nc2, w6);
  b_and_gate b7(a7, control[0], control[1], control[2], w7);

  bits_or or0(w0, w1, out0);
  bits_or or1(w2, w3, out1);
  bits_or or2(w4, w5, out2);
  bits_or or3(w6, w7, out3);
  bits_or or4(out0, out1, out4);
  bits_or or5(out2, out3, out5);
  bits_or or6(out4, out5, out);

endmodule

module b_and_gate(a, c1, c2, c3, out);
  input wire [0:3] a;
  input wire c1, c2, c3;
  output wire [0:3] out;
  wire [0:7] out_;
  wire [0:3] ctr;
  assign ctr[0] = c1;
  assign ctr[1] = c2;
  assign ctr[2] = c3;

  and_gate and0(a[0], ctr[0], out_[0]);
  and_gate and1(a[1], ctr[0], out_[1]);
  and_gate and2(a[2], ctr[0], out_[2]);
  and_gate and3(a[3], ctr[0], out_[3]);

  and_gate and4(out_[0], ctr[1], out_[4]);
  and_gate and5(out_[1], ctr[1], out_[5]);
  and_gate and6(out_[2], ctr[1], out_[6]);
  and_gate and7(out_[3], ctr[1], out_[7]);

  and_gate and8(out_[4], ctr[2], out[0]);
  and_gate and9(out_[5], ctr[2], out[1]);
  and_gate and10(out_[6], ctr[2], out[2]);
  and_gate and11(out_[7], ctr[2], out[3]);

endmodule

module memory(clk, we, we_data, rd_data);
  input clk, we;
  input [0:3] we_data;
  output [0:3] rd_data;

  d_latch d0(clk, we_data[0], we, rd_data[0]);
  d_latch d1(clk, we_data[1], we, rd_data[1]);
  d_latch d2(clk, we_data[2], we, rd_data[2]);
  d_latch d3(clk, we_data[3], we, rd_data[3]);
  
endmodule

module decoder(s, z);
  input [0:1] s;
  output [0:3] z;
  wire [0:1] ns;

  not_gate not0(s[0], ns[0]);
  not_gate not1(s[1], ns[1]);

  and_gate and0(ns[0], ns[1], z[0]);
  and_gate and1(s[0], ns[1], z[1]);
  and_gate and2(ns[0], s[1], z[2]);
  and_gate and3(s[0], s[1], z[3]);

endmodule

module mux_4(x0, x1, x2, x3, s, out);
  input [0:3] x0, x1, x2, x3;
  input [0:1] s;
  output [0:3] out;
  wire [0:1] ns;
  wire [0:3] z, or_out0, or_out1;
  wire [0:3] r0, r1, r2, r3;

  not_gate not0(s[0], ns[0]);
  not_gate not1(s[1], ns[1]);

  and_gate and0(ns[0], ns[1], z[0]);
  and_gate and1(s[0], ns[1], z[1]);
  and_gate and2(ns[0], s[1], z[2]);
  and_gate and3(s[0], s[1], z[3]);

  bits_and_for_one b0(x0, z[0], r0);
  bits_and_for_one b1(x1, z[1], r1);
  bits_and_for_one b2(x2, z[2], r2);
  bits_and_for_one b3(x3, z[3], r3);

  bits_or or1(r0, r1, or_out0);
  bits_or or2(r2, r3, or_out1);
  bits_or or3(or_out0, or_out1, out);

endmodule

module bits_and_for_one(a, s, out);
  input [0:3] a;
  input s;
  output [0:3] out;

  and_gate and0(a[0], s, out[0]);
  and_gate and1(a[1], s, out[1]);
  and_gate and2(a[2], s, out[2]);
  and_gate and3(a[3], s, out[3]);

endmodule

module not_gate(in, out);
  input wire in;
  output wire out;

  supply1 vdd;
  supply0 gnd;

  pmos pmos1(out, vdd, in);
  nmos nmos1(out, gnd, in);
endmodule

module nand_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  supply0 gnd;
  supply1 pwr;

  wire nmos1_out;

  pmos pmos1(out, pwr, in1);
  pmos pmos2(out, pwr, in2);
  nmos nmos1(nmos1_out, gnd, in1);
  nmos nmos2(out, nmos1_out, in2);
endmodule

module nor_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  supply0 gnd;
  supply1 pwr;

  wire pmos1_out;

  pmos pmos1(pmos1_out, pwr, in1);
  pmos pmos2(out, pmos1_out, in2);
  nmos nmos1(out, gnd, in1);
  nmos nmos2(out, gnd, in2);
endmodule

module and_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  wire nand_out;

  nand_gate nand_gate1(in1, in2, nand_out);
  not_gate not_gate1(nand_out, out);
endmodule

module or_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  wire nor_out;

  nor_gate nor_gate1(in1, in2, nor_out);
  not_gate not_gate1(nor_out, out);
endmodule

module r_impl_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;
  wire w1;

  not_gate not1 (in2, w1);
  or_gate or1 (in1, w1, out);
endmodule

module xor_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;

  wire not_in1;
  wire not_in2;

  wire and_out1;
  wire and_out2;

  wire or_out1;

  not_gate not_gate1(in1, not_in1);
  not_gate not_gate2(in2, not_in2);

  and_gate and_gate1(in1, not_in2, and_out1);
  and_gate and_gate2(not_in1, in2, and_out2);

  or_gate or_gate1(and_out1, and_out2, out);
endmodule

module xnor_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;
  wire xor_out;

  xor_gate xor_gate1(in1, in2, xor_out);
  not_gate not_gate1(xor_out, out);

endmodule

module adder(a, b, cin, s, cout);
  input wire a, b, cin;
  output wire s, cout;

  wire w1, w2, w3;

  xor_gate xor1(a, b, w1);
  xor_gate xor2(w1, cin, s);

  and_gate and1(a, b, w2);
  and_gate and2(w1, cin, w3);

  or_gate or1(w2, w3, cout);

endmodule

module gate_100(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;
  wire w1;

  not_gate not1 (in2, w1);
  and_gate or1 (in1, w1, out);
endmodule

module bits_or(in1, in2, out);
  input wire [3:0] in1, in2;
  output wire [3:0] out;
  
  or_gate or1(in1[0], in2[0], out[0]);
  or_gate or2(in1[1], in2[1], out[1]);
  or_gate or3(in1[2], in2[2], out[2]);
  or_gate or4(in1[3], in2[3], out[3]);

endmodule

module bits_and(in1, in2, out);
  input wire [3:0] in1, in2;
  output wire [3:0] out;
  
  and_gate and1(in1[0], in2[0], out[0]);
  and_gate and2(in1[1], in2[1], out[1]);
  and_gate and3(in1[2], in2[2], out[2]);
  and_gate and4(in1[3], in2[3], out[3]);

endmodule

module bits_101_gate(in1, in2, out);
  input wire [3:0] in1, in2;
  output wire [3:0] out;
  
  r_impl_gate impl1(in1[0], in2[0], out[0]);
  r_impl_gate impl2(in1[1], in2[1], out[1]);
  r_impl_gate impl3(in1[2], in2[2], out[2]);
  r_impl_gate impl4(in1[3], in2[3], out[3]);

endmodule

module bits_100_gate(in1, in2, out);
  input wire [3:0] in1, in2;
  output wire [3:0] out;
  
  gate_100 i1(in1[0], in2[0], out[0]);
  gate_100 i2(in1[1], in2[1], out[1]);
  gate_100 i3(in1[2], in2[2], out[2]);
  gate_100 i4(in1[3], in2[3], out[3]);

endmodule

module ripple_carry_adder(in1, in2, out);
  input wire [0:3] in1, in2;
  output wire [0:3] out;
  supply0 cin3;
  wire cin2, cin1, cin0;
  
  adder a1(in1[3], in2[3], cin3, out[3], cin2);
  adder a2(in1[2], in2[2], cin2, out[2], cin1);
  adder a3(in1[1], in2[1], cin1, out[1], cin0);
  adder a4(in1[0], in2[0], cin0, out[0], cin3);

endmodule

module bits_not(in1, out);
  input wire [3:0] in1;
  output wire [3:0] out;

  not_gate not1(in1[3], out[3]);
  not_gate not2(in1[2], out[2]);
  not_gate not3(in1[1], out[1]);
  not_gate not4(in1[0], out[0]);

endmodule

module bits_not1(in1, out);
  input wire [4:0] in1;
  output wire [4:0] out;

  not_gate not0(in1[4], out[4]);
  not_gate not1(in1[3], out[3]);
  not_gate not2(in1[2], out[2]);
  not_gate not3(in1[1], out[1]);
  not_gate not4(in1[0], out[0]);

endmodule

module ripple_carry_adder1(in1, in2, out);
  input wire [0:4] in1, in2;
  output wire [0:4] out;
  supply0 cin4;
  wire cin3, cin2, cin1, cin0;
  
  adder a0(in1[4], in2[4], cin4, out[4], cin3);
  adder a1(in1[3], in2[3], cin3, out[3], cin2);
  adder a2(in1[2], in2[2], cin2, out[2], cin1);
  adder a3(in1[1], in2[1], cin1, out[1], cin0);
  adder a4(in1[0], in2[0], cin0, out[0], cin4);

endmodule

module minus1(in1, in2, out);
  input wire [0:4] in1, in2;
  output wire [0:4] out;
  wire [0:4] in3 = 5'b00001;
  wire [0:4] inv_out;
  wire [0:4] out_two;

  bits_not1 bits1(in2, inv_out);
  ripple_carry_adder1 add0(inv_out, in3, out_two);
  ripple_carry_adder1 add1(out_two, in1, out);

endmodule

module minus(in1, in2, out);
  input wire [0:3] in1, in2;
  output wire [0:3] out;
  wire [0:3] in3 = 4'b0001;
  wire [0:3] inv_out, out_two;

  bits_not bits1(in2, inv_out);
  ripple_carry_adder add0(inv_out, in3, out_two);
  ripple_carry_adder add1(out_two, in1, out);

endmodule

module slt(a, b, out);
  input wire [0:3] a, b;
  output wire [0:3] out;
  wire [0:4] x, y, c;
  supply0 [0:2] gnd;

  assign x[1:4] = a;
  assign y[1:4] = b;
  assign x[0] = a[0];
  assign y[0] = b[0];

  minus1 m1(x, y, c);

  assign out[3] = c[0];
  // not_impl and1(cin_, c[0], out[3]);
  assign out[0:2] = gnd;

endmodule