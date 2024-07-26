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

module r_impl_gate(in1, in2, out);
  input wire in1;
  input wire in2;
  output wire out;
  wire w1;

  not_gate not1 (in2, w1);
  or_gate or1 (in1, w1, out);
endmodule

// module min_gate(a, b, out);
//   input [0:1] a;
//   input [0:1] b;
//   output [0:1] out;
//   supply0 gnd1;
//   supply0 gnd2;
//   supply1 vdd;
  
//   wire xor1_out, xor2_out, and1_out, pmos1_out, and2_out, nmos1_out;
//   xor_gate xor1(a[0], a[1], xor1_out);
//   xor_gate xor2(b[0], b[1], xor2_out);
//   and_gate and1(xor1_out, xor2_out, and1_out);
//   pmos pmos1(pmos1_out, gnd1, and1_out);
//   assign out[0] = pmos1_out;
//   assign out[1] = pmos1_out;

//   and_gate and2(a[0], b[0], and2_out);
//   nmos nmos1(nmos1_out, and2_out, and1_out);
//   pmos pmos2(out[1], vdd, nmos1_out);
//   not_gate not1(out[1], out[0]); //
//   nmos nmos2(out[1], gnd, nmos1_out);
//   not_gate not2(out[1], out[0]); //
// endmodule

module ternary_any(a[1:0], b[1:0], out[1:0]);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;
  wire wire1, wire2, wire3, wire4, wire5, wire6, wire7, wire8, wire9, wire10, wire11, wire12, wire13, wire14, wire15, wire16, wire17;
  and_gate my_and1 (b[1], b[0], wire1);
  and_gate my_and2 (a[0], b[1], wire2);
  and_gate my_and3 (wire2, b[0], wire3);
  and_gate my_and4 (a[1], b[0], wire4);
  and_gate my_and5 (a[1], b[1], wire5);
  and_gate my_and6 (a[1], a[0], wire6);
  and_gate my_and7 (wire5, b[0], wire7);

  xor_gate my_xor1 (wire1, wire2, wire8);
  xor_gate my_xor2 (wire8, wire3, wire9);
  xor_gate my_xor3 (wire9, wire4, wire10);
  xor_gate my_xor4 (wire10, wire5, wire11);
  xor_gate my_xor5 (wire11, wire6, wire12);
  xor_gate my_xor6 (wire12, wire7, out[1]);
  
  and_gate my_and8 (a[0], b[0], wire13);
  xor_gate my_xor7 (b[1], wire13, wire14);
  xor_gate my_xor9 (wire14, wire2, wire15);
  xor_gate my_xor10 (wire15, a[1], wire16);
  xor_gate my_xor11 (wire16, wire4, out[0]);
endmodule

module ternary_min(a[1:0], b[1:0], out[1:0]);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;
  wire w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12;

  and_gate my_and1 (b[1], b[0], w1);
  and_gate my_and2 (a[0], b[0], w2);
  and_gate my_and3 (a[0], b[1], w3);
  and_gate my_and4 (a[1], b[0], w4);
  and_gate my_and5 (a[1], a[0], w5);
  and_gate my_and6 (w5, b[1], w6);
  and_gate my_and7 (w6, b[0], w7);
   
  xor_gate my_xor1 (w1, w2, w8);
  xor_gate my_xor2 (w8, w3, w9);
  xor_gate my_xor3 (w9, w4, w10);
  xor_gate my_xor4 (w10, w5, w11);
  xor_gate my_xor5 (w11, w6, w12);
  xor_gate my_xor6 (w12, w7, out[0]);
  
  and_gate my_and8 (a[1], b[1], out[1]);
endmodule

module ternary_max(a[1:0], b[1:0], out[1:0]);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;

  wire w1, w2, w3, w4, w5, w6, w7, w8, w9, w10, w11, w12;
  or_gate my_and1 (b[1], b[0], w1);
  or_gate my_and2 (a[0], b[0], w2);
  or_gate my_and3 (a[0], b[1], w3);
  or_gate my_and4 (a[1], b[0], w4);
  or_gate my_and5 (a[1], a[0], w5);
  or_gate my_and6 (w5, b[1], w6);
  or_gate my_and7 (w6, b[0], w7);
   
  xor_gate my_xor1 (w1, w2, w8);
  xor_gate my_xor2 (w8, w3, w9);
  xor_gate my_xor3 (w9, w4, w10);
  xor_gate my_xor4 (w10, w5, w11);
  xor_gate my_xor5 (w11, w6, w12);
  xor_gate my_xor6 (w12, w7, w13);

  r_impl_gate my_impl (b[1], b[0], w15);
  not_gate not3 (w15, w16);
  nor_gate nor1 (a[1], a[0], w14);
  and_gate and4 (w16, w14, w17);
  or_gate or4 (w17, w13, out[0]);
 
  or_gate my_and8 (a[1], b[1], out[1]);
endmodule

module ternary_consensus(a[1:0], b[1:0], out[1:0]);
  input [1:0] a;
  input [1:0] b;
  output [1:0] out;
  wire xor1_out, xor2_out, and1_out, and2_out, and3_out, or1_out, or2_out, or3_out;

  xor_gate xor1 (a[1], a[0], xor1_out);
  xor_gate xor2 (b[1], b[0], xor2_out);
  and_gate and1 (a[1], b[1], and1_out);
  and_gate and2 (xor1_out, xor2_out, and2_out);
  and_gate and3 (and2_out, and1_out, and3_out);

  or_gate or1 (a[1], a[0], or1_out);
  or_gate or2 (b[1], b[0], or2_out);
  or_gate or3 (or1_out, or2_out, out[1]);

  xor_gate xor3 (out[1], and3_out, out[0]);
endmodule