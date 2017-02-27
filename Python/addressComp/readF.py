##reads each line from stdin and
##put the corresponding data into a dictionary
## and returns the dictionary
from compare import addrSplit,compareRatio
def readFile():
   i = 0
   j = 1
   custM = {}         ##customer name
   addr = {}         ##address
   splitAddrs = {}
   while True:
      try:
         inputLine = input() # reads from stdin
         inputLine = inputLine.strip()
         token = inputLine.split(' ',1)
         ##put into dictionary
         if token[0] == "CUSTOMER":
            cust = token[1]
            custM[cust] = {}
            print("%-30sStNum  Direction  AptNum  StType   StName"%(token[1]))
         if token[0] == "ADDRBEG":
            addr[j]={}
         if token[0] == "LINE":
            if i >= 1:
               addr[j]["STREET"] = addr[j]["STREET"] + " " + token[1] 
            else:
               addr[j]["STREET"] = token[1]
            i += 1
         if token[0] == "CITY":
            addr[j]["CITY"] = token[1] 
         if token[0] == "ZIP":
            addr[j]["ZIP"] = token[1] 
         if token[0] == "STATE":
            addr[j]["STATE"] = token[1] 
         if token[0] == "ADDREND":
            addr[j]["PSTREET"] ={}
            j += 1
            i = 0
         if token[0] == "CUSTOMEREND":
            custM[cust] = addr
            addrSplit(custM)
            printFile(custM)
            compareRatio(custM,j)
            custM ={}
            addr={}
            j = 1
            i = 0
      except(EOFError):
         break   
   return
   
##prints the customer name and address   
def printFile(custInfo):
   for keys,value in custInfo.items():
      for num,addrM in value.items():
         print("%d    %s"%(num, addrM["STREET"]))
         print("     %s, %s %s\n"%(addrM["CITY"],addrM["STATE"],addrM["ZIP"]))
         stValues = addrM["PSTREET"][num]
         print("%s %-5s  %-10s %-7s %-7s  %-18s"%(" "*29,stValues["streetNr"],stValues["direction"],
               stValues["apt"],stValues["streetType"],stValues["streetName"])
               )
   print("\n")           
         
  
###cd c:\users\rogelioycecy\desktop\classes s2016\cs3723\assign6      