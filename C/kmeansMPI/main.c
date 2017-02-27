/****************************************************************************
Purpose:
    This program demostrates the use of k_means clustering algorithm using OpenMPI
Command Parameters:
    ./executable -f inputFile -k numCluster -i iterations 
        The input file contains points that are use for clustering.
Notes:
    ./executable provides you the usage
    example:
         mpirun -np 8 ./k_means -f data/example4_k8_m10000.txt -k 8 -i 10000

 ****************************************************************************/

#include <stdio.h>
#include <stdlib.h>
#include <getopt.h>
#include <string.h>
#include <err.h>
#include <mpi.h>

#include "k_means.h"

int counter = 0;
int max = 200;
int min = -200;

int main(int argc, char *argv[])
{
	char *data_file = "";
	int k = 2; /* number of clusters */
	int m = 0; /* number of data points */
	int iters = 10; /* number of iterations of clustering */
	
	int j,i = 0;
	struct point p[MAX_POINTS]; /* array that holds data points */
	struct point u[MAX_CENTERS]; /* array that holds the centers */
	int c[MAX_POINTS]; /* cluster id for each point */

	int proc_id;
   int size;

   if(argc != 7){
   	printf("Usage: ./executable -f filename -k numCluster -i iters\n");
      exit(0);
   }
   
   for (i = 1; i < argc; i++){
      // determine which switch it is
      switch (argv[i][1])
      {
      case 'f':
         ++i;
         data_file = argv[i];
         break;
      case 'k':
         ++i;
         k = atoi(argv[i]);
         if(k > MAX_CENTERS){
            printf("Too many centers (must < %d)\n",
                   MAX_CENTERS);
            exit(-1);
         }
         break;
      case 'i':
         ++i;
         iters = atoi(argv[i]);
         break;
      default:
         exit(0);
      }
   }
   	  
	MPI_Init(&argc,&argv);
	/* get process information */
	MPI_Comm_rank(MPI_COMM_WORLD, &proc_id);
	MPI_Comm_size (MPI_COMM_WORLD, &size);
   
	if(proc_id == 0){
		printf("Reading from data file: %s.\n", data_file);
		printf("Finding %d clusters\n", k);
	}

	/* read data points from file */
	read_points_from_file(data_file, p, &m);
	
	/* do K-Means */
	k_means(p, m, k, iters, u, c, proc_id, size);

	/* output centers and cluster assignment */
	if(proc_id == 0){
		printf("centers found:\n");
		for(j = 0; j < k; j++)
			printf("%lf, %lf\n", u[j].x, u[j].y);
   }

	MPI_Finalize();
	
	return 0;
}

/****************************************************************************
	int read_points_from_file(char *input_file, struct point *pts, int *m)
Purpopse:
   Reads data points from input file
Parameters:
   char *input_file			   name of input file to read points from
   struct point *pts	 			struct pointer to stored read points	
   int *m				         number of data points in p[]
	
Notes:
	This returns the number of lines printed + the lines that were suppressed
****************************************************************************/

int read_points_from_file(char *input_file, struct point *pts, int *m)
{
	FILE *fp;
	size_t len = 0;
	ssize_t read = 0;
	char *line = NULL;
	int ret = 0;

	/* open the file */
	fp = fopen(input_file, "r");
	if(fp == NULL)
		err(-1, "Unable to open file");

	*m = 0;

	/* read in the coordinates of the points */
	while((read = getline(&line, &len, fp)) != -1){
		ret = sscanf(line, "%lf,%lf\n", &(pts[*m].x),
			     &(pts[*m].y));
		if(ret != 2)
			continue;

		/* increase the size of pts if necessary */
		(*m)++;
		if(*m == MAX_POINTS){
			printf("Too many data points (maximum %d points)\n",
			       MAX_POINTS);
			exit(-1);
		}
	}

	fclose(fp);

	if(line)
		free(line);
		
	return 0;
}

/*returns random center */
struct point random_center(struct point p[MAX_POINTS])
{
	int idx = 0;
	idx = __sync_fetch_and_add(&counter, 1)%MAX_POINTS;
	return p[idx];
}

