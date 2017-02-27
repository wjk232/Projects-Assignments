/**********************************************************************
k_means.h
Purpose:
    This defines the structs, and prototypes used
    in k_means.c and main.c . 
Notes:

**********************************************************************/

#define MAX_POINTS 32768
#define MAX_CENTERS 16

/*
 * data structure for holding a data point
 */
struct point{
	double x;
	double y;
};

/*
 * read data points from input file
 */
int read_points_from_file(char *data_file, struct point *pts, int *m);

/*
 * find K_means 
 */
void k_means(struct point p[], int m, int k, int iters, struct point u[],
	    int c[], int proc_id, int numProcess);

/*
 * return a random point
 */
struct point random_center(struct point p[]);

