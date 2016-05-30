# CITS3001 Project 2016
# Name:			Ammar Abu Shamleh
# Student number:	21521274

#	Make file for Pylos program

PROJECT = Pylos
SOURCES = $(wildcard *.java)
CLASS = $(wildcard *.class)

JC = javac

$(PROJECT) : $(CLASS)
	$(JC) $(SOURCES) -Xlint

clean:
	rm -f *.class