#include<stdio.h>
#include<stdlib.h>
#include <fcntl.h>
#include<string.h>
#include<math.h>
#include"cacheSimu.h"
#include<errno.h>

/****************************************************************************
   void memparse(int *binary, struct memory *addr,int indexnum,int offsetnum)
Purpopse:
   Implements the directed cache simulation, converts binary.
Parameters:
	int n                log2 cache size     
	int m                log2 block size
   char *status         trace on/off
   char *pfile          filename for reading memory addresses
   char **argv          command parameters
Notes:

****************************************************************************/
void memparse(int *binary, struct memory *addr,int indexnum,int offsetnum){
   int iTag[31];
   int iIndex[31];
   int iOffset[31];
   int i,j=31,k=31;
   char *tagHex;
   
   for(i=31;i>=0;i--){
      if(i > 31-(offsetnum))
         iOffset[i]=binary[i]; 
      else
         iOffset[i]=0;
      if(i <= 31-offsetnum && i > (31-(offsetnum+indexnum))){
         iIndex[j]=binary[i]; 
         j--; 
      }else if(i <= (31-(offsetnum+indexnum))){
         iIndex[j]=0;
         j--; 
      }else
         iIndex[31-i]=0;
      if(i <= (31-((offsetnum+indexnum)))){
         iTag[k]=binary[i]; 
         k--; 
      }else if(i > (31-(offsetnum+indexnum)))
         iTag[31-i]=0;
   }
   addr->index = convBinaryToDeci(iIndex);
   addr->offset=convBinaryToDeci(iOffset);
   tagHex = convDeciToHex(convBinaryToDeci(iTag));
   strcpy(addr->tag,tagHex);
   free(tagHex);
}

/****************************************************************************
	char *convDeciToHex(int iDeci)
Purpopse:
   Converts desimal to hexadecimal.
Parameters:
	int iDeci            decimal to be converted to hexadecimal     
Notes:

****************************************************************************/
char *convDeciToHex(int iDeci){
   char hexrep[16] ={'0','1','2','3','4','5','6','7','8','9','a'
                                                   ,'b','c','d','e','f'};
   int inum = iDeci;
   int i=2;
   char* hex = malloc(11 * sizeof(char));
   
   while(inum > 0){
      inum = inum /16;
      i++;
   }
   if(iDeci == 0){
      hex[i+1]='\0';
      hex[i] = '0';
   }else
      hex[i]='\0';
   hex[0]='0';
   hex[1]='x';
   i--;
   while(iDeci != 0){
      hex[i] = hexrep[iDeci % 16];
      iDeci = iDeci /16;
      i--;
   }
   return hex;
}

/****************************************************************************
	int convBinaryToDeci(int *iBinary)
Purpopse:
   Converts binary to decimal.
Parameters:
	int *iBinary            binary to be ocnverted to decimal     
Notes:

****************************************************************************/
int convBinaryToDeci(int *iBinary){
   int i,j=0;
   int ideci=0;
   
   for(i=31;i>=0;i--){
      if(iBinary[i] == 1)
         ideci += pow(2,j);
      j++;
   }
   return ideci;
}

/****************************************************************************
   int *convDeciToBinary(int deci)
Purpopse:
   Converts decimal to binary.
Parameters:
	int deci                decimal to be converted to binary     
Notes:

****************************************************************************/
int *convDeciToBinary(int deci){
   static int szbinarynum[31];
   int inum=deci;
   int i=0;
  
   for(i=31;i>=0;i--){
      if(inum!=0){
         szbinarynum[i]=(inum % 2);
         inum = inum / 2;
      }else
         szbinarynum[i]=0;
   } 
   return szbinarynum;
}

/****************************************************************************
   int convHexToDeci(char *szhex)
Purpopse:
   Converts hexadecimal to decimal.
Parameters:
	char *szhex             hexadecimal to be converted to decimal     
Notes:

****************************************************************************/
int convHexToDeci(char *szhex){
   int i,decimal = 0;
   int iExp=strlen(szhex)-2;
   
   if(tolower(szhex[1]) == 'x'){
      for(i = 2; i < strlen(szhex);i++){
         iExp-=1;
         if(szhex[i] > 57)
            decimal += (tolower(szhex[i])-'W') * pow(16,(iExp));
         else
            decimal += (szhex[i]-'0') * pow(16,(iExp));
      }
   }else
      decimal = atoi(szhex);
   return decimal;
}

/****************************************************************************
   char *strlowr(char *str)
Purpopse:
   Simple methos to make a char* str to lowercase.
Parameters:
	char *str               the string to make it lowercase     
Returns:
   The string in lowercase
Notes:

****************************************************************************/
char *strlowr(char *str){
  unsigned char *p = (unsigned char *)str;
  
  while (*p) {
     *p = tolower(*p);
      p++;
  }
  return str;
}

void err_sys(const char* x){ 
   fprintf(stderr,"%s: Try again\n",x); 
   exit(-1); 
}
