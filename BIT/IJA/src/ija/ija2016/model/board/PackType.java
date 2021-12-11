/**
 * @file PackType.java
 * @brief Implementation of enum that represents type of pack
 * @author Martin Marusiak
 */
package ija.ija2016.model.board;
/**
 * Enum representing type of pack
 */
public enum PackType {
    
        WASTE("waste"),
        DECK("deck"),
        TARGET("target pack"),
        WORKING("working stack");

        private final String pack;

        PackType(final String pack){
            this.pack = pack;
        }     
        /**
         * Value of enum
         * @return enum constant
         */
        @Override
        public String toString()
        {
            return pack;
        }
    }
