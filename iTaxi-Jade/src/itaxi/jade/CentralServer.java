package itaxi.jade;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

import itaxi.messages.entities.Party;
import itaxi.messages.entities.Vehicle;

import itaxi.jade.behaviour.centralserver.GetCallBehaviour;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;

import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import com.google.gson.Gson;

public class CentralServer extends Agent {

	private static final long serialVersionUID = 1L;
	
	private Gson _gson = new Gson();

	private Map<String, Party> _pendingBookings = new TreeMap<String, Party>();
	private Map<String, Party> _assignedBookings = new TreeMap<String, Party>();
	
	private Map<String, Vehicle> _availableTaxis = new TreeMap<String, Vehicle>();

	protected void setup() {
		System.out.println(getLocalName() + ": initializing...");
		registerAgent();

		addBehaviour(new GetCallBehaviour(this,1000));
	}

	/**
	 *  Register at the yellow pages   
	 */
	private void registerAgent() {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());

		ServiceDescription sd = new ServiceDescription();
		sd.setType(Services.CENTRAL_SERVER.toString());
		sd.setName(getLocalName());
		dfd.addServices(sd);

		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	public Collection<Party> getPendingBookings() {
		return _pendingBookings.values();
	}
	
	public void addPendingBooking(Party p) {
		_pendingBookings.put(p.getName(), p);
	}
	
	public void removePendingBooking(Party p) {
		_pendingBookings.remove(p.getName());
	}
	
	public Collection<Party> getAssignedBookings() {
		return _assignedBookings.values();
	}
	
	public void addAssignedBooking(Party p) {
		_assignedBookings.put(p.getName(), p);
	}
	
	public void removeAssignedBooking(Party p) {
		_assignedBookings.remove(p.getName());
	}
	
	public Collection<Vehicle> getAvailableTaxis() {
		return _availableTaxis.values();
	}
	
	public void addAvailableTaxi(Vehicle v) {
		_availableTaxis.put(v.getVehicleID(), v);
	}
	
	public void removeAvailableTaxi(Vehicle v) {
		_availableTaxis.remove(v.getVehicleID());
	}
	
	public void removeAllAvailableTaxis() {
		_availableTaxis.clear();
	}

	/**
	 * Unregister from the yellow pages 
	 */
	protected void takeDown() { 
		try {
			DFService.deregister(this); 
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}
		// Close the GUI
		//myGui.dispose();
		// Printout a dismissal message
		System.out.println(getLocalName() + ": terminating.");
	}

	public AID[] getTaxis() {

		DFAgentDescription dfd = new DFAgentDescription();

		ServiceDescription sd = new ServiceDescription();
		sd.setType(Services.TAXI.toString());

		dfd.addServices(sd);

		AID[] taxis = null;

		try {
			DFAgentDescription[] result = DFService.search(this, dfd);

			if(result.length == 0)
				return null;

			taxis = new AID[result.length];

			for (int i = 0; i < result.length; i++) {
				taxis[i] = result[i].getName();
			}
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}	
		return taxis;
	}

	class ListTaxisBehaviour extends CyclicBehaviour {
		public void action() {
			AID[] taxis = getTaxis();

			System.out.println("Registered taxis:");
			for(AID taxi : taxis) {
				System.out.println("\t" + taxi.getName());
			}
		}
	}
}
