Networking_Project
==================

p2p file sharing program

Features left to implement:

	* Peer should not send a request for a piece it is already waiting for

	* Timeout on wait for piece after request.  Possibly create a piece/time combo object
	  and reset the flag if time > N seconds.  Check entire array every X seconds (with a timer)

	* Need to determine when every peer has complete file then terminate, and merge pieces


Common bugs:

Forgetting to use correct IP address
If testing all processes on same computer,
have to use a different port num for each