#include <stdio.h>
#include <float.h>
#include <math.h>
#include <mpi.h>

#include "k_means.h"

/****************************************************************************
	void k_means(struct point p[MAX_POINTS], int m, int k,int iters,
         struct point u[MAX_CENTERS],int c[MAX_POINTS],int proc_id,int numProcess)
Purpopse:
   Implements the k_means clustering algorithm using openMPI.
Parameters:
	struct point p[]     array of data points     
	int m                number of data points in p[]
   int k                number of clusters to find
   int iters            number of clustering iterations to run
Returns:
   struct point u[]     array of cluster centers
   int c[]              cluster id for each data points
Notes:

****************************************************************************/

void k_means(struct point p[MAX_POINTS], 
	    int m, 
	    int k,
	    int iters,
	    struct point u[MAX_CENTERS],
	    int c[MAX_POINTS],
       int proc_id,
       int numProcess)
{

   int mPerProcess = m/numProcess;
   int mStart = proc_id * mPerProcess;
   int mEnd = mStart + mPerProcess;   
     
   int sendBufC[m];
   int blocklen[1] = {2};
   
   /* Creates a custom MPI struct with two doubles */
   MPI_Datatype struct_type;
   MPI_Aint offsets[1] = {0};
   MPI_Datatype oldTypes[1] = {MPI_DOUBLE};
   MPI_Type_struct(1,blocklen, offsets,oldTypes, &struct_type);
   MPI_Type_commit(&struct_type);

   int j; /* Iterate 0 to k */
   int i; /* Iterate mStart to mEnd */
   int l; /* Iterate 0 to Iters */
   double sum_x, sum_y,allsum_x,allsum_y; /* the sum of points */
   int cluster_size,allcluster_size; /* points in clusters */
   double min_dist; /* min distance = Maximum of DOUBLE */
   double dist; /* distance between points and centers */
   
   //for not even number of points per process
   if(proc_id == numProcess-1)
      mEnd = (m-(mStart+mPerProcess))+mEnd; 

   if(proc_id == 0){
	   for(j = 0; j < k; j++)
		   u[j] = random_center(p);
   }
   
   MPI_Bcast(u, k, struct_type, 0, MPI_COMM_WORLD);
      
   for(l = 0; l < iters; l++)
   {
      /* find the nearest center to each point */
      for(i = mStart; i < mEnd; i++)
      {           
         min_dist = DBL_MAX;
         /* find the cluster sendBufc[i] for p[i] by 
                              computing p[i]’s distance to all centers */
         for( j = 0; j < k; j++)
         { 
            dist = sqrt(pow(p[i].x - u[j].x,2) + pow(p[i].y - u[j].y,2));
            if( dist < min_dist)
            {
               min_dist = dist;
               sendBufC[i] = j;
            }
         }
      }
      /* based on the cluster assignment, update the center for each cluster */
      for( j = 0; j < k; j++)
      {
         sum_x = 0;
         sum_y = 0;
         cluster_size = 0;
         
         for( i = mStart; i < mEnd;i++)
         {
            if(sendBufC[i] == j)
            {
               sum_x += p[i].x;
               sum_y += p[i].y;
               cluster_size++;
            }
         }
         
         MPI_Barrier(MPI_COMM_WORLD);
         MPI_Reduce(&sum_x, &allsum_x, 1, MPI_DOUBLE, MPI_SUM,0, MPI_COMM_WORLD);
         MPI_Reduce(&sum_y, &allsum_y, 1, MPI_DOUBLE, MPI_SUM,0, MPI_COMM_WORLD);
         MPI_Reduce(&cluster_size, &allcluster_size, 1, MPI_INT, MPI_SUM,0, MPI_COMM_WORLD);
         MPI_Barrier(MPI_COMM_WORLD);
         
         if(proc_id ==  0){
            if(allcluster_size > 0)
            {
               u[j].x = allsum_x/allcluster_size;
               u[j].y = allsum_y/allcluster_size;
            }else
               u[j] = random_center(p);
         }
      }
      
      MPI_Bcast(u, k, struct_type, 0, MPI_COMM_WORLD);
   }
   
   MPI_Allgather(sendBufC+mStart, mPerProcess, MPI_INT, c, mPerProcess, MPI_INT, MPI_COMM_WORLD);	
   MPI_Barrier(MPI_COMM_WORLD);
   MPI_Type_free(&struct_type);
   return;
}
