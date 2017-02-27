from difflib import SequenceMatcher as SM

##gets each value from street into 
##its appropriate spot
def addrSplit(custInfo):
   addr ={}
   aptC="false"
   stType ={"SQ":"SQUARE","BLVD":"BOULEVARD","ST":"STREET","AVE":"AVENUE","DR":"DRIVE","RD":"ROAD","LN":"LANE","CIR":"CIRCLE"}
   direct ={"NS":"NORTHSOUTH","S":"SOUTH","W":"WEST","E":"EAST","N":"NORTH","SW":"SOUTHWEST",
            "SE":"SOUTHEAST","NE":"NORTHEAST","NW":"NORTHWEST"}
   apt = {"APT":"","NR":"","#":""}
   for keys,value in custInfo.items():
      for num,addrM in value.items():
         addr[num] = {"streetNr": "","streetName": "","streetType": "","direction": "","apt": ""}
         token = addrM["STREET"].split(' ',10)
         for j in range(0,len(token)):
            token[j] = token[j].replace(".","").replace("-","")
         addr[num]["streetNr"] = token[0]
                                                                      ##iterate through tokens ##
         for i in range(1,len(token)):   
            if(token[i] in stType or token[i] in stType.values()):       ##if its a street type##
               if(token[i] in stType.values()):
                  addr[num]["streetType"] = token[i]
               else:
                  addr[num]["streetType"] = stType[token[i]]
            elif(token[i] in direct or token[i] in direct.values()):    ###if its a direction###
               if(i < len(token)-1):
                  if(token[i+1] not in stType):
                     if(token[i] in direct.values()):
                        addr[num]["direction"] = addr[num]["direction"] + token[i]
                     else:
                        addr[num]["direction"] = addr[num]["direction"] + direct[token[i]]
                  elif(token[i-1] in stType):
                     if(token[i] in direct.values()):
                        addr[num]["direction"] = addr[num]["direction"] + token[i]
                     else:
                        addr[num]["direction"] = addr[num]["direction"] + direct[token[i]]
                  else:
                     addr[num]["streetName"] = addr[num]["streetName"] + token[i]
               else:
                  if(token[i-1] in stType):
                     if(token[i] in direct.values()):
                        addr[num]["direction"] = addr[num]["direction"] + token[i]
                     else:
                        addr[num]["direction"] = addr[num]["direction"] + direct[token[i]]
                  else:
                     addr[num]["streetName"] = addr[num]["streetName"] + token[i]
                                                                   ##if its and apartmant number##
            elif(token[i] == "APT" or token[i] == "NR" or "#" in token[i] or aptC == "true"):  
               if(i < len(token)-1):
                  aptC = "true"
                  continue
               else:
                  if(token[i][0] == '#'):
                     token[i] = token[i].replace("#","")
                     addr[num]["apt"] = token[i]
                  else:
                     addr[num]["apt"] = token[i]
                  aptC = "false"
            else:                                   ##else is a streetname###
               if(addr[num]["streetName"] == ""):
                  addr[num]["streetName"] = token[i]
               else:
                  addr[num]["streetName"] = addr[num]["streetName"] + " " + token[i]
            addrM["PSTREET"] = addr
            ##custInfo[keys] = addrM
   return  
   
##compares each value from the keys
## and prints the total number 
def compareRatio(custInfo,numOfAddrs):
   total = 0
   for keys,value in custInfo.items():
      print("%s Address  Address  Score"%(" "*10))
      for num,addrM in value.items():                   ##getting the range for the compared values
         for i in range(num+1,numOfAddrs):              ##getting the range for the comparing values          
            stValues = addrM["PSTREET"][num]
            stValues2 = addrM["PSTREET"][i]
            remValues = value[num]
            remValues2 = value[i] 
            for nkeys in stValues.keys():                     ##start comparing 
               if(stValues[nkeys] == "" and stValues2[nkeys] == ""):
                  if(nkeys == "streetNr"):
                     total += 0
                  if(nkeys == "streetType"):
                     total += 10
                  if(nkeys == "direction"):
                     total += 5
                  if(nkeys == "apt"):
                     total += 10
                  if(nkeys == "streetName"):
                     total -= 20
               elif(stValues[nkeys] == "" or stValues2[nkeys] == ""):
                  if(nkeys == "streetNr"):
                     total -= 20
                  if(nkeys == "streetType"):
                     total += 5
                  if(nkeys == "direction"):
                     total -= 5
                  if(nkeys == "apt"):
                     total -= 10
                  if(nkeys == "streetName"):
                     total -= 20
               elif(SM(None,stValues[nkeys],stValues2[nkeys]).ratio() == 1.0):
                  
                  if(nkeys == "streetNr"):
                     total += 20
                  if(nkeys == "streetType"):
                     total += 10
                  if(nkeys == "direction"):
                     total += 5
                  if(nkeys == "apt"):
                     total += 20
                  if(nkeys == "streetName"):
                     total += 20
               elif(nkeys == "apt" or nkeys == "streetName"):
                  n = SM(None,stValues[nkeys],stValues2[nkeys]).ratio()
                  if(n >= .6):
                     if(nkeys == "apt"):
                        total += n * 5
                     if(nkeys == "streetName"):
                        total += n * 10
                  else:
                     if(nkeys == "apt"):
                        total -= 20
                     if(nkeys == "streetName"):
                        total -= 5
               else:
                  if(nkeys == "streetNr"):
                     total -= 20
                  if(nkeys == "streetType"):
                     total -= 10
                  if(nkeys == "direction"):
                     total -= 10
            for nkeys in remValues.keys():
               if(nkeys == "CITY" or nkeys == "STATE" or nkeys == "ZIP"):
                  if(remValues[nkeys] == "" and remValues2[nkeys] == ""):
                     if(nkeys == "CITY"):
                        total += 10
                     if(nkeys == "STATE"):
                        total -= 0
                  elif(remValues[nkeys] == "" or remValues2[nkeys] == ""):
                     if(nkeys == "CITY"):
                        total -= 10
                     if(nkeys == "STATE"):
                        total += 0
                  elif(SM(None,remValues[nkeys],remValues2[nkeys]).ratio() == 1.0):
                     if(nkeys == "CITY"):
                        total += 20
                     if(nkeys == "STATE"):
                        total += 10
                     if(nkeys == "ZIP"):
                        if(len(remValues[nkeys]) == 5 and len(remValues2[nkeys]) == 5):
                           total += 5
                        if(len(remValues[nkeys]) == 10 and len(remValues2[nkeys]) == 10):
                           total += 80
                  elif(nkeys == "CITY" ):
                     n = SM(None,remValues[nkeys],remValues2[nkeys]).ratio()
                     if(n > 0.6):
                        total += n * 15
                     else:
                        total -= 20
                  else:
                     if(nkeys == "STATE"):
                        total -= 20
                     if(nkeys == "ZIP"):
                        if(remValues[nkeys][0:5] == remValues2[nkeys][0:5]):
                           total += 5
            if(total > 100):
               total = 100
            if(total < 0):
               total = 0
            print("%s%d        %d    %4d"%(" "*14,num,i,total))
            total = 0
   print("\n")  
   return
    
