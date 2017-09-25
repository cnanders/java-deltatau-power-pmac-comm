

public final class TestDeltaTauComm {
	
	public static void main(String[] args){
	    
		DeltaTauComm deltaTauComm = new DeltaTauComm("192.168.20.23");
		
	    
	    //deltaTauComm.gpasciiCommand("NewWorkingMode=wm_RUN");
	    System.out.println(deltaTauComm.gpasciiQuery("ActWorkingMode"));
	    
	    System.out.println(deltaTauComm.gpasciiQuery("RepCS1X"));
	    System.out.println(deltaTauComm.gpasciiQuery("RepCS1Y"));
	    System.out.println(deltaTauComm.gpasciiQuery("RepCS2X"));
	    System.out.println(deltaTauComm.gpasciiQuery("RepCS2Y"));
	    	    
	    deltaTauComm.close();
	}
}
