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

	* Peer should not send a request for a piece it is already waiting for

	* Timeout on wait for piece after request.  Possibly create a piece/time combo object
	  and reset the flag if time > N seconds.  Check entire array every X seconds (with a timer)

	* Need to determine when every peer has complete file then terminate, and merge pieces


Common bugs:

Forgetting to use correct IP address
If testing all processes on same computer,
have to use a different port num for each
