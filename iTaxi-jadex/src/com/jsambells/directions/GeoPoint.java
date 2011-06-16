// Created by plusminus on 21:28:12 - 25.09.2008
package com.jsambells.directions;

//import org.andnav.osm.util.constants.GeoConstants;
//import org.andnav.osm.views.util.constants.MathConstants;

//import android.location.Location;

/**
 *
 * @author Nicolas Gramlich
 *
 */
public class GeoPoint {//implements MathConstants, GeoConstants{
        // ===========================================================
        // Constants
        // ===========================================================

        // ===========================================================
        // Fields
        // ===========================================================

        private int mLongitudeE6;
        private int mLatitudeE6;

        // ===========================================================
        // Constructors
        // ===========================================================

        public GeoPoint(final int aLatitudeE6, final int aLongitudeE6) {
                this.mLatitudeE6 = aLatitudeE6;
                this.mLongitudeE6 = aLongitudeE6;
        }

        public GeoPoint(final double aLatitude, final double aLongitude) {
                this.mLatitudeE6 = (int)(aLatitude * 1E6);
                this.mLongitudeE6 = (int)(aLongitude * 1E6);
        }

     /*   public GeoPoint(Location aLocation) {
                this(aLocation.getLatitude(), aLocation.getLongitude());
        }*/


        protected static GeoPoint fromDoubleString(final String s, final char spacer) {
                final int spacerPos = s.indexOf(spacer);
                return new GeoPoint((int) (Double.parseDouble(s.substring(0,
                                spacerPos - 1)) * 1E6), (int) (Double.parseDouble(s.substring(
                                spacerPos + 1, s.length())) * 1E6));
        }

        public static GeoPoint fromIntString(final String s){
                final int commaPos = s.indexOf(',');
                return new GeoPoint(Integer.parseInt(s.substring(0,commaPos-1)),
                                Integer.parseInt(s.substring(commaPos+1,s.length())));
        }

        // ===========================================================
        // Getter & Setter
        // ===========================================================

        public int getLongitudeE6() {
                return this.mLongitudeE6;
        }

        public int getLatitudeE6() {
                return this.mLatitudeE6;
        }

        public void setLongitudeE6(final int aLongitudeE6) {
                this.mLongitudeE6 = aLongitudeE6;
        }

        public void setLatitudeE6(final int aLatitudeE6) {
                this.mLatitudeE6 = aLatitudeE6;
        }

        public void setCoordsE6(final int aLatitudeE6, final int aLongitudeE6) {
                this.mLatitudeE6 = aLatitudeE6;
                this.mLongitudeE6 = aLongitudeE6;
        }

        // ===========================================================
        // Methods from SuperClass/Interfaces
        // ===========================================================

        @Override
        public String toString(){
                return new StringBuilder().append(this.mLatitudeE6).append(",").append(this.mLongitudeE6).toString();
        }

        public String toDoubleString() {
                return new StringBuilder().append(this.mLatitudeE6 / 1E6).append(",").append(this.mLongitudeE6  / 1E6).toString();
        }

        @Override
        public boolean equals(Object obj) {
                if (obj == null) return false;
                if (obj == this) return true;
                if (obj.getClass() != getClass()) return false;
                final GeoPoint rhs = (GeoPoint)obj;
                return rhs.mLatitudeE6 == this.mLatitudeE6 && rhs.mLongitudeE6 == this.mLongitudeE6;
        }
}

