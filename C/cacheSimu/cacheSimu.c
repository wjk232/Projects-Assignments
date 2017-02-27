/*************************************************************************
Purpose:
   This a simple cache simulation program for a direct mapped cache with policy
   and calculate the hit ratio.
Command Parameters:
   ./executable cachesize associativity fifo/lru blocksize 
                                             tracing(on/off) filename
Note:
   chachesize is log 2  of the cache size
   blocksize is the log 2  of the block size
   associativity is 2^associativity
   example:
      ./cacheSimu 16 6 3 lru on memory-medium.txt
*************************************************************************/
#include<stdio.h>
#include<stdlib.h>
#include <fcntl.h>
#include<string.h>
#include<math.h>
#include"cacheSimu.h"

int main(int argc, char *argv[]){
   int p=atoi(argv[3]);
   if(argc != 7)
      err_sys("Error: few arguments:\nn m p fifo/lru on/off filename"); 
   else if(atoi(argv[1]) <= 0 || atoi(argv[2]) <= 0)
      err_sys("integer needs to be greater than 0");
   else if((atoi(argv[2])) > atoi(argv[1]))
      err_sys("second cmd parameter cannot be greater than first input");
   else if(strcmp(strlowr(argv[4]),"fifo") != 0 && 
                                     strcmp(strlowr(argv[4]),"lru") != 0)             
      err_sys("Error: invalid argument #4\nfifo/lru");
   else if(strcmp(strlowr(argv[5]),"on") != 0 && 
                                     strcmp(strlowr(argv[5]),"off") != 0)             
      err_sys("Error: invalid argument #5\noff/on");
   if(atoi(argv[3]) > (atoi(argv[1])-atoi(argv[2])) || atoi(argv[3]) < 0)
      p = atoi(argv[1])-atoi(argv[2]);
   
   cacheSimu(atoi(argv[1]),atoi(argv[2]), p, argv[4], 
                                                     argv[5], argv[6], argv);

   return 0;
}

/****************************************************************************
	void cacheSimu(int n, int m, char *status, char *pfile,char **argv)
Purpopse:
   Implements the directed cache simulation, converts binary.
Parameters:
	int n                log2 cache size     
	int m                log2 block size
	int p                the associativity is 2 p
	char *policy         fifo or lru, indicating the replacement policy
   char *status         trace on/off
   char *pfile          filename for reading memory addresses
   char **argv          command parameters
Notes:

****************************************************************************/
void cacheSimu(int n, int m, int p, char *policy, char *status, char *pfile,
                                                                char **argv){
   int iCachesz = (int)(pow(2,n));
   int iBlocksz = (int)pow(2,m);
   int iNumBlocks = (int)((iCachesz)/iBlocksz);
   int iNumSets = (int) iNumBlocks/ pow(2,p);  
   int iIndex = ((log(iNumSets))/log(2));
   int iOffset = m;
   int iSetAssoc = pow(2,p);
   int *iBinarynum=0;
   char buf[1024];
   char *szOffsetH;
   char *szIndexH;  
   char hitmiss[5];
   char *memHex;
   int iMisses=0,iHits=0,iAccesses=0;
   int i =0,j =0;
   int iIsEmpty=-1;
   int iChoose=0,iTimeCmp=0; 
   lru lru[iNumSets];  
   cache cacheBlock[iNumSets][iSetAssoc];
   memory addr;

   FILE *file=NULL;
   file = fopen(pfile,"r");
   if (file == NULL) {
    	printf("Error openning file\n");
    	return;
   }
   if(strcmp(strlowr(status),"on") == 0)
      printf(PRINT);
   /* reads addresses and converts them to deci/hex/binary */   
   while(fscanf(file,"%s",buf)>0){
      iAccesses += 1;
      memHex = convDeciToHex(convHexToDeci(buf));
      iBinarynum = convDeciToBinary(convHexToDeci(buf));
      memparse(iBinarynum,&addr,iIndex,iOffset);
      szOffsetH = convDeciToHex(addr.offset);
      szIndexH = convDeciToHex(addr.index); 
      
      for(i=0; i < iSetAssoc; i++){
         /* chechs if cache block is empty */
         if(cacheBlock[addr.index][i].tag[1] != 'x'){
           cacheBlock[addr.index][i].time = 0;
           strcpy(cacheBlock[addr.index][i].tag,"   ");
           iIsEmpty = i;
         }
         /* finds if its a Hit/Miss (if its in cache or not) */        
         if(strcmp(cacheBlock[addr.index][i].tag,addr.tag) == 0){
            strcpy(hitmiss,"Hit");
            hitmiss[3] = '\0';
            iHits += 1;
            break;
         }else{
            strcpy(hitmiss,"Miss");
            hitmiss[4]='\0';  
         }
      } 
      if(strcmp(hitmiss,"Miss") == 0){
         iMisses += 1;
         i -= 1;
      }   
      if(strcmp(strlowr(status),"on") == 0){
         printf("%8s%9s%9s%5s%8d%8d%8d%12.8f ", VAR);
         for(j=0; j< iSetAssoc;j++){
            if(cacheBlock[addr.index][j].tag[1] == 'x'){
               printf("%s(%d)",cacheBlock[addr.index][j].tag+2,cacheBlock[addr.index][j].time);
               if(j < iSetAssoc -1)
                  printf(","); 
               }
            }
         printf("\n");
      }
      /* adds/change access time to memory addr tag */
      if(strcmp(hitmiss,"Hit") == 0){
         if(strcmp(strlowr(policy),"lru")==0){
            cacheBlock[addr.index][i].time += 1;
         }   
         if(strcmp(strlowr(policy),"fifo")==0)
            cacheBlock[addr.index][i].time = iAccesses;
      }
      /* if its a miss adds address tag in cache with access time*/ 
      if((iIsEmpty == -1) && strcmp(hitmiss,"Miss") == 0){
         for(j=0; j< iSetAssoc;j++){
            if(j == 0 && strcmp(strlowr(policy),"fifo"))
               iTimeCmp = cacheBlock[addr.index][j].time;
            else if(cacheBlock[addr.index][j].time < iTimeCmp){
               iChoose = j;
               iTimeCmp = cacheBlock[addr.index][j].time;
            }       
         }
         strcpy(cacheBlock[addr.index][iChoose].tag,addr.tag);
         if(strcmp(strlowr(policy),"fifo")==0)
            cacheBlock[addr.index][iChoose].time = iAccesses;
      }else if((iIsEmpty >= 0) && strcmp(hitmiss,"Miss") == 0){
         strcpy(cacheBlock[addr.index][iIsEmpty].tag,addr.tag);
         if(strcmp(strlowr(policy),"fifo")==0)
            cacheBlock[addr.index][iIsEmpty].time = iAccesses;
      }
      
      iIsEmpty = -1;
      free(szOffsetH);
      free(szIndexH);
      free(memHex);
   }
   printf("Rogelio Espinoza\n");
   printf("%s %s %s %s %s %s %s\n",ARGV);
   printf("memory access: %d\n", iAccesses);
   printf("hits: %d\n",iHits);
   printf("misses: %d\n",iMisses);
   printf("miss ratio: %.8f\n",1-((double)(iHits)/iAccesses));
   fclose(file);
}
