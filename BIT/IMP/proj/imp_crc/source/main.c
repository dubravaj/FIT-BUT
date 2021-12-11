#include "board.h"
#include "pin_mux.h"
#include "clock_config.h"
#include "fsl_uart.h"
//#include "fsl_device_registers.h"
//#include "fsl_debug_console.h"
#include "fsl_crc.h"

#define UART UART5
#define DEMO_UART_CLK_FREQ CLOCK_GetFreq(kCLOCK_BusClk)

uint16_t crc16_table[256];
uint32_t crc32_table[256];
/*test data for CRC, length 512 bytes*/
char crc_data[] = "uuQTW9PqtMAUbw7KgL4RI8gGheksHFFOzLzDjYX1LlbMbxu6LrdMZhv3wRaLsz69j8MGDj9Vfd2sc8uTXVYp5tX8MXXsvlOCON1seHxtNEmmxJuNfgIATswsDvXQ8f0oR3izlkaTbiPa9dOtX3HkH6J9KOTwRWzSVTjhu1FOOKobWHoiNiv0TWMhuiBre9WJCugV5cUATPUOD9mKxwcdzajuXSqgQWU2eckM3OQEbTTkoSJUinhB3eqTgosK9dIj2RAO3RbHByLsJ7Ayda08xa6MH5CFbl1Tha1LxCDk9FGkDzjxLUy47CAD4Ym0osIz9CzkvIvcHDZGN9BKCVk0ePqBEhqFmvAEQWvEXdGcWWqmVICl49SK8HJzFySEc7StTXYcAjFu9TBd7FMoIqsBYZ8s1O8foEX9zLKN83JGa9OI908jZ4N7zyYLfqf2fsz4lCD3kpdVNTd2FNbG012Q4XkfJepwFqs7dw4ifyeeUzELl3JuS0cjokB7s8pJmjMH";


/*initialize hardware CRC module, CRC 16*/
static void InitCrc16(CRC_Type *base, uint32_t seed)
{
    crc_config_t config;

    config.polynomial = 0x8005;
    config.seed = seed;
    config.reflectIn = false;
    config.reflectOut = false;
    config.complementChecksum = false;
    config.crcBits = kCrcBits16;
    config.crcResult = kCrcFinalChecksum;

    CRC_Init(base, &config);
}


/*initialize hardware CRC module, CRC 32*/
static void InitCrc32(CRC_Type *base, uint32_t seed)
{
    crc_config_t config;

    config.polynomial = 0x04C11DB7U;
    config.seed = seed;
    config.reflectIn = false;
    config.reflectOut = false;
    config.complementChecksum = false;
    config.crcBits = kCrcBits32;
    config.crcResult = kCrcFinalChecksum;

    CRC_Init(base, &config);
}

/*create table of CRC values for CRC 16, using polynom 0x8005*/
static void computeCrc16Table(){


	//polynom for crc16
	uint16_t polynom = 0x8005;

	for(int dividend = 0; dividend < 256; dividend++ ){

		uint16_t remainder = (uint16_t)dividend << 8;
		for(int i= 0; i < 8; i++){
			if(remainder & 0x8000){
				remainder <<=1;
				remainder ^= polynom;
			}
			else{
				remainder = (remainder << 1);
			}

		}
		//store value to the table
		crc16_table[dividend] = remainder;

	}

}

/*compute CRC value using CRC 16 table*/
static uint16_t computeCrc16(){

	uint16_t crc= 0;
	for(int i=0; i < sizeof(crc_data)-1; i++){
		uint8_t position = (uint8_t)((crc >> 8) ^ crc_data[i]);
		crc = ((crc << 8)^(crc16_table[position]));
	}
	return crc;

}

/*create CRC table with CRC values for CRC 32*/
static void computeCrc32Table(){


	uint32_t polynom = 0x04C11DB7;

	for(int dividend = 0; dividend < 256; dividend++ ){

		uint32_t remainder = (uint32_t)dividend << 24;
		for(int i= 0; i < 8; i++){
			if(remainder & 0x80000000){
				remainder <<=1;
				remainder ^= polynom;
			}
			else{
				remainder = (remainder << 1);
			}

		}
		//store value to the table
		crc32_table[dividend] = remainder;

	}

}

/*compute CRC value using CRC 32 table*/
static uint32_t computeCrc32(){

	uint32_t crc= 0;
	for(int i=0; i < sizeof(crc_data)-1; i++){
		uint8_t position = (uint8_t)((crc ^ (crc_data[i] << 24)) >> 24);
		crc = ((crc << 8)^(crc32_table[position]));
	}
	return crc;

}

/*compute CRC value using simple CRC 16 method*/
static uint16_t CRC16_Simple()
{
    uint16_t generator = 0x8005;	/* polynom*/
    uint16_t crc = 0; /* CRC value */

    for(int i=0; i < sizeof(crc_data)-1; i++)
    {
        crc ^= (uint16_t)(crc_data[i] << 8);

        for(int i = 0; i < 8; i++)
        {
            if (crc & 0x8000)
            {
                crc = (uint16_t)((crc << 1) ^ generator);
            }
            else
            {
                crc <<= 1;
            }
        }
    }

    return crc;
}

/*compute CRC value using simple CRC 32 method*/
static uint32_t CRC32_Simple()
{
    uint32_t generator = 0x04C11DB7; /*polynom*/
    uint32_t crc = 0; /* CRC value*/

    for(int i=0; i < sizeof(crc_data)-1; i++)
    {
        crc ^= (uint32_t)(crc_data[i] << 24);

        for(int i = 0; i < 8; i++)
        {
            if (crc & 0x80000000)
            {
                crc = (uint32_t)((crc << 1) ^ generator);
            }
            else
            {
                crc <<= 1;
            }
        }
    }

    return crc;
}


int main(void) {

/* Init board hardware. */
  BOARD_InitPins();
  BOARD_BootClockRUN();
  BOARD_InitDebugConsole();

 //messages printed to the terminal
  uint8_t crc16_ok[] = "CRC-16 HW: check successful.\r\n";
  uint8_t crc32_ok[] = "CRC-32 HW: check successful.\r\n";
  uint8_t crc16_simple_ok[] = "CRC-16 simple: check successful.\r\n";
  uint8_t crc32_simple_ok[] = "CRC-32 simple: check successful.\r\n";
  uint8_t crc16_table_ok[] = "CRC-16 table: check successful.\r\n";
  uint8_t crc32_table_ok[] = "CRC-32 table: check successful.\r\n";
  uint8_t crc_fail[] = "CRC check failed.\r\n";


  /*CRC results for test data to compare them with computed values*/
  const uint16_t crc16_checksum = 0xBFCC;
  const uint32_t crc32_checksum = 0xB2BF1565u;


  uart_config_t config;
  CRC_Type *base = CRC0;

  //CRC values from HW module
  uint16_t crc_checksum16;
  uint32_t crc_checksum32;

  /*configure UART*/
  UART_GetDefaultConfig(&config);
  config.baudRate_Bps = BOARD_DEBUG_UART_BAUDRATE;
  config.enableTx = true;
  config.enableRx = true;

  UART_Init(UART, &config, DEMO_UART_CLK_FREQ);

  /*create CRC tables for CRC-16 and CRC-32*/
  computeCrc16Table();
  computeCrc32Table();


  /* compute CRC-16 checksum*/
  InitCrc16(base, 0x0U);
  CRC_WriteData(base, (uint8_t *)crc_data, sizeof(crc_data) - 1);
  crc_checksum16 = CRC_Get16bitResult(base);

  /*check result,write message about check result to console with UART*/
  if(crc_checksum16 != crc16_checksum){

    UART_WriteBlocking(UART, crc_fail, sizeof(crc_fail)-1);
  }
  else{

	 UART_WriteBlocking(UART, crc16_ok, sizeof(crc16_ok)-1);
  }

  /*compute CRC-32 checksum*/
  InitCrc32(base, 0x0U);
  CRC_WriteData(base, (uint8_t *)crc_data, sizeof(crc_data) - 1);
  crc_checksum32 = CRC_Get32bitResult(base);

  /*check result,write message about check result to console with UART*/
  if(crc_checksum32 != crc32_checksum){
	 UART_WriteBlocking(UART, crc_fail, sizeof(crc_fail)-1);
  }
  else{
    UART_WriteBlocking(UART, crc32_ok, sizeof(crc32_ok)-1);
  }


/* Simple CRC 16 computing according to polynomial 0x8005, simple algorithm*/
 uint16_t crc16_simple_res = CRC16_Simple();
 /*check result,write message about check result to console with UART*/
 if(crc16_simple_res != crc16_checksum){
  	 UART_WriteBlocking(UART, crc_fail, sizeof(crc_fail)-1);
 }
 else{

	 UART_WriteBlocking(UART, crc16_simple_ok, sizeof(crc16_simple_ok)-1);
 }


 /* Simple CRC 32 computing according to polynomial 0x04C11DB7, simple algorithm*/
 uint32_t crc32_simple_res = CRC32_Simple();
 /*check result,write message about check result to console with UART*/
 if(crc32_simple_res != crc32_checksum){
  	 UART_WriteBlocking(UART, crc_fail, sizeof(crc_fail)-1);
 }
 else{

     UART_WriteBlocking(UART, crc32_simple_ok, sizeof(crc32_simple_ok)-1);
 }

 /*compute CRC 16 value with precomputed table*/
uint16_t crc16_table_res = computeCrc16();

/*check result,write message about check result to console with UART*/
if(crc16_table_res != crc16_checksum){
	UART_WriteBlocking(UART, crc_fail, sizeof(crc_fail)-1);
 }
 else{

	 UART_WriteBlocking(UART, crc16_table_ok, sizeof(crc16_table_ok)-1);
  }


/*compute CRC 32 value with precomputed table*/
uint32_t crc32_table_res = computeCrc32();
/*check result,write message about check result to console with UART*/
if(crc32_table_res != crc32_checksum){
	UART_WriteBlocking(UART, crc_fail, sizeof(crc_fail)-1);
 }
 else{

	 UART_WriteBlocking(UART, crc32_table_ok, sizeof(crc32_table_ok)-1);
 }



    while (1)
      {

      }

}
