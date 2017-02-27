all: assign5

assign5: main.c k_means.c
	mpicc -o k_means main.c k_means.c -lm
clean: 
	rm -f k_means *.o
