library IEEE;
use IEEE.std_logic_1164.all;
use IEEE.std_logic_arith.all;
use IEEE.std_logic_unsigned.all;


entity ledc8x8 is
       port(
		         SMCLK: in std_logic;
             RESET: in std_logic;
		         ROW: out std_logic_vector (0 to 7);
		         LED: out std_logic_vector (0 to 7)
		         
	);
end entity ledc8x8;


architecture main of ledc8x8 is

  signal clock_en: std_logic; 
  signal clock_count: std_logic_vector (7 downto 0);
  signal led_count: std_logic_vector (22 downto 0); 
  signal led_control: std_logic;  
  signal status_row: std_logic_vector (7 downto 0);
  signal status_led: std_logic_vector (7 downto 0);
 
  
   
begin

set_letter: process(led_control,status_row)
begin
  
      if led_control = '1' then
         case status_row is
            when "10000000" => status_led <= "11000000";
            when "01000000" => status_led <= "11111110";
            when "00100000" => status_led <= "11111110";
            when "00010000" => status_led <= "11111110";
            when "00001000" => status_led <= "11111110";
            when "00000100" => status_led <= "11111110";
            when "00000010" => status_led <= "11011101";
            when "00000001" => status_led <= "11100011";
            when others => status_led <= "11111111"; 
          end case;
      else 
          case status_row is
            when "10000000" => status_led <= "00000111";
            when "01000000" => status_led <= "01111011";
            when "00100000" => status_led <= "01111101";
            when "00010000" => status_led <= "01111101";
            when "00001000" => status_led <= "01111101";
            when "00000100" => status_led <= "01111101";
            when "00000010" => status_led <= "01111011";
            when "00000001" => status_led <= "00000111";
            when others => status_led <= "11111111";
          end case;
  end if;                      
end process set_letter;

lower_signal_gen: process(SMCLK,RESET)
begin
    if RESET = '1' then
       led_count <= "00000000000000000000000";
    elsif SMCLK'event and SMCLK = '1' then
       led_count <= led_count + 1;
        if led_count = "11100001000000000000000" then
            led_control <= not led_control; 
            led_count <= "00000000000000000000000";   
     end if;    
  end if;  
end process lower_signal_gen; 

 
clock_gen: process(SMCLK,RESET)      
begin
    if RESET = '1' then
       clock_count <= "00000000";
    elsif SMCLK'event and SMCLK = '1' then
       clock_count <= clock_count +1 ;
    end if;  
end process clock_gen;
 
clock_en <= '1' when clock_count = "11111111" else '0';
  
 
next_row: process (SMCLK,RESET)
begin
    if RESET = '1' then
        status_row <= "10000000";
    elsif SMCLK'event and SMCLK = '1' then
       if clock_en = '1' then
          status_row <= status_row(0) & status_row(7 downto 1);
       end if; 
    end if;    
end process next_row;
 
  
ROW <= status_row;
LED <= status_led;    
  

end main;
