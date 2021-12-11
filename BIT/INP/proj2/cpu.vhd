-- cpu.vhd: Simple 8-bit CPU (BrainLove interpreter)
-- Copyright (C) 2016 Brno University of Technology,
--                    Faculty of Information Technology
-- Author(s): Juraj Ondrej Dúbrava
--

library ieee;
use ieee.std_logic_1164.all;
use ieee.std_logic_arith.all;
use ieee.std_logic_unsigned.all;

-- ----------------------------------------------------------------------------
--                        Entity declaration
-- ----------------------------------------------------------------------------
entity cpu is
 port (
   CLK   : in std_logic;  -- hodinovy signal
   RESET : in std_logic;  -- asynchronni reset procesoru
   EN    : in std_logic;  -- povoleni cinnosti procesoru
 
   -- synchronni pamet ROM
   CODE_ADDR : out std_logic_vector(11 downto 0); -- adresa do pameti
   CODE_DATA : in std_logic_vector(7 downto 0);   -- CODE_DATA <- rom[CODE_ADDR] pokud CODE_EN='1'
   CODE_EN   : out std_logic;                     -- povoleni cinnosti
   
   -- synchronni pamet RAM
   DATA_ADDR  : out std_logic_vector(9 downto 0); -- adresa do pameti
   DATA_WDATA : out std_logic_vector(7 downto 0); -- mem[DATA_ADDR] <- DATA_WDATA pokud DATA_EN='1'
   DATA_RDATA : in std_logic_vector(7 downto 0);  -- DATA_RDATA <- ram[DATA_ADDR] pokud DATA_EN='1'
   DATA_RDWR  : out std_logic;                    -- cteni (1) / zapis (0)
   DATA_EN    : out std_logic;                    -- povoleni cinnosti
   
   -- vstupni port
   IN_DATA   : in std_logic_vector(7 downto 0);   -- IN_DATA <- stav klavesnice pokud IN_VLD='1' a IN_REQ='1'
   IN_VLD    : in std_logic;                      -- data platna
   IN_REQ    : out std_logic;                     -- pozadavek na vstup data
   
   -- vystupni port
   OUT_DATA : out  std_logic_vector(7 downto 0);  -- zapisovana data
   OUT_BUSY : in std_logic;                       -- LCD je zaneprazdnen (1), nelze zapisovat
   OUT_WE   : out std_logic                       -- LCD <- OUT_DATA pokud OUT_WE='1' a OUT_BUSY='0'
 );
end cpu;


-- ----------------------------------------------------------------------------
--                      Architecture declaration
-- ----------------------------------------------------------------------------
architecture behavioral of cpu is

 -- zde dopiste potrebne deklarace signalu

 -- PC
 signal pc_code_address : std_logic_vector(11 downto 0); -- adresa do pamati programu
 signal pc_reg_add : std_logic; -- zvysenie hodnoty pc registru
 signal pc_reg_sub : std_logic;  -- znizenie hodnoty pc registu 
 -- PTR
signal ptr_data_address : std_logic_vector(9 downto 0);   -- adresa do pamati dat
signal ptr_add : std_logic; -- zvysenie hodnoty ptr registru
signal ptr_sub : std_logic; -- znizenie hodnoty ptr registru
 
-- CNT 
signal cnt_counter : std_logic_vector(7 downto 0); -- hodnota na pocitanie zatvoriek pri while
signal cnt_add : std_logic; -- zvysenie poctu 
signal cnt_sub : std_logic; -- znizenie poctu
signal set_cnt : std_logic;
-- TMP
signal tmp_value : std_logic_vector(7 downto 0); -- pomocna premenna
signal tmp_val : std_logic_vector(7 downto 0 );
signal tmp_load : std_logic; 

-- MX_OUT
signal sel : std_logic_vector(1 downto 0); -- 2 bity selectu pre 4 vstupovy MX

-- BrainLove instrukcie 
type bl_instruction is (
pc_inc, pc_dec, -- praca s PC registrom
ptr_inc,ptr_dec, -- praca s PTR registrom
begin_while,end_while, -- while cyklus
put_char,get_n_store, -- putchar a *ptr = getchar()
tmp_store, tmp_write, -- ulozenie do tmp a zapis z tmp
halt, -- return
ignore -- preskoc
);
signal bl_inst : bl_instruction; 


-- FSM
type fsm_state is (sidle,sfetch0,sdecode,pc_inc,pc_dec,ptr_inc,ptr_inc2,
ptr_dec,ptr_dec2,
begin_while,swhile1,swhile2,swhile3,swhile4,
end_while,e_while1,e_while2,e_while3,e_while4,e_while5,e_while6,
put_char,put_char2,put_char3,get_n_store,get_n_store2,tmp_store,tmp_store2,tmp_write,  
shalt, ignore); -- postupne doplnit 

signal pstate : fsm_state; -- present state
signal nstate : fsm_state; -- next state


begin

 -- zde dopiste vlastni VHDL kod
 -- PC register
pc_register : process(CLK,RESET,pc_code_address)
begin
    if(RESET = '1') then
        pc_code_address <= (others => '0');
    elsif (CLK'event) and (CLK = '1') then
        if(pc_reg_add = '1') then
            pc_code_address <= pc_code_address + 1;
        elsif (pc_reg_sub = '1') then
            pc_code_address <= pc_code_address - 1;
        end if;
     end if;
    CODE_ADDR <= pc_code_address;
end process; 

-- PTR register
ptr_register : process(CLK,RESET,ptr_data_address)
begin
    if(RESET = '1') then
        ptr_data_address <= (others => '0');
    elsif (CLK'event) and (CLK = '1') then
        if(ptr_add = '1') then
            ptr_data_address <= ptr_data_address + 1;
        elsif (ptr_sub = '1') then
           ptr_data_address <= ptr_data_address - 1;
        end if;
     end if;
    DATA_ADDR <= ptr_data_address;
end process; 

-- CNT register
cnt_register : process(CLK,RESET,set_cnt,cnt_counter)
begin
    if(RESET = '1') then
        cnt_counter <= (others => '0');
    elsif (CLK'event) and (CLK = '1') then
        if(cnt_add = '1') then
            cnt_counter <= cnt_counter + 1;
        elsif (cnt_sub = '1') then
           cnt_counter <= cnt_counter - 1;
        end if;
     end if;
     if(set_cnt = '1') then
        cnt_counter <= "00000001";
     end if;   
end process; 

-- treba overit ci je to dobre
-- TMP register
tmp_register : process(CLK,RESET,tmp_load,tmp_value)
begin
    if(RESET = '1') then
        tmp_value <= (others => '0');
    elsif (CLK'event) and (CLK = '1') then
        if(tmp_load = '1') then
            tmp_value <=  DATA_RDATA;
        end if;
     end if;
     tmp_val <= tmp_value;
end process; 

-- MX riadiaci zapisovanu hodnotu do pamati RAM
mx_out : process (DATA_RDATA,IN_DATA,sel,CLK,tmp_val)
begin
    case sel is
        when "00" => DATA_WDATA <= IN_DATA;
        when "01" => DATA_WDATA <= tmp_val;
        when "10" => DATA_WDATA <= DATA_RDATA - 1;
        when "11" => DATA_WDATA <= DATA_RDATA + 1;
        when others =>
     end case;
end process;

--decoder instrukcii
bl_decoder : process (CODE_DATA)
begin
    case CODE_DATA is
        when X"3E"  => bl_inst <= pc_inc; -- >
        when X"3C"  => bl_inst <= pc_dec; -- <
        when X"2B"  => bl_inst <= ptr_inc; -- +
        when X"2D"  => bl_inst <= ptr_dec; -- -
        when X"5B"  => bl_inst <= begin_while; -- [
        when X"5D"  => bl_inst <= end_while; -- ]
        when X"2E"  => bl_inst <= put_char; -- .
        when X"2C"  => bl_inst <= get_n_store; -- ,
        when X"24"  => bl_inst <= tmp_store; -- $
        when X"21"  => bl_inst <= tmp_write; -- !
        when X"00"  => bl_inst <= halt; -- null
        when others => bl_inst <= ignore; -- preskoc 
      end case;
end process;


-- register aktualneho stavu
fsm_pstate : process(CLK, RESET)
begin
    if(RESET = '1') then
        pstate <= sidle;
    elsif (CLK'event) and (CLK = '1') then
        if(EN = '1') then
            pstate <= nstate;
        end if;
    end if;
end process;

--vystup na LCD
OUT_DATA <= DATA_RDATA;
 

-- FSM logika nasledujuceho stavu
nsl : process (pstate,bl_inst,IN_VLD,OUT_BUSY,sel,cnt_counter,DATA_RDATA,CODE_DATA)
begin

-- pociatocne nastavenie signalov
CODE_EN <= '0';
DATA_EN <= '0';
DATA_RDWR <= '0';
IN_REQ <= '0'; 
OUT_WE <= '0'; 
pc_reg_add <= '0';
pc_reg_sub <= '0';
ptr_add <= '0';
ptr_sub <= '0';
cnt_add <= '0';
cnt_sub <= '0';
tmp_load <= '0';
sel <= "00";
set_cnt <= '0';
case pstate is
-- IDLE
when sidle => 
    nstate <= sfetch0;

when sfetch0 =>
    nstate <= sdecode;
    CODE_EN <= '1';   

when sdecode =>
    case bl_inst is
        when pc_inc =>
            nstate <= pc_inc;
        when pc_dec =>
            nstate <= pc_dec;
        when ptr_inc =>
            nstate <= ptr_inc;
        when ptr_dec =>
            nstate <= ptr_dec;
        when begin_while =>
            nstate <= begin_while;
        when end_while =>
            nstate <= end_while;
        when put_char =>
            nstate <= put_char;
        when get_n_store =>
            nstate <= get_n_store;
        when tmp_store =>
            nstate <= tmp_store;
        when tmp_write =>
            nstate <= tmp_write;
        when halt =>
            nstate <= shalt;
        when ignore =>
            nstate <= ignore;
     end case;
-- >    
when pc_inc =>
     nstate <= sfetch0;
     ptr_add <= '1'; -- ptr = ptr + 1
     pc_reg_add <= '1'; -- pc = pc + 1
   

-- <
when pc_dec =>
     nstate <= sfetch0;
     ptr_sub <= '1';  -- ptr = ptr - 1
     pc_reg_add <= '1'; -- pc = pc + 1   

-- + start        
when ptr_inc => 
    nstate <= ptr_inc2;
    DATA_EN <= '1'; -- povolenie citania
    DATA_RDWR <= '1'; -- citanie
    sel <= "11";
  
-- koniec faze instrukcie +    
when ptr_inc2 =>
    nstate <= sfetch0;
    DATA_EN <= '1';
    DATA_RDWR <= '0'; -- zapis
    sel <= "11";  -- zapis hodnotu zvacsenu o 1    
    pc_reg_add <= '1';
-- -
when ptr_dec =>
    nstate <= ptr_dec2;
    DATA_EN <= '1';
    DATA_RDWR <= '1';
    
   
-- koniec faze - 
when ptr_dec2 =>
    nstate <= sfetch0;
    DATA_EN <= '1';
    DATA_RDWR <= '0'; -- zapis
    sel <= "10";
    pc_reg_add <= '1';
    
-- WHILE        
--when begin_while =>
--   nstate <= swhile1;
--   pc_reg_add <= '1';
--   DATA_EN <= '1'; 
--   DATA_RDWR <= '1'; -- nacitaj data z ram[PTR]

-- jednoduchy while 
--when swhile1 =>
--    if(DATA_RDATA = "00000000") then 
--        nstate <= swhile2;
--    else
--        nstate <= sfetch0; 
--    end if;    

--when swhile2 =>
--    nstate <= swhile3;
--    CODE_EN <= '1';
--    pc_reg_add <= '1';

--when swhile3 =>
--    if(CODE_DATA = X"5D") then
--        nstate <= sfetch0;
--    else
--        nstate <= swhile2;    
--    end if;                        

--when end_while => 
--    nstate <= e_while1;
--    DATA_EN <= '1'; 
--    DATA_RDWR <= '1'; -- nacitaj data z ram[PTR]

-- koniec jednoducheho whilu 
--when e_while1 =>
--    if(DATA_RDATA = "00000000") then
--      pc_reg_add <= '1';
 --     nstate <= sfetch0;
--    else 
--      nstate <= e_while2;   
--    end if;
    
--when e_while2 =>
--    nstate <= e_while3;
--    pc_reg_sub <= '1';
--    CODE_EN <= '1';
    
--when e_while3 =>
--     if(CODE_DATA = X"5B") then
--       nstate <= sfetch0;
--       pc_reg_add <= '1';       
--     else
--        nstate <= e_while2;
--     end if
                
-- WHILE        
when begin_while =>
   nstate <= swhile1;
   pc_reg_add <= '1';  -- pc = pc + 1
   DATA_EN <= '1'; 
   DATA_RDWR <= '1'; -- nacitaj data z ram[PTR]

when swhile1 =>
     -- if(ram[PTR] == 0)
    if(DATA_RDATA = "00000000") then
      set_cnt <= '1';
      nstate <= swhile2;
    else
      nstate <= sfetch0; -- podmienka neplati 
    end if; 

when swhile2 =>
   if(cnt_counter = "00000000" ) then 
       nstate <= sfetch0;    -- CNT == 0
   else
       nstate <= swhile3;    -- CNT != 0
   end if;    

when swhile3 =>
    nstate <= swhile4;
    CODE_EN <= '1';   -- c <- rom[PC]
    
    
when swhile4 =>     
    nstate <= swhile2;  --spat na zaciatok while cyklu
    if(CODE_DATA = X"5B") then
      cnt_add <= '1';        -- c == [
    elsif(CODE_DATA = X"5D") then
      cnt_sub <= '1';        -- c == ]    
    end if; 
    pc_reg_add <= '1';
   
when end_while => 
    nstate <= e_while1;
    DATA_EN <= '1'; 
    DATA_RDWR <= '1'; -- nacitaj data z ram[PTR]

when e_while1 =>
    
    if(DATA_RDATA = "00000000") then
      nstate <= sfetch0;
      pc_reg_add <= '1';
    else
      nstate <= e_while2;   
    end if;

when e_while2 =>
    nstate <= e_while3;
    set_cnt <= '1';
    pc_reg_sub <= '1';

when e_while3 =>
    if(cnt_counter = "00000000" ) then 
       nstate <= sfetch0;    -- CNT == 0
    else
       nstate <= e_while4;    -- CNT != 0
    end if;           
      
when e_while4 =>
     nstate <= e_while5;
     CODE_EN <= '1';   -- c <- rom[PC]     

when e_while5 =>
    nstate <= e_while6;
    if(CODE_DATA = X"5D") then
      cnt_add <= '1';        -- c == [
    elsif(CODE_DATA = X"5B") then
      cnt_sub <= '1';        -- c == ]    
    end if;
    
when e_while6 =>
    nstate <= e_while3;
   if(cnt_counter = "00000000") then
      pc_reg_add <= '1';   
   else
      pc_reg_sub <= '1';
   end if;
        
-- putchar 
when put_char =>
    if(OUT_BUSY = '0') then
        nstate <= put_char2;
     else 
        nstate <= put_char;   
    end if;
    
-- koniec putchar    
when put_char2 => 
        nstate <= put_char3;  
        DATA_EN <= '1';
        DATA_RDWR <= '1'; -- citanie z ram
when put_char3 =>
        nstate <= sfetch0;    
        OUT_WE <= '1';
        pc_reg_add <= '1'; -- pc = pc + 1

-- input data            
when get_n_store =>
    IN_REQ <= '1';
    if(IN_VLD = '1') then
        nstate <= get_n_store2;
    else
        nstate <= get_n_store;
    end if;
-- koniec input data    
when get_n_store2 =>    
       nstate <= sfetch0;
       DATA_EN <= '1';
       DATA_RDWR <= '0';
       sel <= "00";
       pc_reg_add <= '1'; -- pc = pc + 1
-- uloz hodnotu do tmp       
when tmp_store =>
    nstate <= tmp_store2;
    DATA_EN <= '1';
    DATA_RDWR <= '1';
-- koniec fazy ukladania
when tmp_store2 =>
    nstate <= sfetch0;    
    tmp_load <= '1';
    pc_reg_add <= '1'; -- pc = pc + 1
    pc_reg_sub <= '0';
-- zapis hodnotu z tmp        
when tmp_write =>
    nstate <= sfetch0;
    DATA_EN <= '1';
    sel <= "01";
    pc_reg_add <= '1'; -- pc = pc + 1
   
-- return         
when shalt => 
    nstate <= shalt;

when ignore =>
    nstate <= sfetch0;
    pc_reg_add <= '1' ;
      
end case;

end process;

end behavioral;
 
