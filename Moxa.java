/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package moxa.io;

import com.sun.jna.*;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.DoubleByReference;


/**
 *
 * @author tstrawbridge
 */
public class Moxa {
    
    public interface MOXADLL extends Library{
        
        /** Native access to JNA Conversion
         * Native Type  |   Size        |  Java Type   | Common Windows Types  |
         * char         |8 bit int      |byte          | BYTE, TCHAR          
         * short        |16-bit int     |short         | WORD 
         * wchar_t      |16/32 bit char |char          | TCHAR
         * int          |32 bit int     |int           | DWORD
         * int          |boolean        |boolean       | BOOL
         * long         |32/64 bit int  |NativeLong    | LONG
         * long long    |64 bit int     |long          | _int64
         * float        |32 bit FP      |float         |
         * double       | 64 bit FP     |double        |    
         * char*        |C string       |String        | LPTCSTR    
         * void*        |pointer        |Pointer       | LPVOID, HANDLE, LPXXX
         * 
         * 
         * 
         * 
         * 
         * 
         */    
        
        /**
         * this process uses the moxa 64-bit driver that should be located on the PC
         * the mssql server driver should also be installed on the pc in c:\Windows\System32
         
        **/


        // initiate the socket
        public int MXEIO_Init();        // return values MXIO_OK on sucess, or error code
        // terminate the socket
        public void MXEIO_Exit();
        
        /**
         * General Commands for access to DLL
         */
        
        public int MXIO_GetDllVersion();
        public int MXIO_GetDllBuildDate();
        public int MXIO_GetModuleType(int connection, byte slot, IntByReference value);
        public int MXIO_ReadFirmwareRevision(int connection, byte[] revision);
        public int MXIO_ReadFirmwareDate(int connection, short revision);
        
        public int MXIO_Restart(int connection);
        public int MXIO_Reset(int connection);
        
        // connect to device, device handle will be returned
        public int MXEIO_Disconnect(int connection);
        public int MXEIO_CheckConnection(int connection, int timeout, ByteByReference value);
        
        // c library is char *, WORD, DWORD, int *, char *
        // by value, by value, by value, by ref, by value
        
        public int MXEIO_E1K_Connect(byte[] IP, short port, int timeout, IntByReference connection, byte[] password);
        
        public int E1K_DI_Reads(int connection, byte channel, byte channelCount, IntByReference value);
        public int E1K_DO_Reads(int connection, byte channel, byte channelCount, IntByReference value);
        public int E1K_DO_Writes(int connection, byte channel, byte channelCount, int value);
        
        // analog reads
        public int E1K_AI_Reads(int connection, byte channel, byte channelCount, DoubleByReference returnValue);
        
        
        
        public int DI_Read(int connection, byte slot, byte channel, IntByReference value);                                          // does not work
        public int DI_Reads(int connection, byte slot, byte startChannel, byte count, ByteByReference value);
        
        public int DO_Read(int connection, byte slot, byte channel, IntByReference value);
        public int DO_Write(int connection, byte slot, byte channel, byte value);  
        
        
        /**
         * Special Digital Output Commands for ioLogik 1200
         * 
         * E1K_DO_SetModes
         * E1K_DO_GetModes
         * 
         * 
         */
        
        
        // pulse output writes
        public int E1K_Pulse_SetStartStatuses(int connection, byte channel, byte channelCount, int value);                          // turns on/off the pulse output
        public int E1K_Pulse_SetSignalWidths(int connection, byte channel, byte channelCount, int onWidth, int offWidth);           // determines the pulse widths of output
        public int E1K_DO_SetModes(int connection, byte channel, byte channelCount, int value);
        
    }
    
    final IntByReference connRef = new IntByReference();
    //final DoubleByReference dblRef = new DoubleByReference();
    private int retCode;
    
    private  MOXADLL lib;
    private int ihConnection;
    private String RetString = "";                              // return string text
    final int TIMEOUT = 3000;                           // timeout setting for device
    private short port;
    
    
    
    
    public Moxa(){
        // -- load library from the java class path
        // lib = (MOXADLL) Native.loadLibrary("drivers/MXIO.dll", MOXADLL.class);                    // doesn't load if running outside of IDE.... need to load from class path
        lib = (MOXADLL) Native.loadLibrary("C:\\Windows\\System32\\MXIO.dll", MOXADLL.class);
        
        
    }
    
    public String CheckErr(int iRet){
        
        String RetMsg = "";
        // define error codes
        
            switch (iRet){
                case 0:
                    RetMsg = "MXIO_OK";
                    break;
                case 1001:
                    RetMsg = "ILLEGAL_FUNCTION";
                    break;
                case 1002:
                    RetMsg = "ILLEGAL_DATA_ADDRESS";
                    break;
                case 1003:
                    RetMsg = "ILLEGAL_DATA_VALUE";
                    break;
                case 2001:
                    RetMsg = "EIO_TIME_OUT";
                    break;
                    
            }
                
        return RetMsg;
    }
    
    
    public int MXIO_ErrorCodes(int r){
        
        int MXIO_OK = 0;
        int ILLEGAL_FUNCTION = 1001;
        int ILLEGAL_DATA_ADDRESS = 1002;
        int ILLEGAL_DATA_VALUE = 1003;
        int SLAVE_DEVICE_FAILURE = 1004;
        int SLAVE_DEVICE_BUSY = 1006;

        int EIO_TIME_OUT = 2001;
        int EIO_INIT_SOCKETS_FAIL = 2002;
        int EIO_CREATING_SOCKET_ERROR = 2003;
        int EIO_RESPONSE_BAD = 2004;
        int EIO_SOCKET_DISCONNECT = 2005;
        int PROTOCOL_TYPE_ERROR = 2006;
        int EIO_PASSWORD_INCORRECT = 2007;

        int SIO_OPEN_FAIL = 3001;
        int SIO_TIME_OUT = 3002;
        int SIO_CLOSE_FAIL = 3003;
        int SIO_PURGE_COMM_FAIL = 3004;
        int SIO_FLUSH_FILE_BUFFERS_FAIL = 3005;
        int SIO_GET_COMM_STATE_FAIL = 3006;
        int SIO_SET_COMM_STATE_FAIL = 3007;
        int SIO_SETUP_COMM_FAIL = 3008;
        int SIO_SET_COMM_TIME_OUT_FAIL = 3009;
        int SIO_CLEAR_COMM_FAIL = 3010;
        int SIO_RESPONSE_BAD = 3011;
        int SIO_TRANSMISSION_MODE_ERROR = 3012;
        int SIO_BAUDRATE_NOT_SUPPORT = 3013;

        int PRODUCT_NOT_SUPPORT = 4001;
        int HANDLE_ERROR = 4002;
        int SLOT_OUT_OF_RANGE = 4003;
        int CHANNEL_OUT_OF_RANGE = 4004;
        int COIL_TYPE_ERROR = 4005;
        int REGISTER_TYPE_ERROR = 4006;
        int FUNCTION_NOT_SUPPORT = 4007;
        int OUTPUT_VALUE_OUT_OF_RANGE = 4008;
        int INPUT_VALUE_OUT_OF_RANGE = 4009;
        int SLOT_NOT_EXIST = 4010;
        int FIRMWARE_NOT_SUPPORT = 4011;
        int CREATE_MUTEX_FAIL = 4012;
        int ENUM_NET_INTERFACE_FAIL = 5000;
        int ADD_INFO_TABLE_FAIL = 5001;
        int UNKNOWN_NET_INTERFACE_FAIL = 5002;
        int TABLE_NET_INTERFACE_FAIL = 5003;
       
        return r;
    }
    
    
    public int getDLLVersion(){
        return lib.MXIO_GetDllVersion();
    }
    
    public int getDllBuildDate(){
        return lib.MXIO_GetDllBuildDate();
    }
    
    public int getModuleType(int connection){
        IntByReference refHandle = new IntByReference();
        
        lib.MXIO_GetModuleType(connection, (byte) 0, refHandle);
        int val = refHandle.getValue();
        return val;
    }
    
    public int connectToDevice(String IP, String password){
        //returns the handle id;
        byte[] arr = IP.getBytes();                     
        byte[] bytPass = password.getBytes();
        IntByReference refHandle = new IntByReference();
        
        lib.MXEIO_E1K_Connect(arr, port, TIMEOUT, refHandle, bytPass);
        int val = refHandle.getValue();
        return val;
    }
    
    // disconnects device handle so it cant be reused....
    public void disconnect(int connection){
        lib.MXEIO_Disconnect(connection);
    }
    
    public int checkConnection(int connection){
        ByteByReference bytRef = new ByteByReference();
        
        lib.MXEIO_CheckConnection(connection, TIMEOUT, bytRef);
        int val = bytRef.getValue();
        return val;
    }
    
    // read DI values, return int value
    public int readDI(int connection, byte channel, byte count){
        IntByReference refValue = new IntByReference();
        lib.E1K_DI_Reads(connection, channel, count, refValue);
        int value = refValue.getValue();
        return value;
    }
    
    /*public int ReadDIS(int connection, byte slot, byte channel){
        IntByReference refValue = new IntByReference();
        lib.DI_Reads(connection, 0, channel, 0, refValue);
        int value = refValue.getValue();
        return value;
    }*/
    
    public int readOutput(int connection, byte slot, byte channel){
        IntByReference refValue = new IntByReference();
        lib.E1K_DO_Reads(connection, slot, channel, refValue);
        int value = refValue.getValue();
        return value;
    }
    
    public int writeOutput(int connection, byte channel, byte count, int value){
        int val = lib.E1K_DO_Writes(connection, channel, count, value);
        return val;
    }
    
    // analog inputs
    public double readAnalogChannel(int connection, byte channel, byte count){
        DoubleByReference refValue = new DoubleByReference();
        lib.E1K_AI_Reads(connection, channel, count, refValue);
        double value = refValue.getValue();
        return value;
        
    }
    
    
    /********* DIGITAL OUTPUT ********************************************************/
    // 0 - D/O mode
    // 1 - pulse mode
    public int setDOMode(int connection, byte channel, byte count, int val){
        IntByReference refValue = new IntByReference();
        lib.E1K_DO_SetModes(connection, channel, count, val);
        int value = refValue.getValue();
        return value;
    }
    /*********************************************************************************/
    
    
    
    /********* PULSE OUTPUTS *********************************************************/
    public int writePulseOutput(int connection, byte channel, byte count, int value){
        int val = lib.E1K_Pulse_SetStartStatuses(connection, channel, count, value);
        return val;
    }
    public int setPulseOutput(int connection, byte channel, byte count, int onWidth, int offWidth){
        int val = lib.E1K_Pulse_SetSignalWidths(connection, channel, count, onWidth, offWidth);
        return val;
    }
    
    /*********************************************************************************/
    
    
    public void initDevice(){
        lib.MXEIO_Init();
    }
    
    public void exitDevice(){
        lib.MXEIO_Exit();
    }
    
    
    
    
    
}
