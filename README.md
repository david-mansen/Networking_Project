Networking_Project
==================

p2p file sharing program

FOR TESTING:

	Note: If piece/merged files seem to have different data than TheFile.dat,
	check the encoding of TheFile.dat.  If saved with encoding ansi,
	will have 1 byte for each character, and the file size will match
	the number of characters.  
	
	ssh into storm/thunder, then ssh from these bastion servers into 
	an individual machine (e.g. ssh lin114-00.cise.ufl.edu)  
	
	If testing a large file, use the cise/tmp/ folder.  Make sure you restrict access
	to only yourself so others cannot plagiarize our code.  if ssh'd into an individual
	machine, this folder is hidden but can still be accessed. 


Features left to implement:

	* Peer should not send pieces to a peer it has choked.  This could happen if it chokes after the peer
	  had already sent a request

	* Send a 'not interested' message when receiving a piece to any swarmpeer that doesnt have any desired                   pieces

	* Upon receiving a 'have' message, Send an 'interested' message to the peer that sent the have message 
	  if it has a new piece that is desired. 


Common bugs:

Forgetting to use correct IP address
If testing all processes on same computer,
have to use a different port num for each
