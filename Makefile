########################################################################
# Theme 		   : Networks
# Author           : Neil D'Souza
########################################################################

########################################################################
# MetaData
########################################################################
README=$(USER)_readme
MAIN=main.Mastermind
CLIENT=protocol.RunClient
SERVER=protocol.RunServer

########################################################################
# Paths
########################################################################
SOURCEPATH=src
DESTPATH=build/classes
JARPATH=build/jar
JAVAPATH=$(JARPATH)/log4j-1.2.jar:$(JARPATH)/junit4.jar
MAINPATH=$(SOURCEPATH)/main
CLASSPATH=-cp $(MAINPATH):$(DESTPATH):$(JAVAPATH)
STDIO=$(MAINPATH)/stdio
UTILITY=$(MAINPATH)/utility
CHANNEL=$(MAINPATH)/channel
COMMAND=$(MAINPATH)/command
PROTOCOL=$(MAINPATH)/protocol
GAME=$(MAINPATH)/game
FACTORY=$(MAINPATH)/factory
########################################################################
# Files
########################################################################
RUNNER=org.junit.runner.JUnitCore
TCLASSES=$(SOURCEPATH)/test/*.java
JCLASSES=$(STDIO)/In.java						\
		 $(STDIO)/Out.java						\
	 	 $(UTILITY)/PrettyPrinter.java			\
		 $(UTILITY)/Message.java				\
		 $(CHANNEL)/Channel.java 				\
		 $(CHANNEL)/Client.java 				\
		 $(CHANNEL)/Server.java 				\
		 $(CHANNEL)/Lobby.java					\
		 $(CHANNEL)/Stream.java					\
		 $(PROTOCOL)/Identifiable.java			\
		 $(PROTOCOL)/Protocol.java				\
		 $(PROTOCOL)/ClientProtocol.java		\
		 $(PROTOCOL)/ServerProtocol.java		\
		 $(PROTOCOL)/LobbyProtocol.java			\
		 $(PROTOCOL)/PoisonProtocol.java		\
		 $(GAME)/Game.java						\
		 $(GAME)/Agent.java						\
		 $(FACTORY)/Mastermind.java
TESTS=test.TestGame test.TestState test.TestMessage
 

########################################################################
# Commands
########################################################################
JC=javac
RM=rm -rf
SOURCE=-sourcepath $(SOURCEPATH)
DEST=-d $(DESTPATH)

compile:
	@echo “compiling ...” 
	$(JC) $(CLASSPATH) $(SOURCE) $(DEST) $(JCLASSES)

test:
	@echo “compiling tests...” 
	$(JC) $(CLASSPATH) $(SOURCE) $(DEST) $(TCLASSES)
	@echo “running tests...”
	java $(CLASSPATH) $(RUNNER) $(TESTS)

client:
	@echo “running...”
	java $(CLASSPATH) $(MAIN) CLIENT

server:
	@echo “running...”
	java $(CLASSPATH) $(MAIN) LOBBY

lobby:
	@echo “running...”
	java $(CLASSPATH) $(MAIN) LOBBY

dead:
	@echo “running...”
	java $(CLASSPATH) $(MAIN) POISON


run:
	$(MAKE) compile
	@echo “running...”
	java $(CLASSPATH) $(MAIN) SERVER


clean:
	@echo “cleaning ...”
	$(RM) $(DESTPATH)
	mkdir $(DESTPATH)

archive:
	zip $(USER)-a2 $(SOURCES) $(README) $(MAKEFILE)

