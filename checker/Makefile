CC = g++
CFLAGS = -Wall -g

all: build

build: sync

sync: main.o 
	$(CC) $(CFLAGS) -o $@ $^

.PHONY: clean

run: build
	./sync $(problem)

clean:
	rm -f *.o *~ sync 