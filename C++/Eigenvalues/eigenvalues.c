#include "return_codes.h"

#include <float.h>
#include <math.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#define MAX_ITER 2000
#define EPSILON 1e-6

// Function to allocate memory for a matrix
double *allocateArray(int n)
{
	double *matrix = (double *)malloc(n * n * sizeof(double));
	if (matrix == NULL)
	{
		return NULL;
	}
	memset(matrix, 0, n * n * sizeof(double));
	return matrix;
}

void eigenvalues_complex(double a, double b, double c, double d, double *real, double *imag)
{
	double tr = a + d;
	double det = a * d - b * c;
	double des = tr * tr - 4 * det;
	*real = tr / 2;
	*imag = sqrt(fabs(des)) / 2;
}

// Function to perform QR decomposition of a matrix
void qr_decomposition(double *a, int n, double *q, double *r)
{
	for (int j = 0; j < n; j++)
	{
		r[j * n + j] = 0;
		for (int i = 0; i < n; i++)
		{
			r[j * n + j] += a[i * n + j] * a[i * n + j];
		}
		r[j * n + j] = sqrt(r[j * n + j]);
		for (int i = 0; i < n; i++)
		{
			q[i * n + j] = a[i * n + j] / r[j * n + j];
		}
		for (int k = j + 1; k < n; k++)
		{
			r[j * n + k] = 0;
			for (int i = 0; i < n; i++)
			{
				r[j * n + k] += q[i * n + j] * a[i * n + k];
			}
			for (int i = 0; i < n; i++)
			{
				a[i * n + k] -= q[i * n + j] * r[j * n + k];
			}
		}
	}
}

// Function to perform matrix multiplication
void matmul(const double *A, const double *B, double *C, int n)
{
	for (int i = 0; i < n; i++)
	{
		for (int j = 0; j < n; j++)
		{
			C[i * n + j] = 0;
			for (int k = 0; k < n; k++)
			{
				C[i * n + j] += A[i * n + k] * B[k * n + j];
			}
		}
	}
}

// Function to compute the eigenvalues of a matrix using QR algorithm
int qr_eigenvalues(double *A, int n, double *eigenvalues, double *eigenvalues_im)
{
	int i, iter;
	double *Q = allocateArray(n);
	if (Q == NULL)
		return 1;

	double *R = allocateArray(n);
	if (R == NULL)
	{
		free(Q);
		return 1;
	}

	iter = 0;
	while (iter < MAX_ITER)
	{
		qr_decomposition(A, n, Q, R);
		matmul(R, Q, A, n);
		double diagonal_sum = 0;
		double not_diagonal_sum = 0;
		for (int j = 0; j < n; ++j)
		{
			diagonal_sum += A[j * n + j];
			not_diagonal_sum += A[j * n + n / 2];
		}
		if (fabs(not_diagonal_sum - diagonal_sum) < n * EPSILON)
		{
			break;
		}
		iter++;
	}

	for (i = 0; i < n; i++)
	{
		if (i != n - 1 && fabs(A[i * n + i] / A[i * n + i + 1]) < 3)
		{
			eigenvalues_complex(A[i * n + i], A[i * n + i + 1], A[(i + 1) * n + i], A[(i + 1) * n + i + 1], &eigenvalues_im[i], &eigenvalues_im[i + 1]);
			i++;
		}
		else
		{
			eigenvalues[i] = A[i * n + i];
		}
	}

	free(Q);
	free(R);
	return 0;
}

int main(int argc, char *argv[])
{
	if (argc != 3)
	{
		fprintf(stderr, "Error: you need to pass only the input and output file as arguments\n");
		return ERROR_PARAMETER_INVALID;
	}

	FILE *in = fopen(argv[1], "r");
	if (in == NULL)
	{
		fprintf(stderr, "Error: Could not open input file \"%s\n", argv[1]);
		return ERROR_CANNOT_OPEN_FILE;
	}

	int n;
	fscanf(in, "%d", &n);

	double *A = allocateArray(n);
	if (A == NULL)
	{
		fprintf(stderr, "Exception: not enough memory");
		fclose(in);
		return ERROR_OUT_OF_MEMORY;
	}

	for (int i = 0; i < n; ++i)
	{
		for (int j = 0; j < n; ++j)
		{
			fscanf(in, "%lf", &A[i * n + j]);
		}
	}
	fclose(in);

	double *e = malloc(sizeof(double) * n);
	if (e == NULL)
	{
		fprintf(stderr, "Exception: not enough memory");
		free(A);
		return ERROR_OUT_OF_MEMORY;
	}

	double *e_im = malloc(sizeof(double) * n);
	if (e_im == NULL)
	{
		fprintf(stderr, "Exception: not enough memory");
		free(A);
		free(e);
		return ERROR_OUT_OF_MEMORY;
	}

	for (int i = 0; i < n; ++i)
	{
		e_im[i] = DBL_MAX;
	}

	// Calculate eigenvalues
	int success_code = qr_eigenvalues(A, n, e, e_im);
	free(A);
	if (success_code != 0)
	{
		fprintf(stderr, "Exception: not enough memory");
		free(e);
		free(e_im);
		return ERROR_OUT_OF_MEMORY;
	}

	FILE *out = fopen(argv[2], "w");
	if (out == NULL)
	{
		fprintf(stderr, "Error: Could not open output file \"%s\n", argv[2]);
		free(e);
		free(e_im);
		return ERROR_CANNOT_OPEN_FILE;
	}

	for (int i = 0; i < n; ++i)
	{
		if (e_im[i] != DBL_MAX)
		{
			fprintf(out, "%g +%gi\n", e_im[i], e_im[i + 1]);
			fprintf(out, "%g -%gi\n", e_im[i], e_im[i + 1]);
			i++;
		}
		else
		{
			fprintf(out, "%g\n", e[i]);
		}
	}
	fclose(out);

	free(e_im);
	free(e);
	return SUCCESS;
}