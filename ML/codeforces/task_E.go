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

/*
   x[t]      h[t-1]
     │         │
     └───┬─────┘
         ▼
   ┌───────────────┐
   │   LSTM Cell   │
   │ ┌─┬─┬─┬─────┐ │
   │ │F│I│O│ G   │ │
   │ └─┴─┴─┴─────┘ │
   └─────┬─────────┘
         ▼
       c[t-1]
         │
         ▼
     ┌────────┐
     │  c[t]  │
     └──┬───┬─┘
        │   │
        ▼   ▼
      tanh  o
        │   │
        └─⊙─┘
          │
        h[t]
*/

type Gate struct {
	W, U [][]float64
	B    []float64
}

type LSTMCell struct {
	Size      int
	Forget    Gate
	Input     Gate
	Output    Gate
	Candidate Gate
}

func sigmoid(val float64) float64 {
	return 1.0 / (1.0 + math.Exp(-val))
}

func gradSigmoid(out float64) float64 {
	return out * (1.0 - out)
}

func gradTanh(out float64) float64 {
	return 1.0 - out*out
}

func gradGateO(dhAgg, oT float64) float64 {
	return dhAgg * gradSigmoid(oT)
}

func gradGateF(dcAgg, cOld, fT float64) float64 {
	return dcAgg * cOld * gradSigmoid(fT)
}

func gradGateI(dcAgg, gT, iT float64) float64 {
	return dcAgg * gT * gradSigmoid(iT)
}

func gradGateG(dcAgg, iT, gT float64) float64 {
	return dcAgg * iT * gradTanh(gT)
}

func scanMatrix(dim int, sc *bufio.Scanner) [][]float64 {
	result := make([][]float64, dim)
	for i := 0; i < dim; i++ {
		if !sc.Scan() {
			log.Fatalf("Error reading on row %d", i)
		}
		parts := strings.Fields(sc.Text())
		row := make([]float64, dim)
		for j := 0; j < dim; j++ {
			val, err := strconv.ParseFloat(parts[j], 64)
			if err != nil {
				log.Fatalf("Element conversion error [%d][%d]: %v", i, j, err)
			}
			row[j] = val
		}
		result[i] = row
	}
	return result
}

func scanVector(dim int, sc *bufio.Scanner) []float64 {
	if !sc.Scan() {
		log.Fatal("Error reading on row 0")
	}
	parts := strings.Fields(sc.Text())
	vec := make([]float64, dim)
	for i := 0; i < dim; i++ {
		val, err := strconv.ParseFloat(parts[i], 64)
		if err != nil {
			log.Fatalf("Element conversion error [%d]: %v", i, err)
		}
		vec[i] = val
	}
	return vec
}

func loadGate(dim int, sc *bufio.Scanner) Gate {
	return Gate{
		W: scanMatrix(dim, sc),
		U: scanMatrix(dim, sc),
		B: scanVector(dim, sc),
	}
}

func multiplyMatVec(mat [][]float64, vec []float64) []float64 {
	n := len(mat)
	res := make([]float64, n)
	for i := 0; i < n; i++ {
		for j := 0; j < n; j++ {
			res[i] += mat[i][j] * vec[j]
		}
	}
	return res
}

func addVec(a, b []float64) []float64 {
	out := make([]float64, len(a))
	for i := range a {
		out[i] = a[i] + b[i]
	}
	return out
}

func hadamard(a, b []float64) []float64 {
	out := make([]float64, len(a))
	for i := range a {
		out[i] = a[i] * b[i]
	}
	return out
}

func addMats(a, b [][]float64) [][]float64 {
	n := len(a)
	ans := make([][]float64, n)
	for i := 0; i < n; i++ {
		ans[i] = make([]float64, n)
		for j := 0; j < n; j++ {
			ans[i][j] = a[i][j] + b[i][j]
		}
	}
	return ans
}

func mapVec(x []float64, fn func(float64) float64) []float64 {
	out := make([]float64, len(x))
	for i := range x {
		out[i] = fn(x[i])
	}
	return out
}

func outerProduct(x, y []float64) [][]float64 {
	n := len(x)
	mat := make([][]float64, n)
	for i := 0; i < n; i++ {
		mat[i] = make([]float64, n)
		for j := 0; j < n; j++ {
			mat[i][j] = x[i] * y[j]
		}
	}
	return mat
}

func gateActivation(gate Gate, x, h []float64, activation func(float64) float64) []float64 {
	return mapVec(
		addVec(
			addVec(
				multiplyMatVec(gate.W, x),
				multiplyMatVec(gate.U, h),
			),
			gate.B,
		),
		activation,
	)
}

func matVecTProduct(mat [][]float64, vec []float64) []float64 {
	n := len(mat)
	res := make([]float64, n)
	for i := 0; i < n; i++ {
		for j := 0; j < n; j++ {
			res[j] += mat[i][j] * vec[i]
		}
	}
	return res
}

func sumMatVecTProducts(mats [4][][]float64, grads [4][]float64, size int) []float64 {
	res := make([]float64, size)
	for k := 0; k < 4; k++ {
		tmp := matVecTProduct(mats[k], grads[k])
		for i := 0; i < size; i++ {
			res[i] += tmp[i]
		}
	}
	return res
}

func (cell *LSTMCell) Forward(seqLen int, hInit, cInit []float64, inputs [][]float64) ([][]float64, [][]float64, [][]float64, [][]float64, [][]float64, [][]float64) {
	hidden := make([][]float64, 0, seqLen+1)
	cellState := make([][]float64, 0, seqLen+1)
	forgetList := make([][]float64, 0, seqLen)
	inGateList := make([][]float64, 0, seqLen)
	outputList := make([][]float64, 0, seqLen)
	candidateList := make([][]float64, 0, seqLen)
	hidden = append(hidden, hInit)
	cellState = append(cellState, cInit)
	for t := 0; t < seqLen; t++ {
		f := gateActivation(cell.Forget, inputs[t], hidden[t], sigmoid)
		inGate := gateActivation(cell.Input, inputs[t], hidden[t], sigmoid)
		o := gateActivation(cell.Output, inputs[t], hidden[t], sigmoid)
		g := gateActivation(cell.Candidate, inputs[t], hidden[t], math.Tanh)
		cNext := addVec(hadamard(f, cellState[t]), hadamard(inGate, g))
		hNext := hadamard(o, cNext)
		forgetList = append(forgetList, f)
		inGateList = append(inGateList, inGate)
		outputList = append(outputList, o)
		candidateList = append(candidateList, g)
		cellState = append(cellState, cNext)
		hidden = append(hidden, hNext)
	}
	return hidden, cellState, forgetList, inGateList, outputList, candidateList
}

func (cell *LSTMCell) Backward(
	seqLen int,
	h, c, x, f, inGate, o, g [][]float64,
	sc *bufio.Scanner,
) ([][]float64, []float64, []float64, [][][]float64, [][][]float64, [][]float64) {
	dhT := scanVector(cell.Size, sc)
	dcT := scanVector(cell.Size, sc)
	extraGrads := make([][]float64, seqLen)
	for idx := 0; idx < seqLen; idx++ {
		extraGrads[seqLen-1-idx] = scanVector(cell.Size, sc)
	}

	gradW := make([][][]float64, 4)
	gradU := make([][][]float64, 4)
	gradB := make([][]float64, 4)
	for k := 0; k < 4; k++ {
		gradW[k] = make([][]float64, cell.Size)
		gradU[k] = make([][]float64, cell.Size)
		for j := 0; j < cell.Size; j++ {
			gradW[k][j] = make([]float64, cell.Size)
			gradU[k][j] = make([]float64, cell.Size)
		}
		gradB[k] = make([]float64, cell.Size)
	}

	dx := make([][]float64, seqLen)
	dhNext := make([]float64, cell.Size)
	copy(dhNext, dhT)
	dcNext := make([]float64, cell.Size)
	copy(dcNext, dcT)

	for t := seqLen - 1; t >= 0; t-- {
		xT := x[t]
		hOld := h[t]
		cOld := c[t]
		fT := f[t]
		iT := inGate[t]
		oT := o[t]
		gT := g[t]
		cT := c[t+1]

		dhAgg := make([]float64, cell.Size)
		for i := 0; i < cell.Size; i++ {
			dhAgg[i] = extraGrads[t][i] + dhNext[i]*cT[i]
		}
		dcAgg := make([]float64, cell.Size)
		for i := 0; i < cell.Size; i++ {
			dcAgg[i] = dcNext[i] + dhNext[i]*oT[i]
		}

		gradO := make([]float64, cell.Size)
		gradF := make([]float64, cell.Size)
		gradI := make([]float64, cell.Size)
		gradG := make([]float64, cell.Size)
		for j := 0; j < cell.Size; j++ {
			gradO[j] = gradGateO(dhAgg[j], oT[j])
			gradF[j] = gradGateF(dcAgg[j], cOld[j], fT[j])
			gradI[j] = gradGateI(dcAgg[j], gT[j], iT[j])
			gradG[j] = gradGateG(dcAgg[j], iT[j], gT[j])
		}
		grads := [][]float64{gradF, gradI, gradO, gradG}
		for idx, grad := range grads {
			gradW[idx] = addMats(gradW[idx], outerProduct(grad, xT))
			gradU[idx] = addMats(gradU[idx], outerProduct(grad, hOld))
			for j := 0; j < cell.Size; j++ {
				gradB[idx][j] += grad[j]
			}
		}
		Ws := [4][][]float64{cell.Forget.W, cell.Input.W, cell.Output.W, cell.Candidate.W}
		Us := [4][][]float64{cell.Forget.U, cell.Input.U, cell.Output.U, cell.Candidate.U}
		gradsArr := [4][]float64{gradF, gradI, gradO, gradG}
		dx[t] = sumMatVecTProducts(Ws, gradsArr, cell.Size)
		dhNew := sumMatVecTProducts(Us, gradsArr, cell.Size)
		copy(dhNext, dhNew)
		for i := 0; i < cell.Size; i++ {
			dcNext[i] = dcAgg[i] * fT[i]
		}
	}
	return dx, dhNext, dcNext, gradW, gradU, gradB
}

func printVec(w *bufio.Writer, v []float64) {
	for i, value := range v {
		if i > 0 {
			err := w.WriteByte(' ')
			if err != nil {
				return
			}
		}
		_, err := fmt.Fprintf(w, "%.17g", value)
		if err != nil {
			return
		}
	}
	err := w.WriteByte('\n')
	if err != nil {
		return
	}
}

func printMat(w *bufio.Writer, m [][]float64) {
	for i := 0; i < len(m); i++ {
		printVec(w, m[i])
	}
}

func main() {
	scanner := bufio.NewScanner(os.Stdin)
	scanner.Split(bufio.ScanLines)
	writer := bufio.NewWriter(os.Stdout)

	if !scanner.Scan() {
		log.Fatal("scanning failed")
	}
	dim, err := strconv.Atoi(scanner.Text())
	if err != nil {
		log.Fatalf("Size conversion error (dim): %v", err)
	}

	cell := LSTMCell{Size: dim}
	cell.Forget = loadGate(dim, scanner)
	cell.Input = loadGate(dim, scanner)
	cell.Output = loadGate(dim, scanner)
	cell.Candidate = loadGate(dim, scanner)

	if !scanner.Scan() {
		log.Fatal("scanning failed")
	}
	steps, err := strconv.Atoi(scanner.Text())
	if err != nil {
		log.Fatalf("Sequence length conversion error (steps): %v", err)
	}

	hStart := scanVector(dim, scanner)
	cStart := scanVector(dim, scanner)
	inputSeq := make([][]float64, steps)
	for i := 0; i < steps; i++ {
		inputSeq[i] = scanVector(dim, scanner)
	}
	h, c, f, inGate, o, g := cell.Forward(steps, hStart, cStart, inputSeq)
	for t := 0; t < steps; t++ {
		printVec(writer, o[t])
	}
	printVec(writer, h[steps])
	printVec(writer, c[steps])
	dx, dh0, dc0, dW, dU, dB := cell.Backward(steps, h, c, inputSeq, f, inGate, o, g, scanner)
	for t := steps - 1; t >= 0; t-- {
		printVec(writer, dx[t])
	}
	printVec(writer, dh0)
	printVec(writer, dc0)
	for k := 0; k < 4; k++ {
		printMat(writer, dW[k])
		printMat(writer, dU[k])
		printVec(writer, dB[k])
	}

	defer func(writer *bufio.Writer) {
		err := writer.Flush()
		if err != nil {
			return
		}
	}(writer)
}
