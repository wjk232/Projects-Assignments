/**********************************************************************
cacheSimu.h
Purpose:
    This defines the tyoedefs,macros, and prototypes used
    in program. 
Notes:

**********************************************************************/

typedef struct cache{
   char tag[13];
   int time;
   //char data[1024];
}cache;
typedef struct lru{
   int leastacc;
   int time;
}lru;
typedef struct memory{
   char tag[13];
   int index;
   int offset; 
}memory;

void err_sys(const char* x);
void cacheSimu(int n, int m, int p, char * policy, char *status, char *pfile, char **agrv);
int power(int n, int m);
int convHexToDeci(char *szhex);
int *convDeciToBinary(int deci);
int convBinaryToDeci(int *iBinary);
char *convDeciToHex(int iDeci);
char *strlowr(char *str);
void memparse(int *binary, struct memory *addr,int indexnum,int offsetnum);
int sizeArr(int *arr);
#define VAR memHex+2,addr.tag+2, szIndexH+2,hitmiss,iHits,iMisses,iAccesses,1-(((double)iHits)/iAccesses)
#define PRINT " Address      Tag    Set #  H/M   #Hits #Misses   #Accs  Miss ratio\n"
#define ARGV argv[0],argv[1],argv[2],argv[3],argv[4],argv[5],argv[6]
