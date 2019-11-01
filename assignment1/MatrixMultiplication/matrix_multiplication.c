#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <string.h>

/* Multiply n x n matrices a and b  */
void multiply_naive(double *a, double *b, double *c, int n) {
  int i, j, k;
  for (i = 0; i < n; i++)
    for (j = 0; j < n; j++)
      for (k = 0; k < n; k++)
        c[i*n + j] += a[i*n + k]*b[k*n + j];
}

/* Multiply n x n matrices a and b  */
void multiply_blocked(double *a, double *b, double *c, int n, int B) {
  int i, j, k, i1, j1, k1;
  for (i = 0; i < n; i+=B)
    for (j = 0; j < n; j+=B)
      for(k = 0; k < n; k+=B)
        /* B x B mini matrix multiplications */
        // DONE: Complete this method
        for (i1 = 0; i1 < B; i1++)
          for (j1 = 0; j1 < B; j1++)
            for(k1 = 0; k1 < B; k1++)
            {
              c[n*(i+i1)+(j+j1)] += a[n*(i+i1)+(k+k1)] * b[n*(k+k1)+(j+j1)];
            }
}

void readMatrix(char* filename, double* matrix, int n) {
  int i, j;
  double x;
  FILE* fp = fopen(filename, "r");

  for (i = 0; i < n; i++){
    for(j = 0; j < n; j++){
      fscanf(fp, "%lf", &x);
      matrix[i*n + j] = x;
    }
  }
  fclose(fp);
}


int main ( int arc, char **argv ) {
  char* method = argv[1];
  int n = atoi(argv[2]);
  char *filenameA = argv[3];
  char *filenameB = argv[4];
  int block_size=n;
  if (strcmp(method,"blocked") == 0) {
    block_size = atoi(argv[5]);
  }

  printf("method=%s\nN=%d\nB=%d\nmatrix A: %s\nmatrix B: %s\n", method, n, block_size, filenameA, filenameB);
  double* a = (double *) calloc(sizeof(double), n*n);
  double* b = (double *) calloc(sizeof(double), n*n);

  readMatrix(filenameA, a, n);
  readMatrix(filenameB, b, n);

  // DONE: initialize matrix c
  double* c = (double *) calloc(sizeof(double), n*n);

  clock_t begin, end;
  double time_spent;

  begin = clock();
  if (strcmp(method,"blocked") == 0){
    multiply_blocked(a, b, c, n, block_size);
  } else {
    multiply_naive(a, b, c, n);
  }
  end = clock();
  time_spent = (double)(end - begin) / CLOCKS_PER_SEC;
  printf("time: %lf\n", time_spent);

#if 0
  printf("Output C:\n");
  for (int i = 0; i < n; i++)
  {
    for (int j = 0; j < n; j++)
    {
      printf("%lf ", (double) *(c+n*i+j));
    }
    printf("\n");
  }
#endif

  return 0;
};
