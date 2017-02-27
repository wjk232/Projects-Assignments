all:cacheSimu 

cacheSimu: cacheSimu.c cacheSimufuncs.c
	cc -o cacheSimu cacheSimu.c cacheSimufuncs.c -lm

clean:
	rm -f cacheSimu *.o
