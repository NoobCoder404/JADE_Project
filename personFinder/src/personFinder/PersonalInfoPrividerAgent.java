/*****************************************************************
JADE - Java Agent DEvelopment Framework is a framework to develop 
multi-agent systems in compliance with the FIPA specifications.
Copyright (C) 2000 CSELT S.p.A. 

GNU Lesser General Public License

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
 *****************************************************************/

package personFinder;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.*;

public class PersonalInfoPrividerAgent extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// The list of person's information (maps the a person using name)
	private Hashtable<String,Person> personalInfo;
	// The GUI by means of which the user can add person in the personalInfo(hashtable)
	private PersonalInfoProviderGui myGui;

	// Put agent initializations here
	protected void setup() {
		// Create the hashtable to save the information
		personalInfo = new Hashtable<String,Person>();

		// Create and show the GUI 
		myGui = new PersonalInfoProviderGui(this);
		myGui.showGui();

		// Register the person-finder service in the yellow pages(DF)
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("person-finder");
		sd.setName("JADE-personalInfo-retrieval");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		// Add the behaviour serving queries from information retrieval agents
		addBehaviour(new InfoRequestsServer());

	
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Deregister from the yellow pages
		try {
			DFService.deregister(this);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Close the GUI
		myGui.dispose();
		// Printout a dismissal message
		System.out.println("Personal information provider-agent "+getAID().getName()+" terminating.");
	}

	/**
     This is invoked by the GUI when the user adds the information of a new person
    
	 */
	
	public void updateCatalogue(final String name,final String email,final String gender,final int age,final String address) {
		addBehaviour(new OneShotBehaviour() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public void action() {
				Person p = new Person();
				p.setAddress(address);
				p.setAge(age);
				p.setEmail(email);
				p.setGender(gender);
				p.setName(name);
				
				
				personalInfo.put(name, p);
				System.out.println(name+"'s information inserted into system. email = "+email);
			}
		} );
	}

	/**
	   Inner class InfoRequestsServer.
	   This is the behaviour used by information provide agents to serve incoming requests 
	   for offer from retrieval agent.
	   If the requested person is in the local hashtable the provider agent replies 
	   with a PROPOSE message along with the information. Otherwise a REFUSE message is
	   sent back.
	 */
	private class InfoRequestsServer extends CyclicBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public void action() {
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
			ACLMessage msg = myAgent.receive(mt);
			if (msg != null) {
				// CFP Message received. Process it
				String name = msg.getContent();
				ACLMessage reply = msg.createReply();

				Person p = personalInfo.get(name);
				if (p != null) {
					// The requested person's information is available. Reply with the information
					reply.setPerformative(ACLMessage.PROPOSE);
					
					reply.setContent(p.toString()); //information sent to the retrieval agent
				}
				else {
					// The requested person's information is NOT available.
					reply.setPerformative(ACLMessage.REFUSE);
					reply.setContent("not-available");
				}
				myAgent.send(reply);
			}
			else {
				block();
			}
		}
	}  // End of inner class InfoRequestsServer
}

	