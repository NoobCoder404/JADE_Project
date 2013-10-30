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
import jade.core.AID;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

public class PersonalInfoRetrieval extends Agent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// The name of the person to find
	private String targetName;
	// The list of known information provider agents
	private AID[] InfoProviderAgents;

	// Put agent initializations here
	protected void setup() {
		// Printout a welcome message
		System.out.println("Hello! PersonFinder-agent " + getAID().getName()
				+ " is ready.");

		// Get the name of the person as a start-up argument
		Object[] args = getArguments();
		if (args != null && args.length > 0) {
			targetName = (String) args[0];
			System.out.println("Target person is " + targetName);

			// Add a TickerBehaviour that schedules a request to seller agents
			// every minute
			addBehaviour(new TickerBehaviour(this, 5000) {
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				protected void onTick() {
					System.out
							.println("Trying to retreival the information of "
									+ targetName);
					// Update the list of seller agents
					DFAgentDescription template = new DFAgentDescription();
					ServiceDescription sd = new ServiceDescription();
					sd.setType("person-finder");
					template.addServices(sd);
					try {
						DFAgentDescription[] result = DFService.search(myAgent,
								template);
						System.out
								.println("Found the following seller agents:");
						InfoProviderAgents = new AID[result.length];
						for (int i = 0; i < result.length; ++i) {
							InfoProviderAgents[i] = result[i].getName();
							System.out.println(InfoProviderAgents[i].getName());
						}
					} catch (FIPAException fe) {
						fe.printStackTrace();
					}

					// Perform the request
					myAgent.addBehaviour(new RequestPerformer());
				}
			});
		} else {
			// Make the agent terminate
			System.out.println("No target person specified currently");
			doDelete();
		}
	}

	// Put agent clean-up operations here
	protected void takeDown() {
		// Printout a dismissal message
		System.out.println("Personal information Retrieval-agent "
				+ getAID().getName() + " terminating.");
	}

	/**
	 * Inner class RequestPerformer. This is the behaviour used by agent who wants to find person
	 *  to request information provider.
	 */
	private class RequestPerformer extends Behaviour {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;
		String personalInfo; // save the information in the hash table
		private int NumOfReply = 0;

		public void action() {

			switch (step) {
			case 0:
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < InfoProviderAgents.length; ++i) {
					cfp.addReceiver(InfoProviderAgents[i]);
				}
				cfp.setContent(targetName);
				cfp.setConversationId("person-retrieval");
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique
																		// value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(
						MessageTemplate.MatchConversationId("person-retrieval"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from seller agents
				ACLMessage reply = myAgent.receive(mt);

				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// start to search person's information
						System.out.print("Information from "
								+ InfoProviderAgents);
						personalInfo = reply.getContent();
						System.out.println("info = " + personalInfo);
						System.out.println("End this message\n");

						NumOfReply = NumOfReply + 1;
					}
					repliesCnt++;

					if (repliesCnt >= InfoProviderAgents.length) {
						// We received all replies
						System.out.println("Task completed"
								+ "\nInformation about " + targetName
								+ " gathered from " + NumOfReply + " agents");
						myAgent.doDelete();
					}
				} else {
					block();

				}
				break;

			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}

	} // End of inner class RequestPerformer
}
