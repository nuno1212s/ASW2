package rsa.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import rsa.quad.PointQuadtree;
import rsa.quad.Trie;
import rsa.shared.Location;
import rsa.shared.RideMatchInfo;
import rsa.shared.RideRole;
import rsa.shared.UserStars;

public class Matcher implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 365838490212732987L;

	private static Location topLeft = new Location(10, 20), bottomRight = new Location(20, 10);

    private static double radius;
    
	private static final Random random = new Random();

    private Map<Long, Ride> rides;

    private Map<Long, RideMatch> matches;

    private PointQuadtree<Ride> quadTree;

    public Matcher() {

    	Trie.setCapacity(10);
    	
        rides = new HashMap<>();
        matches = new HashMap<>();
     
        makeQuad();
    }
    
    private void makeQuad() {

        quadTree = new PointQuadtree<>(topLeft.getX(), topLeft.getY(), bottomRight.getX(), bottomRight.getY());

    }

    private Ride getRideWithId(long rideId) {
        return rides.get(rideId);
    }

    private RideMatch getRideMatchWithId(long matchId) {
    	return matches.get(matchId);
    }
    
    public void acceptMatch(long rideId, long matchId) {
    	
    	RideMatch match = getRideMatchWithId(matchId);
    	
    	match.setAccepted(rideId, true);
    	
    	if (match.allAccepted()) {
    		
    		for (RideRole role : RideRole.values()) {
    			
    			match.getRide(role).setMatch(match);
    			
    		}
    		
    		matches.remove(match.getId());
    	}
    	
    }
    
    public long addRide(User user, Location from, Location to, String plate, float cost) {

        Ride r = new Ride(user, from, to, plate, cost);
        
        if (getRideWithId(r.getId()) != null) {
        	return addRide(user, from, to, plate, cost);
        }

        quadTree.insert(r);

        rides.put(r.getId(), r);

        return r.getId();

    }

    public void concludeRide(long rideId, UserStars stars) {

        Ride rideWithId = getRideWithId(rideId);

        rideWithId.getUser().addStars(stars, rideWithId.getRideRole());

        rides.remove(rideId);
    }

    public static Location getBottomRight() {
        return bottomRight;
    }

    public static double getRadius() {
        return radius;
    }

    public static void setRadius(double radius) {
        Matcher.radius = radius;
    }

    public static Location getTopLeft() {
        return topLeft;
    }

    public static void setTopLeft(Location topLeft) {
        Matcher.topLeft = topLeft;
        
    }

    public static void setBottomRight(Location bottomRight) {
        Matcher.bottomRight = bottomRight;
        
    }

    SortedSet<RideMatchInfo> updateRide(long rideId, Location current) {

        Ride r = getRideWithId(rideId);
        
        System.out.println(r.getUser().getNick());

        quadTree.delete(r);

        r.setCurrent(current);

        quadTree.insert(r);

        if (!r.isMatched()) {

            SortedSet<RideMatchInfo> rides = new TreeSet<>(r.getComparator());

            Set<Ride> near = quadTree.findNear(r.getCurrent().getX(), r.getCurrent().getY(), Matcher.getRadius());
            
            System.out.println(near);
            
            near = near.stream()
                    .filter(r1 -> {
                    	 
                    	System.out.println(r1.getId());
                    	System.out.println(r1.getRideRole().other().equals(r.getRideRole()));
                    	System.out.println(r1.getTo().distance(r.getTo()) <= Matcher.getRadius());
                    	System.out.println(r1.getMatch() == null);
                    	
                    	return 
                    	r1.getRideRole().other().equals(r.getRideRole())
                            && r1.getTo().distance(r.getTo()) <= Matcher.getRadius()
                            && r1.getMatch() == null;})
                    .collect(Collectors.toSet());
            
            System.out.println(near);
            
            int i = 0;

            forx: for (Ride ride : near) {
            	
            	//Search through the current existing matches to see if there is already a match between the 2
            	for (RideMatch match : this.matches.values()) {
            		
            		if (match.getRide(r.getRideRole()).getId() == r.getId()
            				&& match.getRide(r.getRideRole().other()).getId() == ride.getId()) {
            			
            			RideMatchInfo f = new RideMatchInfo(match);

        				System.out.println(f.getMatchId());
        				
            			if (!rides.add(new RideMatchInfo(match))) {
            				System.out.println("ERRO1");
            			}
            			i++;
            			
            			continue forx;
            		}
            		
            	}
            	
            	RideMatch newMatch = new RideMatch(r, ride);
            	
            	if (matches.containsKey(newMatch.getId())) {
            		System.out.println("LOL");
            	}
            	
            	matches.put(newMatch.getId(), newMatch);
            	
            	RideMatchInfo f = new RideMatchInfo(newMatch);
            	
        		System.out.println(f.getMatchId());
            	
            	if (!rides.add(f)) {
            		System.out.println("ERRO");
            	}
            
            	
            }
            
            System.out.println(rides);
            System.out.println(i);

            return rides;

        }

        return null;
    }

    public class RideMatch {
    	
        long id;

        Map<RideRole, Ride> rides;
        
        Map<Long, Boolean> accepted;

        public RideMatch(Ride left, Ride right) {

            id = random.nextLong();

            rides = new HashMap<>();

            rides.put(left.getRideRole(), left);
            rides.put(right.getRideRole(), right);
            
            this.accepted = new HashMap<>();
            
            this.accepted.put(left.getId(), false);
            this.accepted.put(right.getId(), false);

        }
        
        void setAccepted(long rideId, boolean accepted) {
        	this.accepted.put(rideId, accepted);
        }
        
        boolean allAccepted() {
        	
        	boolean valid = true;
        	
        	for (boolean cur : this.accepted.values()) {
        		valid &= cur;
        	}
        	
        	return valid;
        	
        }

        public long getId() {
            return id;
        }

        public Ride getRide(RideRole role) {
            return rides.get(role);
        }

        public boolean matchable() {

            boolean hasDriver = rides.get(RideRole.DRIVER) != null, hasPassenger = rides.get(RideRole.PASSENGER) != null;

            if (hasDriver && hasPassenger) {
            
            if (!(getRide(RideRole.DRIVER).getMatch() == null && getRide(RideRole.PASSENGER).getMatch() == null)) {
                return false;
            }

            if (!(getRide(RideRole.DRIVER).getFrom().distance(getRide(RideRole.PASSENGER).getFrom()) <= Matcher.getRadius())) {
                return false;
            }

            if (!(getRide(RideRole.DRIVER).getTo().distance(getRide(RideRole.PASSENGER).getTo()) <= Matcher.getRadius())) {
                return false;
            }
            
            return true;
            }

            return false;
        }

    }

}
