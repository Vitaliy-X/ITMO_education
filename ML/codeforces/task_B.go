package main

import (
	"bufio"
	"fmt"
	"log"
	"math"
	"os"
	"strconv"
	"strings"
)

type Mat struct {
	rows, cols int
	data       []float64
}

func NewMat(rows, cols int, val float64) *Mat {
	mat := make([]float64, rows*cols)
	for i := range mat {
		mat[i] = val
	}
	return &Mat{rows, cols, mat}
}

func (m *Mat) At(i, j int) float64 {
	return m.data[i*m.cols+j]
}

func (m *Mat) Set(i, j int, v float64) {
	m.data[i*m.cols+j] = v
}

func (m *Mat) Scale(i, j int, f float64) {
	m.data[i*m.cols+j] *= f
}

func (m *Mat) Inc(i, j int, d float64) {
	m.data[i*m.cols+j] += d
}

func (m *Mat) Read(scanner *bufio.Scanner) {
	for i := 0; i < m.rows; i++ {
		if !scanner.Scan() {
			log.Fatalf("error: failed to read a line for matrix")
		}
		parts := strings.Fields(scanner.Text())
		if len(parts) < m.cols {
			log.Fatalf("error: not enough columns in matrix row (expected %d, got %d)", m.cols, len(parts))
		}
		for j := 0; j < m.cols; j++ {
			val, err := strconv.Atoi(parts[j])
			if err != nil {
				log.Fatalf("error: failed to convert '%s' to number: %v", parts[j], err)
			}
			m.Set(i, j, float64(val))
		}
	}
}

func (m *Mat) String() string {
	var b strings.Builder
	for i := 0; i < m.rows; i++ {
		for j := 0; j < m.cols; j++ {
			b.WriteString(fmt.Sprintf("%.10g", m.At(i, j)))
			if j+1 < m.cols {
				b.WriteString(" ")
			}
		}
		if i+1 < m.rows {
			b.WriteString("\n")
		}
	}
	return b.String()
}

type Node interface {
	Forward()
	Backward()
	Vals() *Mat
	Grads() *Mat
}

type NodeBase struct {
	vals  *Mat
	grads *Mat
}

func (n *NodeBase) Vals() *Mat  { return n.vals }
func (n *NodeBase) Grads() *Mat { return n.grads }
func (n *NodeBase) Forward()    {}
func (n *NodeBase) Backward()   {}

type NodeHolder struct {
	NodeBase
	ref interface{}
}

type TanhNode struct {
	NodeHolder
}

func MakeTanhNode(node Node) *TanhNode {
	return &TanhNode{
		NodeHolder{
			NodeBase: NodeBase{
				NewMat(node.Vals().rows, node.Vals().cols, 0),
				NewMat(node.Vals().rows, node.Vals().cols, 0),
			},
			ref: node,
		},
	}
}

func (t *TanhNode) Forward() {
	args := t.ref.(Node)
	for i := 0; i < t.vals.rows; i++ {
		for j := 0; j < t.vals.cols; j++ {
			t.vals.Set(i, j, math.Tanh(args.Vals().At(i, j)))
		}
	}
	// fmt.Printf("TanhNode Forward: %v\n", t.vals)
}

func (t *TanhNode) Backward() {
	arg := t.ref.(Node)
	for i := 0; i < t.vals.rows; i++ {
		for j := 0; j < t.vals.cols; j++ {
			grad := (1 - t.vals.At(i, j)*t.vals.At(i, j)) * t.grads.At(i, j)
			arg.Grads().Inc(i, j, grad)
		}
	}
}

type NodePair struct {
	l, r Node
}

type MatMulNode struct {
	NodeHolder
}

func MakeMatMulNode(pair NodePair) *MatMulNode {
	r := pair.l.Vals().rows
	c := pair.r.Vals().cols
	return &MatMulNode{
		NodeHolder: NodeHolder{
			NodeBase: NodeBase{NewMat(r, c, 0), NewMat(r, c, 0)},
			ref:      pair,
		},
	}
}

func (m *MatMulNode) Forward() {
	pair := m.ref.(NodePair)
	for i := 0; i < pair.l.Vals().rows; i++ {
		for j := 0; j < pair.l.Vals().cols; j++ {
			for k := 0; k < pair.r.Vals().cols; k++ {
				m.vals.Inc(i, k, pair.l.Vals().At(i, j)*pair.r.Vals().At(j, k))
			}
		}
	}
}

func (m *MatMulNode) backwardImpl(n1, n2 Node, maxI, maxJ, maxK int, reverse bool) {
	for i := 0; i < maxI; i++ {
		for j := 0; j < maxJ; j++ {
			for k := 0; k < maxK; k++ {
				var grad float64
				if reverse {
					grad = m.grads.At(k, j) * n2.Vals().At(k, i)
					n1.Grads().Inc(i, j, grad)
				} else {
					grad = m.grads.At(i, k) * n2.Vals().At(j, k)
					n1.Grads().Inc(i, j, grad)
				}
			}
		}
	}
}

func (m *MatMulNode) Backward() {
	pair := m.ref.(NodePair)
	m.backwardImpl(pair.l, pair.r, pair.l.Grads().rows, pair.l.Grads().cols, pair.r.Vals().cols, false)
	m.backwardImpl(pair.r, pair.l, pair.l.Grads().cols, pair.r.Grads().cols, pair.l.Grads().rows, true)
}

type AddNode struct {
	NodeHolder
}

func MakeAddNode(args []Node) *AddNode {
	a := args[0].Vals().rows
	b := args[0].Vals().cols
	return &AddNode{
		NodeHolder: NodeHolder{
			NodeBase: NodeBase{NewMat(a, b, 0), NewMat(a, b, 0)},
			ref:      args,
		},
	}
}

func (a *AddNode) addTo(d, src *Mat) {
	for i := 0; i < d.rows; i++ {
		for j := 0; j < d.cols; j++ {
			d.Inc(i, j, src.At(i, j))
		}
	}
}

func (a *AddNode) Forward() {
	args := a.ref.([]Node)
	for _, n := range args {
		a.addTo(a.vals, n.Vals())
	}
}

func (a *AddNode) Backward() {
	args := a.ref.([]Node)
	for _, n := range args {
		a.addTo(n.Grads(), a.grads)
	}
}

type ReluNode struct {
	NodeHolder
	leak float64
}

func MakeReluNode(arg Node, leak float64) *ReluNode {
	return &ReluNode{
		NodeHolder: NodeHolder{
			NodeBase: NodeBase{NewMat(arg.Vals().rows, arg.Vals().cols, 0), NewMat(arg.Vals().rows, arg.Vals().cols, 0)},
			ref:      arg,
		},
		leak: leak,
	}
}

func (r *ReluNode) Forward() {
	arg := r.ref.(Node)
	for i := 0; i < r.vals.rows; i++ {
		for j := 0; j < r.vals.cols; j++ {
			v := arg.Vals().At(i, j)
			if v < 0 {
				v /= r.leak
			}
			r.vals.Set(i, j, v)
		}
	}
}

func (r *ReluNode) Backward() {
	arg := r.ref.(Node)
	for i := 0; i < r.vals.rows; i++ {
		for j := 0; j < r.vals.cols; j++ {
			grad := r.grads.At(i, j)
			if arg.Vals().At(i, j) < 0 {
				grad /= r.leak
			}
			arg.Grads().Inc(i, j, grad)
		}
	}
}

type HadamardNode struct {
	NodeHolder
}

func MakeHadamardNode(args []Node) *HadamardNode {
	a := args[0].Vals().rows
	b := args[0].Vals().cols
	return &HadamardNode{
		NodeHolder: NodeHolder{
			NodeBase: NodeBase{NewMat(a, b, 1), NewMat(a, b, 0)},
			ref:      args,
		},
	}
}

func (h *HadamardNode) Forward() {
	args := h.ref.([]Node)
	for _, n := range args {
		for i := 0; i < n.Vals().rows; i++ {
			for j := 0; j < n.Vals().cols; j++ {
				h.vals.Scale(i, j, n.Vals().At(i, j))
			}
		}
	}
	// fmt.Printf("HadamardNode Forward: %v\n", h.vals)
}

func (h *HadamardNode) Backward() {
	args := h.ref.([]Node)
	for idx := range args {
		for i := 0; i < args[idx].Grads().rows; i++ {
			for j := 0; j < args[idx].Grads().cols; j++ {
				prod := 1.0
				for k := range args {
					if k != idx {
						prod *= args[k].Vals().At(i, j)
					}
				}
				prod *= h.grads.At(i, j)
				args[idx].Grads().Inc(i, j, prod)
			}
		}
	}
}

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	if !scanner.Scan() {
		log.Fatal("error: failed to read the first line")
	}
	params := strings.Fields(scanner.Text())
	n, _ := strconv.Atoi(params[0])
	m, _ := strconv.Atoi(params[1])
	k, _ := strconv.Atoi(params[2])

	nodes := make([]Node, 0, n)
	for i := 0; i < n; i++ {
		scanner.Scan()
		cmd := strings.Fields(scanner.Text())
		if cmd[0] == "var" {
			r, _ := strconv.Atoi(cmd[1])
			c, _ := strconv.Atoi(cmd[2])
			nodes = append(nodes, &NodeBase{NewMat(r, c, 0), NewMat(r, c, 0)})
		} else if cmd[0] == "tnh" {
			idx, _ := strconv.Atoi(cmd[1])
			nodes = append(nodes, MakeTanhNode(nodes[idx-1]))
		} else if cmd[0] == "rlu" {
			alpha, _ := strconv.Atoi(cmd[1])
			idx, _ := strconv.Atoi(cmd[2])
			nodes = append(nodes, MakeReluNode(nodes[idx-1], float64(alpha)))
		} else if cmd[0] == "mul" {
			l, _ := strconv.Atoi(cmd[1])
			r, _ := strconv.Atoi(cmd[2])
			nodes = append(nodes, MakeMatMulNode(NodePair{nodes[l-1], nodes[r-1]}))
		} else if cmd[0] == "sum" {
			args := make([]Node, 0, len(cmd)-2)
			for _, s := range cmd[2:] {
				idx, _ := strconv.Atoi(s)
				args = append(args, nodes[idx-1])
			}
			nodes = append(nodes, MakeAddNode(args))
		} else if cmd[0] == "had" {
			args := make([]Node, 0, len(cmd)-2)
			for _, s := range cmd[2:] {
				idx, _ := strconv.Atoi(s)
				args = append(args, nodes[idx-1])
			}
			nodes = append(nodes, MakeHadamardNode(args))
		}
	}
	for v := 0; v < m; v++ {
		nodes[v].Vals().Read(scanner)
		// fmt.Printf("Vals %d: %v\n", v, nodes[v].Vals())
	}
	for i := 0; i < k; i++ {
		nodes[n-k+i].Grads().Read(scanner)
		// fmt.Printf("Grads %d: %v\n", n-k+i, nodes[n-k+i].Grads())
	}
	for _, node := range nodes {
		node.Forward()
		// fmt.Printf("Forward: %v\n", node.Vals())
	}
	for i := len(nodes) - 1; i >= 0; i-- {
		nodes[i].Backward()
		// fmt.Printf("Backward: %v\n", nodes[i].Grads())
	}
	for i := 0; i < k; i++ {
		fmt.Println(nodes[n+i-k].Vals())
	}
	for i := 0; i < m; i++ {
		fmt.Println(nodes[i].Grads())
	}
}
