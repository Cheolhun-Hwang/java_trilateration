package util_2;

public class ThreeDistanceToCenterLocation {
	public static double lat;
	public static double lon;
	public static double minLat, minLong, maxLat,maxLong;
	public static double height, width;
	
	/*
	 * # params : 
	 * bAlat, bAlong : Location of Device_A
	 * bBlat, bBlong : Location of Device_B
	 * bClat, bClong : Location of Device_C
	 * 
	 * # Function REF : https://everything2.com/title/Triangulate - "3 - Three Distances Known"
	 * (x-xn)^2 + (y-yn)^2 = rn2
	 *  A(100,100), B(160,120), and C(70,150)   ## ref : use mile.
	 *  
	 *  A: (x-100)^2 + (y-100)^2 = 50.002
	 *  B: (x-160)^2 + (y-120)^2 = 36.062
	 *  C: (x-70)^2 + (y-150)^2 = 60.832
	 *  
	 *  A: x^2 - 200x + 10000 + y^2 - 200y + 10000 = 2500
	 *  B: x^2 - 320x + 25600 + y^2 - 240y + 14400 = 1300
	 *  C: x^2 - 140x + 4900 + y^2 - 300y + 22500 = 3700
	 *  
	 *  A - B: 120x - 15600 + 40y - 4400 = 1200 
	 *  		⇒ y = -3x + 530
	 *  B - C: -180x + 20700 + 60y - 8100 = -2400 
	 *  		⇒ y = 3x - 250
	 *  
	 *  A - B = B - C
	 *  -3x + 530 = 3x - 250
	 *  780 = 6x
	 *  130 = x
	 *  
	 *  y = 3(130) - 250
	 *  y = 140
	 *  
	 *  Result : (130,140)
	 */
	public static boolean calc(double a, double b, double c, double d, double e, double f, 
			double distanceA, double distanceB, double distanceC) {
		try {
			double foundBeaconLat = 0, foundBeaconLong = 0;
			double W, Z, foundBeaconLongFilter;
			
			getMaxLocation(a, b, c, d, e, f);
			getMinLocation(a, b, c, d, e, f);
			
			height = Math.toRadians(maxLat - minLat);
			width = Math.toRadians(maxLong - minLong);
			
			System.out.println("## Traingle info");
			System.out.println("Width : " + width);
			System.out.println("Height : " + height);
			
			double bAlat = Math.toRadians(a - minLat);
			double bAlong = Math.toRadians(b - minLong);
			double bBlat = Math.toRadians(c - minLat);
			double bBlong = Math.toRadians(d - minLong);
			double bClat = Math.toRadians(e - minLat);
			double bClong = Math.toRadians(f - minLong);
			
			System.out.println("Location A Point : " + bAlat +" / " + bAlong);
			System.out.println("Location B Point : " + bBlat +" / " + bBlong);
			System.out.println("Location C Point : " + bClat +" / " + bClong);
			
			
			//W : a constant of "A-B"
			W = distanceA * distanceA - distanceB * distanceB - bAlat * bAlat - bAlong * bAlong + bBlat * bBlat + bBlong * bBlong;
			//W : a constant of "B-C"
			Z = distanceB * distanceB - distanceC * distanceC - bBlat * bBlat - bBlong * bBlong + bClat * bClat + bClong * bClong;
			
			//foundBeaconLat : x = n/m
			foundBeaconLat = (W * (bClong - bBlong) - Z * (bBlong - bAlong)) / (2 * ((bBlat - bAlat) * (bClong - bBlong) - (bClat - bBlat) * (bBlong - bAlong)));

			//foundBeaconLong : Assign to y = (A-B)
			foundBeaconLong = (W - 2 * foundBeaconLat * (bBlat - bAlat)) / (2 * (bBlong - bAlong));
			//foundBeaconLongFilter : Assign to y = (B-C)
			foundBeaconLongFilter = (Z - 2 * foundBeaconLat * (bClat - bBlat)) / (2 * (bClong - bBlong));
			
			//Average foundBeaconLong and foundBeaconLongFilter
			foundBeaconLong = (foundBeaconLong + foundBeaconLongFilter) / 2;
			
			if(foundBeaconLat != 0 || foundBeaconLong != 0) {
				ThreeDistanceToCenterLocation.lat = foundBeaconLat;
				ThreeDistanceToCenterLocation.lon = foundBeaconLong;
				
				return true;
			}else {
				return false;
			}
		}catch(Exception exception) {
			exception.printStackTrace();
			return false;
		}
	}
	
	public static void callTestMethod(double targetLat, double targetLong) {
		System.out.println("## TARGET TEST...");
		System.out.println("Location Target Point : " + Math.toRadians(targetLat - minLat) +" / " + Math.toRadians(targetLong - minLong));
	}
	
	private static void getMinLocation(double a, double b, double c, double d, double e, double f) {
		minLat = a;
		minLong = b;
		
		if(minLat > c) {
			minLat = c;
		}
		if(minLat > e) {
			minLat = e;
		}
		
		if(minLong > d) {
			minLong = d;
		}
		if(minLong > f) {
			minLong = f;
		}
	}
	
	private static void getMaxLocation(double a, double b, double c, double d, double e, double f) {
		maxLat = a;
		maxLong = b;
		
		if(maxLat < c) {
			maxLat = c;
		}
		if(maxLat < e) {
			maxLat = e;
		}
		
		if(maxLong < d) {
			maxLong = d;
		}
		if(maxLong < f) {
			maxLong = f;
		}
	}
}
