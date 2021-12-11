/**
 * @file Pack.java
 * @brief Implementation of enum that represents pack
 * @author Martin Marusiak
 */
package ija.ija2016.model.board;

/**
 * Enum representing pack
 */
public enum Pack {
    
        WASTE(PackType.WASTE),
        DECK(PackType.DECK),
        TARGET1(PackType.TARGET,0),
        TARGET2(PackType.TARGET,1),
        TARGET3(PackType.TARGET,2),
        TARGET4(PackType.TARGET,3),
        WORKING1(PackType.WORKING,0),
        WORKING2(PackType.WORKING,1),
        WORKING3(PackType.WORKING,2),
        WORKING4(PackType.WORKING,3),
        WORKING5(PackType.WORKING,4),
        WORKING6(PackType.WORKING,5),
        WORKING7(PackType.WORKING,6);
        
        private final PackType pack;
        private final int index;

        Pack(final PackType pack, int order){
            this.pack = pack;
            this.index = order;
        }    
        
        Pack(final PackType pack){
            this.pack = pack;
            this.index = 0;
        }     
        /**
         * Get type of pack
         * @return type of pack
         */
        final PackType getPackType(){
            return pack;
        }
        /**
         * Get index of pack
         * @return index of pacl
         */
        final int getIndex(){
            return index;
        }
        /**
         * String representation of pack
         * @return string representation of pack
         */
        @Override
        public String toString()
        {
            if(this.pack == PackType.WASTE || this.pack == PackType.DECK)
                return pack.toString();
            
            return pack.toString()+" "+Integer.toString(index+1);
        }
    }
